package com.javanight.assistanceservice.service;

import com.javanight.assistanceservice.entity.Assistance;
import com.javanight.assistanceservice.model.Freelancer;
import com.javanight.assistanceservice.repository.IAssistanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

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

    private static final String[] callables = new String[] { "createAssistance", "getAssistanceById", "getAllAssistance",
            "getFreelancerRankingByAssistanceId", "freelancerGenerateTreeOfRequests"
    };



    @Override
    public Assistance createAssistance(Assistance assistance) {
        log.info("Saving assistance {}", assistance.getId());
        return this.assistanceRepository.save(assistance);
    }

    @Override
    public Assistance getAssistanceById(int id) {
        log.info("Getting assistance {}", id);
        return this.assistanceRepository.findById(Integer.valueOf(id)).orElse(null);
    }

    @Override
    public List<Assistance> getAllAssistance() {
        log.info("Getting all assistance");
        return this.assistanceRepository.findAll();
    }

    @Override
    public Map<String, TreeSet<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId) {
        log.info("Getting freelancers ranked grouped by assistance {}", assistanceId);
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

    @Override
    public void freelancerGenerateTreeOfRequests(Integer depth)  {

    }

    @Override
    public void generateTreeOfRequests(Integer depth) throws Exception {
        if (depth <= 0) {
            throw new Exception("Depth param must be greater then zero");
        }

        int randomIndex = Double.valueOf(Math.random() * callables.length).intValue();
        this.callMethodByName(callables[randomIndex], depth);

        depth--;
        if (depth > 0) {
            generateTreeOfRequests(depth);
        }
    }

    private void callMethodByName(String methodName, Integer depth) throws Exception {
        switch (methodName) {
            case "createAssistance":
                int assistanceId = this.assistanceRepository.findAll().size() + 1;
                this.createAssistance(new Assistance(assistanceId, String.format("assistance-%s", assistanceId),
                        String.format("description for assistance %s", assistanceId)));
                break;
            case "getAssistanceById":
                this.getAssistanceById(getIdRandomId());
                break;
            case "getAllAssistance":
                this.getAllAssistance();
                break;
            case "getFreelancerRankingByAssistanceId":
                this.getFreelancerRankingByAssistanceId(null);
                break;
            case "generateTreeOfRequests":
                this.generateTreeOfRequests(depth);
                break;
        }
    }

    private int getIdRandomId() {
        int maxAssistanceId = this.assistanceRepository.findAll().stream().mapToInt(Assistance::getId).max().orElse(1);
        return Double.valueOf(Math.random() * maxAssistanceId).intValue();
    }


    private Map<String, TreeSet<Freelancer>> mapFreelancersByProfession(List<Freelancer> freelancers) {
        Map<String, TreeSet<Freelancer>> freelancersByProfession = new HashMap<>();
        for (Freelancer freelancer : freelancers) {
            TreeSet<Freelancer> freelancerList = Optional.ofNullable(freelancersByProfession.get(freelancer.getProfession()))
                    .orElse(new TreeSet<>(new Comparator<Freelancer>() {
                        @Override
                        public int compare(Freelancer freelancer1, Freelancer freelancer2) {
                            return freelancer1.getRate() > freelancer2.getRate() ? -1 : 0;
                        }
                    }));
            if(CollectionUtils.isEmpty(freelancerList)) {
                freelancersByProfession.put(freelancer.getProfession(), freelancerList);
            }

            freelancerList.add(freelancer);
        }

        return freelancersByProfession;
    }
}
