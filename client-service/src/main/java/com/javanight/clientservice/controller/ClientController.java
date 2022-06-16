package com.javanight.clientservice.controller;

import com.javanight.clientservice.entity.Client;
import com.javanight.clientservice.model.Assistance;
import com.javanight.clientservice.model.Freelancer;
import com.javanight.clientservice.service.ClientService;
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
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;


    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping()
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.ok(this.clientService.createClient(client));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable("id") int id) {
        return ResponseEntity.ok(this.clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(this.clientService.getAllClients());
    }

    @GetMapping("/getAllAssistanceAvailable")
    public ResponseEntity<List<Assistance>> getAllAssistance() {
        log.info("Getting all available assistance");
        return ResponseEntity.ok(this.clientService.getAllAssistanceAvailable());
    }

    @GetMapping("/getAllFreelancers")
    public ResponseEntity<List<Freelancer>> getAllFreelancers() {
        log.info("Getting all available freelancers");
        return ResponseEntity.ok(this.clientService.getAllFreelancers());
    }

    @GetMapping("/getFreelancerRankingByAssistanceId")
    public ResponseEntity<Map<String, List<Freelancer>>> getFreelancerRankingByAssistanceId(@RequestParam(value = "professionId", required = false) Integer assistanceId)  {
        log.info("Getting all freelancers ranked by profession");
        return ResponseEntity.ok(this.clientService.getFreelancerRankingByAssistanceId(assistanceId));
    }
}
