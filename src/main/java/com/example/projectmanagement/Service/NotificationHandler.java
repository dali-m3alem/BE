package com.example.projectmanagement.Service;

import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class NotificationHandler implements WebSocketHandler {

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final NotificationRepository notificationRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // empty implementation
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
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(notification.toString()));
            }
        }
    }

    public Notification createNotification(String description, User sentTo) {
        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setDate(LocalDateTime.now());
        notification.setSentTo(sentTo);
        notificationRepository.save(notification);
        return notification;
    }

}
