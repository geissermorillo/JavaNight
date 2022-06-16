package com.javanight.freelancerservice.service;

import com.javanight.freelancerservice.entity.Freelancer;
import com.javanight.freelancerservice.repository.IFreelancerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FreelancerService implements IFreelancerService {

    private final IFreelancerRepository freelancerRepository;

    public FreelancerService(IFreelancerRepository freelancerRepository, RestTemplate restTemplate) {
        this.freelancerRepository = freelancerRepository;
    }

    @Override
    public Freelancer createFreelancer(Freelancer freelancer) {
        return this.freelancerRepository.save(freelancer);
    }

    @Override
    public Freelancer getFreelancerById(int id) {
        return this.freelancerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Freelancer> getAllFreelancers() {
        return this.freelancerRepository.findAll();
    }

    @Override
    public List<Freelancer> getFreelancersByProfession(String profession) {
        return this.freelancerRepository.getFreelancersByProfession(profession);
    }
}
