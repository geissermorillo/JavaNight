package com.javanight.clientservice.service;

import brave.Tracer;
import com.javanight.clientservice.annotations.EnableTracing;
import com.javanight.clientservice.entity.Client;
import com.javanight.clientservice.feignClient.AssistanceFeignClient;
import com.javanight.clientservice.feignClient.FreelancerFeignClient;
import com.javanight.clientservice.model.Assistance;
import com.javanight.clientservice.model.Freelancer;
import com.javanight.clientservice.repository.IClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@EnableTracing(enabled = false)
@Service
@Slf4j
public class ClientService implements IClientService {
    private final IClientRepository clientRepository;
    private final RestTemplate restTemplate;
    private final AssistanceFeignClient assistanceFeignClient;
    private final FreelancerFeignClient freelancerFeignClient;
    private final Tracer tracer;


    private final String ASSISTANCE_SERVICE_URL = "http://localhost:8081/assistance";
    private final String FREELANCER_SERVICE_URL = "http://localhost:8082/freelancer";

    private static final String[] callables = new String[] { "createClient", "getClientById", "getAllClients",
            "getAllAssistanceAvailable", "getAllFreelancers", "getFreelancerRankingByAssistanceId",
            "assistanceGenerateTreeOfRequests", "freelancerGenerateTreeOfRequests"
    };

    private Logger proxiedLog;

    public ClientService(IClientRepository clientRepository, RestTemplate restTemplate,
                         AssistanceFeignClient assistanceFeignClient,
                         FreelancerFeignClient freelancerFeignClient, Tracer tracer) {
        this.clientRepository = clientRepository;
        this.restTemplate = restTemplate;
        this.assistanceFeignClient = assistanceFeignClient;
        this.freelancerFeignClient = freelancerFeignClient;
        this.tracer = tracer;
    }

    @EnableTracing
    @Override
    public Client createClient(@SpanTag("Client Parameter") Client client) {
        log.info("Saving client {}", client.getName());
        return this.clientRepository.save(client);
    }

    @EnableTracing(enabled = true, tags = {"My Tag 1: Tagged by annotation 1", "My Tag 2: Tagged by annotation 2"})
    @Override
    public Client getClientById(int id) {
        log.info("Getting client {}", id);
        return this.clientRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    @EnableTracing(enabled = false)
    @Override
    public List<Client> getAllClients() {
        log.info("Getting all clients");
        return this.clientRepository.findAll();
    }


    @Override
    public List<Assistance> getAllAssistanceAvailable() {
        log.info("Getting all assistance available");
//        return this.restTemplate.getForObject(ASSISTANCE_SERVICE_URL, List.class);
        return this.assistanceFeignClient.getAllAssistance();
    }

    @Override
    public List<Freelancer> getAllFreelancers() {
        log.info("Getting all freelancers available");
        return this.restTemplate.getForObject(FREELANCER_SERVICE_URL, List.class);
    }
    @EnableTracing(tags = "Kind: Producer")
    @Override
    public Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId) {
        log.info("Getting freelancers ranked grouped by assistance <{}>", assistanceId);
        String url = ASSISTANCE_SERVICE_URL + "/getFreelancerRankedByAssistanceId" + Optional.ofNullable(assistanceId).map(id -> "?"+id).orElse("");
        return this.restTemplate.getForObject(url, Map.class);
    }

    @Override
    public void generateTreeOfRequests(Integer depth) throws Exception {
        log.info("Generating requests tree with depth {}", depth);
        if (Optional.ofNullable(depth).orElse(-1) <= 0) {
            throw new Exception("Depth param must be greater then zero");
        }

        depth--;
        int randomIndex = Double.valueOf(Math.random() * callables.length).intValue();
        this.callMethodByName(callables[randomIndex], depth);

        if (depth > 0) {
            generateTreeOfRequests(depth);
        }
    }

    @Override
    public void assistanceGenerateTreeOfRequests(Integer depth) {
        this.assistanceFeignClient.generateTreeOfRequests(depth);
    }

    @Override
    public void freelancerGenerateTreeOfRequests(Integer depth) {
        this.freelancerFeignClient.generateTreeOfRequests(depth);
    }

    private void callMethodByName(String methodName, Integer depth) throws Exception {
        switch (methodName) {
            case "createClient":
                int clientId = this.clientRepository.findAll().size() + 1;
                this.createClient(new Client(clientId, String.format("client-%s", clientId), String.format("client-%s@mail.com", clientId)));
                break;
            case "getClientById":
                this.getClientById(getIdRandomId() + 1);
                break;
            case "getAllClients":
                this.getAllClients();
                break;
            case "getAllAssistanceAvailable":
                this.getAllAssistanceAvailable();
                break;
            case "getAllFreelancers":
                this.getAllFreelancers();
                break;
            case "getFreelancerRankingByAssistanceId":
                this.getFreelancerRankingByAssistanceId(null);
                break;
            case "assistanceGenerateTreeOfRequests":
                this.assistanceGenerateTreeOfRequests(depth);
                break;
            case "generateTreeOfRequests":
                this.generateTreeOfRequests(depth);
                break;
        }
    }

    private int getIdRandomId() {
        int maxClientId = this.clientRepository.findAll().stream().mapToInt(Client::getId).max().orElse(1);
        return Double.valueOf(Math.random() * maxClientId).intValue();
    }
}
