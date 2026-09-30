package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.LoginRequest;
import com.backendwork.backendApp.entity.Agent;
import com.backendwork.backendApp.services.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping
    public ResponseEntity<Agent> createAgent(
            @RequestBody Agent agent
    ) {

        Agent createdAgent =
                agentService.createAgent(agent);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAgent);
    }

    @GetMapping
    public ResponseEntity<List<Agent>> getAllAgents() {

        List<Agent> agents =
                agentService.getAllAgents();

        return ResponseEntity.ok(agents);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {

        agentService.resetPassword(email, newPassword);

        return ResponseEntity.ok(
                "Agent password updated successfully"
        );
    }
}
