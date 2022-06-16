package com.javanight.clientservice.service;

import com.javanight.clientservice.entity.Client;
import com.javanight.clientservice.model.Assistance;
import com.javanight.clientservice.model.Freelancer;
import com.javanight.clientservice.repository.IClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService implements IClientService {
    private final IClientRepository clientRepository;
    private final RestTemplate restTemplate;

    private final String ASSISTANCE_SERVICE_URL = "http://localhost:8081/assistance";
    private final String FREELANCER_SERVICE_URL = "http://localhost:8082/freelancer";

    public ClientService(IClientRepository clientRepository, RestTemplate restTemplate) {
        this.clientRepository = clientRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public Client createClient(Client client) {
        return this.clientRepository.save(client);
    }

    @Override
    public Client getClientById(int id) {
        return this.clientRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    @Override
    public List<Client> getAllClients() {
        return this.clientRepository.findAll();
    }

    @Override
    public List<Assistance> getAllAssistanceAvailable() {
        return this.restTemplate.getForObject(ASSISTANCE_SERVICE_URL, List.class);
    }

    @Override
    public List<Freelancer> getAllFreelancers() {
        return this.restTemplate.getForObject(FREELANCER_SERVICE_URL, List.class);
    }

    @Override
    public Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId) {
        String url = ASSISTANCE_SERVICE_URL + "/getFreelancerRankingByAssistanceId" + Optional.ofNullable(assistanceId).map(id -> "?"+id).orElse("");
        return this.restTemplate.getForObject(url, Map.class);
    }
}
