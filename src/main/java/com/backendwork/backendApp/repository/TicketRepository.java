package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {
}