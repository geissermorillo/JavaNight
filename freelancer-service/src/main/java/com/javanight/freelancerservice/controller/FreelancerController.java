package com.javanight.freelancerservice.controller;

import com.javanight.freelancerservice.entity.Freelancer;
import com.javanight.freelancerservice.service.FreelancerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/freelancer")
public class FreelancerController {

    private final FreelancerService freelancerService;

    public FreelancerController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }


    @PostMapping()
    public ResponseEntity<Freelancer> createFreelancer(@RequestBody Freelancer freelancer) {
        return ResponseEntity.ok(this.freelancerService.createFreelancer(freelancer));
    }

    @GetMapping
    public ResponseEntity<List<Freelancer>> getAllFreelancers() {
        log.info("Returning all available freelancers");
        return ResponseEntity.ok(this.freelancerService.getAllFreelancers());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Freelancer> getFreelancerById(@PathVariable("id") int id) {
        log.info("Returning freelancer");
        return ResponseEntity.ok(this.freelancerService.getFreelancerById(id));
    }

    @GetMapping("/getFreelancersByProfession/{profession}")
    public ResponseEntity<List<Freelancer>> getFreelancersByProfession(@PathVariable("profession") String profession) {
        return ResponseEntity.ok(this.freelancerService.getFreelancersByProfession(profession));
    }
}
