package com.javanight.clientservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "freelancer-service", url = "http://localhost:8082", path = "/freelancer")
public interface FreelancerFeignClient {

    @GetMapping
    void generateTreeOfRequests(@RequestParam(value = "depth", required = false) Integer depth);
}
