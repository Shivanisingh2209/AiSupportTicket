package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findByCustomerEmail(String customerEmail);

    List<Ticket> findByStatus(String status);

    List<Ticket> findByPriority(String priority);

    List<Ticket> findByCustomerId(String customerId);

    List<Ticket> findByAgentId(String agentId);

    long countByStatus(String status);

    long countByAgentId(String agentId);

}