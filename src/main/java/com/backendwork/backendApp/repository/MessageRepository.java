package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByTicketIdOrderByCreatedAtAsc(String ticketId);
}