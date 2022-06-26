package com.javanight.clientservice.handler;

import brave.Span;
import brave.Tracer;
import com.javanight.clientservice.annotations.EnableTracing;
import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class LogInvocationHandler implements InvocationHandler {
    private Tracer tracer;

    private Logger proxied;

    public LogInvocationHandler(Logger proxied, Tracer tracer) {
        this.tracer = tracer;
        this.proxied = proxied;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String enclosingMethodName = this.getEnclosingMethodName();
        Class<?> parentClass = Class.forName(proxied.getName());
        Method enclosingMethod = Arrays.stream(parentClass.getDeclaredMethods())
                .filter(method1 -> method1.getName().equals(enclosingMethodName))
                .findFirst()
                .orElse(null);

        if (parentClass.isAnnotationPresent(EnableTracing.class) ||
                enclosingMethod.isAnnotationPresent(EnableTracing.class)) {

            Span span = this.tracer.nextSpan();
            try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
                span.name(enclosingMethodName);
                span.tag("InvokeFrom", "LogInvocationHandler");
                span.tag("tag", "myTag");
                return method.invoke(proxied, args);
            } finally {
                if(this.isEnabledToTrace(parentClass, enclosingMethod)) {
                    span.finish();
                } else {
                    span.abandon();
                }
            }
        }
       return method.invoke(proxied, args);
    }

    private boolean isEnabledToTrace(Class<?> parentClass,  Method method) {
        EnableTracing classAnnotation = Optional.ofNullable(parentClass)
                .map(annotation -> annotation.getAnnotation(EnableTracing.class))
                .orElse(null);
        
        EnableTracing methodAnnotation = Optional.ofNullable(method)
                .map(annotation -> annotation.getAnnotation(EnableTracing.class))
                .orElse(null);

        boolean enabledTracing = Optional.ofNullable(methodAnnotation)
                .map(EnableTracing::value)
                .orElse(
                    Optional.ofNullable(classAnnotation).map(EnableTracing::value).orElse(false)
                );

        return enabledTracing;
    }

    private String getEnclosingMethodName() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .filter(stackTraceElement -> this.proxied.getName().equals(stackTraceElement.getClassName()))
                .map(StackTraceElement::getMethodName)
                .findFirst()
                .orElse(null);
    }
}
