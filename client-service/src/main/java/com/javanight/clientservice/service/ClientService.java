package com.javanight.clientservice.service;

import brave.Tracer;
import com.javanight.clientservice.annotations.EnableTracing;
import com.javanight.clientservice.entity.Client;
import com.javanight.clientservice.feignClient.AssistanceFeignClient;
import com.javanight.clientservice.feignClient.FreelancerFeignClient;
import com.javanight.clientservice.model.Assistance;
import com.javanight.clientservice.model.Freelancer;
import com.javanight.clientservice.repository.IClientRepository;
import com.javanight.clientservice.utils.ProxyInstanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EnableTracing(value = true)
@Service
@Slf4j
public class ClientService implements IClientService {
    private final IClientRepository clientRepository;
    private final RestTemplate restTemplate;
    private final AssistanceFeignClient assistanceFeignClient;
    private final FreelancerFeignClient freelancerFeignClient;
    private final Tracer tracer;
    private final ProxyInstanceUtils proxyInstanceUtils;


    private final String ASSISTANCE_SERVICE_URL = "http://localhost:8081/assistance";
    private final String FREELANCER_SERVICE_URL = "http://localhost:8082/freelancer";

    private static final String[] callables = new String[] { "createClient", "getClientById", "getAllClients",
            "getAllAssistanceAvailable", "getAllFreelancers", "getFreelancerRankingByAssistanceId",
            "assistanceGenerateTreeOfRequests", "freelancerGenerateTreeOfRequests"
    };

    private Logger proxiedLog;

    public ClientService(IClientRepository clientRepository, RestTemplate restTemplate,
                         AssistanceFeignClient assistanceFeignClient,
                         FreelancerFeignClient freelancerFeignClient, Tracer tracer, ProxyInstanceUtils proxyInstanceUtils) {
        this.clientRepository = clientRepository;
        this.restTemplate = restTemplate;
        this.assistanceFeignClient = assistanceFeignClient;
        this.freelancerFeignClient = freelancerFeignClient;
        this.tracer = tracer;
        this.proxyInstanceUtils = proxyInstanceUtils;
    }

    @PostConstruct
    private void init() {
        proxiedLog = this.proxyInstanceUtils.getLoggerProxyInstance(log, tracer);
    }

    @Override
    @EnableTracing
    public Client createClient(Client client) {
        proxiedLog.info("Saving client {}", client.getName());
        return this.clientRepository.save(client);
    }

    @Override
    public Client getClientById(int id) {
        proxiedLog.info("Getting client {}", id);
        return this.clientRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    @Override
    public List<Client> getAllClients() {
        proxiedLog.info("Getting all clients");
        return this.clientRepository.findAll();
    }

    @Override
    public List<Assistance> getAllAssistanceAvailable() {
        proxiedLog.info("Getting all assistance available");
//        return this.restTemplate.getForObject(ASSISTANCE_SERVICE_URL, List.class);
        return this.assistanceFeignClient.getAllAssistance();
    }

    @Override
    public List<Freelancer> getAllFreelancers() {
        proxiedLog.info("Getting all freelancers available");
        return this.restTemplate.getForObject(FREELANCER_SERVICE_URL, List.class);
    }

    @Override
    public Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId) {
        proxiedLog.info("Getting freelancers ranked grouped by assistance <{}>", assistanceId);
        String url = ASSISTANCE_SERVICE_URL + "/getFreelancerRankedByAssistanceId" + Optional.ofNullable(assistanceId).map(id -> "?"+id).orElse("");
        return this.restTemplate.getForObject(url, Map.class);
    }

    @Override
    public void generateTreeOfRequests(Integer depth) throws Exception {
        proxiedLog.info("Generating requests tree with depth {}", depth);
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
