package com.javanight.freelancerservice.service;

import com.javanight.freelancerservice.entity.Freelancer;

import java.util.List;
import java.util.Map;

public interface IFreelancerService {
    Freelancer createFreelancer(Freelancer freelancer);

    Freelancer getFreelancerById(int id);

    List<Freelancer> getAllFreelancers();

    List<Freelancer> getFreelancersByProfession(String profession);

    void generateTreeOfRequests(Integer depth) throws Exception;
}
