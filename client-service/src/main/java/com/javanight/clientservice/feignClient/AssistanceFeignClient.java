package com.javanight.clientservice.feignClient;

import com.javanight.clientservice.model.Assistance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "assistance-service", url = "http://localhost:8081", path = "/assistance")
public interface AssistanceFeignClient {

    @GetMapping
    List<Assistance> getAllAssistance();

    @GetMapping
    void generateTreeOfRequests(@RequestParam(value = "depth", required = false) Integer depth);
}
