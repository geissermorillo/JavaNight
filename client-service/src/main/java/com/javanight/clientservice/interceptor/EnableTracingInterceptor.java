package com.javanight.clientservice.interceptor;

import brave.Span;
import brave.Tracer;
import com.javanight.clientservice.annotations.EnableTracing;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
public class EnableTracingInterceptor {

    @Autowired
    private Tracer tracer;

    @Pointcut(value = "@annotation(com.javanight.clientservice.annotations.EnableTracing)")
    void annotatedMethod() {}

//    @Pointcut(value = "execution(* (@com.javanight.clientservice.annotations.EnableTracing *).*(..))")
//    void methodOfAnnotatedClass() {}

    @Around(value = "annotatedMethod() && @annotation(enableTracingAtMethodLevel)")
    public Object adviseAnnotatedMethods(ProceedingJoinPoint proceedingJoinPoint, EnableTracing enableTracingAtMethodLevel) throws Throwable {
        return  this.trySample(proceedingJoinPoint, enableTracingAtMethodLevel);
    }

//    @Around(value = "methodOfAnnotatedClass() && !annotatedMethod() && @within(enableTracingAtClassLevel)")
//    public Object adviseMethodsOfAnnotatedClass(ProceedingJoinPoint proceedingJoinPoint, EnableTracing enableTracingAtClassLevel)
//            throws Throwable {
//        return this.trySample(proceedingJoinPoint, enableTracingAtClassLevel);
//    }


    public Object trySample(ProceedingJoinPoint proceedingJoinPoint, EnableTracing enableTracing) throws Throwable {
        Object result = null;
        if (enableTracing != null) {
            Span span = this.tracer.nextSpan();
            try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
                // Adding default tags
                this.addDefaultTags(span, proceedingJoinPoint);
                // Adding custom tags from  annotation
                this.addTagFromAnnotation(span, enableTracing);
                result = proceedingJoinPoint.proceed();
            } finally {
                if(this.isEnabledToTrace(enableTracing)) {
                    span.finish();
//                    span.flush();
                } else {
                    span.abandon();
                }
            }
        }

        return result;
    }

    private String getParametersJoinedFromInvocation(Object[] args) {
        return Arrays
                .stream(args)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
    private void addDefaultTags(Span span, ProceedingJoinPoint proceedingJoinPoint) {
        String joinedParameters = this.getParametersJoinedFromInvocation(proceedingJoinPoint.getArgs());
        span.name(proceedingJoinPoint.getSignature().getName());
        span.tag("parameters", joinedParameters);
        span.kind(Span.Kind.PRODUCER);
    }

    private void addTagFromAnnotation(Span span, EnableTracing enableTracing) {
        String[] tagsStr = Optional.ofNullable(enableTracing).map(EnableTracing::tags).orElse(new String[]{});
        if (tagsStr.length > 0) {
            Arrays.stream(tagsStr).forEach(tag -> {
                String[] tagParts = tag.split(":");
                if(tagParts.length == 2) {
                    span.tag(tagParts[0].trim(), tagParts[1].trim());
                }

            });
        }
    }

    private boolean isEnabledToTrace(EnableTracing enableTracing) {
        boolean enabledTracing = Optional.ofNullable(enableTracing)
                .map(EnableTracing::enabled)
                .orElse(false);

        return enabledTracing;
    }
}
