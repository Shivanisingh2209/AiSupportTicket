package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.TicketMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketMessageRepository extends MongoRepository<TicketMessage, String> {

    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(String ticketId);
}
