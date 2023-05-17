package com.example.projectmanagement.Service;

import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationHandler implements WebSocketHandler {

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;


    public NotificationHandler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Handle transport errors if needed
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Handle incoming messages if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendNotification(Notification notification) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(notification.toString()));
            }
        }
    }

    public Notification createNotification(String description, User sentTo) {
        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setDate(LocalDateTime.now());
        notification.setSendTo(sentTo);
        notification.setIsRead(false);
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getUserNotifications(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(()
                        -> new EntityNotFoundException("User not found : " + id));
        return notificationRepository.findAllBySendTo(user);
    }

    public List<Notification> updateIsRead(Long id) {
        List<Notification> notifications = getUserNotifications(id);
        for (Notification notification : notifications) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        return notifications;
    }
}


