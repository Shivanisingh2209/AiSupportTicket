package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.entity.Notification;
import com.backendwork.backendApp.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public Notification createNotification(
            @RequestBody Notification notification) {

        return notificationService.createNotification(
                notification.getUserId(),
                notification.getUserName(),
                notification.getMessage(),
                notification.getType(),
                notification.getTicketId()
        );
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(
            @PathVariable String userId) {

        return notificationService
                .getUserNotifications(userId);
    }

    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable String userId) {

        return notificationService
                .getUnreadNotifications(userId);
    }
}