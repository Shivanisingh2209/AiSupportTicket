package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

import java.util.Optional;

public interface AgentRepo extends MongoRepository<Agent, String> {

    Optional<Agent> findByEmail(String email);

    Optional<Agent> findFirstByStatus(String status);

    List<Agent> findByStatus(String status);

}
