package com.javanight.assistanceservice.service;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.javanight.assistanceservice.entity.Assistance;
import com.javanight.assistanceservice.model.Freelancer;
import com.javanight.assistanceservice.repository.IAssistanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AssistanceService implements IAssistanceService {
    private final IAssistanceRepository assistanceRepository;
    private final RestTemplate restTemplate;

    private final String FREELANCER_SERVICE_URL = "http://localhost:8082/freelancer";

    public AssistanceService(IAssistanceRepository assistanceRepository, RestTemplate restTemplate) {
        this.assistanceRepository = assistanceRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public Assistance createAssistance(Assistance assistance) {
        return this.assistanceRepository.save(assistance);
    }

    @Override
    public Assistance getAssistanceById(int id) {
        return this.assistanceRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    @Override
    public List<Assistance> getAllAssistance() {
        return this.assistanceRepository.findAll();
    }

    @Override
    public Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId) {
        String url = FREELANCER_SERVICE_URL +
                Optional.ofNullable(assistanceId).map(id -> "/getFreelancersByProfession/%s").orElse("");

        if (assistanceId != null) {
            Assistance assistance = this.assistanceRepository.findById(assistanceId).orElse(null);
            url = String.format(url, assistance.getName());
        }

        ParameterizedTypeReference<List<Freelancer>> typeRef = new ParameterizedTypeReference<List<Freelancer>>() {};



        List<Freelancer> freelancers = this.restTemplate.exchange(url, HttpMethod.GET, null, typeRef).getBody();
        return this.mapFreelancersByProfession(freelancers);
    }

    private Map<String, List<Freelancer>> mapFreelancersByProfession(List<Freelancer> freelancers) {
        Map<String, List<Freelancer>> freelancersByProfession = new HashMap<>();
        for (Freelancer freelancer : freelancers) {
            List<Freelancer> freelancerList = Optional.ofNullable(freelancersByProfession.get(freelancer.getProfession())).orElse(new ArrayList<>());
            if(CollectionUtils.isEmpty(freelancerList)) {
                freelancersByProfession.put(freelancer.getProfession(), freelancerList);
            }

            freelancerList.add(freelancer);
        }

        return freelancersByProfession;
    }
}
