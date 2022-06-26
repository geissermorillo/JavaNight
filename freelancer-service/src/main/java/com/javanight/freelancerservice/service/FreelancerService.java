package com.javanight.freelancerservice.service;

import com.javanight.freelancerservice.entity.Freelancer;
import com.javanight.freelancerservice.repository.IFreelancerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class FreelancerService implements IFreelancerService {

    private final IFreelancerRepository freelancerRepository;

    public FreelancerService(IFreelancerRepository freelancerRepository) {
        this.freelancerRepository = freelancerRepository;
    }

    private static final String[] callables = new String[] { "createFreelancer", "getFreelancerById", "getAllFreelancers",
            "getFreelancersByProfession"
    };


    @Override
    public Freelancer createFreelancer(Freelancer freelancer) {
        log.info("Saving freelancer {}", freelancer.getId());
        return this.freelancerRepository.save(freelancer);
    }

    @Override
    public Freelancer getFreelancerById(int id) {
        log.info("Getting freelancer {}", id);
        return this.freelancerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Freelancer> getAllFreelancers() {
        log.info("Getting all freelancers");
        return this.freelancerRepository.findAll();
    }

    @Override
    public List<Freelancer> getFreelancersByProfession(String profession) {
        log.info("Getting all freelancers by profession {}", profession);
        return this.freelancerRepository.getFreelancersByProfession(profession);
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
            case "createFreelancer":
                int freelancerId = this.freelancerRepository.findAll().size() + 1;
                int randomRate = Double.valueOf(Math.random() * callables.length).intValue();
                this.createFreelancer(
                        new Freelancer(freelancerId, randomRate,
                                String.format("freelancer-%s", freelancerId),
                                String.format("description for assistance %s", freelancerId),
                                String.format("profession-%s", freelancerId))
                );
                break;
            case "getFreelancerById":
                this.getFreelancerById(getIdRandomId());
                break;
            case "getAllFreelancers":
                this.getAllFreelancers();
                break;
            case "getFreelancersByProfession":
                this.getFreelancersByProfession("Masonry");
                break;
            case "generateTreeOfRequests":
                this.generateTreeOfRequests(depth);
                break;
        }
    }

    private int getIdRandomId() {
        int maxAssistanceId = this.freelancerRepository.findAll().stream().mapToInt(Freelancer::getId).max().orElse(1);
        return Double.valueOf(Math.random() * maxAssistanceId).intValue();
    }

}
