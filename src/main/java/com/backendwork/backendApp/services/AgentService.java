package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Agent;
import com.backendwork.backendApp.repository.AgentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Agent createAgent(Agent agent) {

        if(agent.getStatus() == null) {
            agent.setStatus("AVAILABLE");
        }

        if (agent.getPassword() != null) {
            agent.setPassword(
                    passwordEncoder.encode(agent.getPassword())
            );
        }

        return agentRepo.save(agent);
    }

    public List<Agent> getAllAgents() {
        return agentRepo.findAll();
    }

    public boolean agentExists(String agentId) {
        return agentRepo.existsById(agentId);
    }

    public Agent getAgentByEmail(String email) {

        return agentRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Agent not found with email: " + email
                        )
                );
    }

    public String login(String email, String password) {

        Agent agent = agentRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                password,
                agent.getPassword()
        )) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return jwtService.generateToken(
                agent.getEmail(),
                "AGENT"
        );
    }

    public void resetPassword(
            String email,
            String newPassword
    ) {

        Agent agent = agentRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Agent not found with email: " + email
                        )
                );

        agent.setPassword(
                passwordEncoder.encode(newPassword)
        );

        agentRepo.save(agent);
    }

    public Agent updateAgentStatus(String agentId, String status) {

        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Agent not found with id: " + agentId
                        ));

        agent.setStatus(status.toUpperCase());

        return agentRepo.save(agent);
    }

    public Agent getAvailableAgent() {
        return agentRepo.findFirstByStatus("AVAILABLE")
                .orElseThrow(() ->
                        new RuntimeException("No available agent found"));
    }

}
