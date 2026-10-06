package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatLogRepository extends MongoRepository<ChatLog, String> {

    List<ChatLog> findTop20ByConversationIdOrderByCreatedAtDesc(String conversationId);
}