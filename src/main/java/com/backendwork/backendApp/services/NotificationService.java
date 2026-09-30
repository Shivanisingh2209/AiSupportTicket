package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Notification;
import com.backendwork.backendApp.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(
            String userId,
            String userName,
            String message,
            String type,
            String ticketId) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setUserName(userName);
        notification.setMessage(message);
        notification.setType(type);
        notification.setTicketId(ticketId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }
}