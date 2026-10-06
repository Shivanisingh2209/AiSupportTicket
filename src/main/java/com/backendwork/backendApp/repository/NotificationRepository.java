package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(String userId);
}