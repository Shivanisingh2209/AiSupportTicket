package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AgentRepo extends MongoRepository<Agent, String> {

    Optional<Agent> findByEmail(String email);
}
