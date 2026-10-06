package com.backendwork.backendApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_logs")
public class ChatLog {

    @Id
    private String id;
    private String conversationId;
    private String role;      // "user" ya "assistant"
    private String text;
    private Instant createdAt;

    public ChatLog() {}

    public ChatLog(String conversationId, String role, String text) {
        this.conversationId = conversationId;
        this.role = role;
        this.text = text;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}
