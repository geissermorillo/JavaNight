package com.javanight.clientservice.samplers;

import org.springframework.cloud.sleuth.SamplerFunction;
import org.springframework.cloud.sleuth.http.HttpRequest;

public class CustomHttpRequestSamplerFunction implements SamplerFunction<HttpRequest> {
    @Override
    public Boolean trySample(HttpRequest request) {
        request.url();
        return !"/client/getAllAssistanceAvailable".equals(request.path());
    }
}
