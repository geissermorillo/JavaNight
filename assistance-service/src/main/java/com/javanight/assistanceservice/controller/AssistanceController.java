package com.javanight.assistanceservice.controller;

import com.javanight.assistanceservice.entity.Assistance;
import com.javanight.assistanceservice.model.Freelancer;
import com.javanight.assistanceservice.service.AssistanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/assistance")
public class AssistanceController {

    private final AssistanceService assistanceService;

    public AssistanceController(AssistanceService assistanceService) {
        this.assistanceService = assistanceService;
    }

    @PostMapping()
    public ResponseEntity<Assistance> createAssistance(@RequestBody Assistance assistance) {
        return ResponseEntity.ok(this.assistanceService.createAssistance(assistance));
    }

    @GetMapping
    public ResponseEntity<List<Assistance>> getAllAssistance() {
        log.info("Returning all available assistance");
        return ResponseEntity.ok(this.assistanceService.getAllAssistance());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Assistance> getAssistanceById(@PathVariable("id") int id) {
        return ResponseEntity.ok(this.assistanceService.getAssistanceById(id));
    }


    @GetMapping("/getFreelancerRankingByAssistanceId")
    public ResponseEntity<Map<String, List<Freelancer>>> getFreelancerRankingByAssistanceId(@RequestParam(value = "professionId", required = false) Integer assistanceId) {
        log.info("returning all freelancers ranked by profession");
        return ResponseEntity.ok(this.assistanceService.getFreelancerRankingByAssistanceId(assistanceId));
    }
}
