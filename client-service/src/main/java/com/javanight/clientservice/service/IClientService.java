package com.javanight.clientservice.service;

import com.javanight.clientservice.entity.Client;
import com.javanight.clientservice.model.Assistance;
import com.javanight.clientservice.model.Freelancer;

import java.util.List;
import java.util.Map;

public interface IClientService {

    Client createClient(Client client);

    Client getClientById(int id);

    List<Client> getAllClients();

    List<Assistance> getAllAssistanceAvailable();

    List<Freelancer> getAllFreelancers();

    Map<String, List<Freelancer>> getFreelancerRankingByAssistanceId(Integer assistanceId);
}
