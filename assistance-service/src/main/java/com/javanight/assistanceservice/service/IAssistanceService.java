package com.javanight.assistanceservice.service;

import com.javanight.assistanceservice.entity.Assistance;
import com.javanight.assistanceservice.model.Freelancer;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface IAssistanceService {
    Assistance createAssistance(Assistance assistance);

    Assistance getAssistanceById(int id);

    List<Assistance> getAllAssistance();

    Map<String, TreeSet<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId);

    void generateTreeOfRequests(Integer depth) throws Exception;

    void freelancerGenerateTreeOfRequests(Integer depth);
}
