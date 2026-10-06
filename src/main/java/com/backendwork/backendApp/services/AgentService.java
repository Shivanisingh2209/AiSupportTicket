package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Agent;
import com.backendwork.backendApp.repository.AgentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backendwork.backendApp.repository.TicketRepository;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TicketRepository ticketRepository;

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

        Agent agent = agentRepo.findByEmail(email).orElse(null);

        if (agent == null) {
            System.out.println("LOGIN DEBUG: no agent found for email [" + email + "]");
            throw new RuntimeException("Invalid email or password");
        }

        String stored = agent.getPassword();
        boolean ok = stored != null && passwordEncoder.matches(password, stored);

        System.out.println("LOGIN DEBUG: agent found, hash prefix ["
                + (stored == null ? "null" : stored.substring(0, Math.min(7, stored.length())))
                + "], encoder=" + passwordEncoder.getClass().getSimpleName()
                + ", matches=" + ok);

        if (!ok) {
            throw new RuntimeException("Invalid email or password");
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

    public Agent getLeastLoadedAvailableAgent() {

        List<Agent> availableAgents =
                agentRepo.findByStatus("AVAILABLE");

        if (availableAgents.isEmpty()) {
            throw new RuntimeException(
                    "No available agent found"
            );
        }

        List<String> activeStatuses =
                List.of("OPEN", "IN_PROGRESS");

        return availableAgents.stream()
                .min((agent1, agent2) -> {

                    long count1 =
                            ticketRepository
                                    .countByAgentIdAndStatusIn(
                                            agent1.getId(),
                                            activeStatuses
                                    );

                    long count2 =
                            ticketRepository
                                    .countByAgentIdAndStatusIn(
                                            agent2.getId(),
                                            activeStatuses
                                    );

                    return Long.compare(count1, count2);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "No available agent found"
                        )
                );
    }

}
