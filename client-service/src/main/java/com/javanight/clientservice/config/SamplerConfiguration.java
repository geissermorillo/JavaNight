package com.javanight.clientservice.config;

import com.javanight.clientservice.interceptor.EnableTracingInterceptor;
import com.javanight.clientservice.samplers.CustomHttpRequestSamplerFunction;
import org.springframework.cloud.sleuth.SamplerFunction;
import org.springframework.cloud.sleuth.http.HttpRequest;
import org.springframework.cloud.sleuth.instrument.web.HttpServerSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class SamplerConfiguration {
    @Bean(name = HttpServerSampler.NAME)
    public SamplerFunction<HttpRequest> customHttpRequestSamplerFunction() {
        return new CustomHttpRequestSamplerFunction();
    }
    
    @Bean
    public EnableTracingInterceptor enableTracingInterceptor() {
        return new EnableTracingInterceptor();
    }


}
