package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Agent;
import com.backendwork.backendApp.repository.AgentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    private AgentRepo agentRepo;

    public Agent createAgent(Agent agent) {

        if(agent.getStatus() == null) {
            agent.setStatus("AVAILABLE");
        }
        return agentRepo.save(agent);
    }

    public List<Agent> getAllAgents() {
        return agentRepo.findAll();
    }

    public boolean agentExists(String agentId) {
        return agentRepo.existsById(agentId);
    }

}
