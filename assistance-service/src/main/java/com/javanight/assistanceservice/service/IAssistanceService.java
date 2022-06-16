package com.javanight.assistanceservice.service;

import com.javanight.assistanceservice.entity.Assistance;
import com.javanight.assistanceservice.model.Freelancer;

import java.util.List;
import java.util.Map;

public interface IAssistanceService {
    Assistance createAssistance(Assistance assistance);

    Assistance getAssistanceById(int id);

    List<Assistance> getAllAssistance();

    Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId);
}
