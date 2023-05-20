package com.example.projectmanagement.Service;

import com.example.projectmanagement.Domaine.Message;
import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.MessageRepository;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import com.example.projectmanagement.Reposirtory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final NotificationRepository notificationRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    private final List<WebSocketSession> webSocketSessions = new ArrayList<>();

    public WebSocketHandler(NotificationRepository notificationRepository) {
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
  /*  public void sendMessage(Message message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message.toString()));
            }
        }
    }
    public Message createMessage(Message message, User sender, List<User> recipients){
        message.setSender(sender);
        message.setRecipients(recipients);
        message.setLocalDateTime(LocalDateTime.now());
        message.setContent(message.getContent());
        messageRepository.save(message);
        return message;
    }
    public List<Message> getMessagesBetweenTwoUsersById(Long sender, Long recipients){
        return messageRepository.findMessagesBetweenTwoUsersById(sender,recipients);
    }
    public List<Message> getMessagesByUserId(Long userId) {
        return messageRepository.findByRecipientsId(userId);
    } */
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

    //chat methods and chat websocket stuff

}
