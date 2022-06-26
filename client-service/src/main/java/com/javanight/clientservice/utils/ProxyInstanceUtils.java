package com.javanight.clientservice.utils;

import brave.Tracer;
import com.javanight.clientservice.ClientServiceApplication;
import com.javanight.clientservice.handler.LogInvocationHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
public class ProxyInstanceUtils {

    public Logger getLoggerProxyInstance(Logger logger, Tracer tracer) {
        return (Logger) Proxy.newProxyInstance(
                ClientServiceApplication.class.getClassLoader(),
                new Class[] {Logger.class},
                new LogInvocationHandler(logger, tracer));
    }
}
