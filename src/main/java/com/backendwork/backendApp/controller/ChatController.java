package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.entity.ChatLog;
import com.backendwork.backendApp.repository.ChatLogRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatLogRepository repository;

    public ChatController(ChatClient.Builder builder,
                          ChatLogRepository repository,
                          @Value("classpath:knowledge/faq.txt") Resource faqResource) throws Exception {
        this.repository = repository;
        String faq = faqResource.getContentAsString(StandardCharsets.UTF_8);

        String systemPrompt = """
         You are a customer support assistant for our company.
         Always reply in English, even if the customer writes in another language.
         Keep answers polite, clear and short.

        RULES:
        1. Use ONLY the COMPANY INFORMATION below to answer.
        2. If the answer is not clearly written in the COMPANY INFORMATION, you must reply with exactly this sentence and nothing else:
           "I'm not sure about that. Would you like me to create a support ticket so our team can help you?"
        3. Never say "we do not offer", "we do not sell" or "we do not have" unless the COMPANY INFORMATION says so explicitly.
        4. Never guess or make up policies, prices, dates or features.
        5. Never ask for or repeat sensitive data such as passwords or card numbers.

        COMPANY INFORMATION:
        %s
        """.formatted(faq);

        this.chatClient = builder.defaultSystem(systemPrompt).build();
    }

    public record ChatRequest(String conversationId, String message) {}
    public record ChatResponse(String reply) {}

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String conversationId =
                (request.conversationId() == null || request.conversationId().isBlank())
                        ? "default"
                        : request.conversationId();

        try {
            List<ChatLog> latest =
                    repository.findTop20ByConversationIdOrderByCreatedAtDesc(conversationId);

            List<Message> history = new ArrayList<>();
            for (int i = latest.size() - 1; i >= 0; i--) {
                ChatLog log = latest.get(i);
                if ("user".equals(log.getRole())) {
                    history.add(new UserMessage(log.getText()));
                } else {
                    history.add(new AssistantMessage(log.getText()));
                }
            }

            String reply = chatClient.prompt()
                    .messages(history)
                    .user(request.message())
                    .call()
                    .content();

            repository.save(new ChatLog(conversationId, "user", request.message()));
            repository.save(new ChatLog(conversationId, "assistant", reply));

            return new ChatResponse(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Sorry, I'm having trouble right now. Please try again in a moment.");
        }
    }
}