package com.example.projectmanagement.Reposirtory;

import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllBySendTo(User user1);
    long countByIsReadAndSendToId(boolean isRead, Long userId);

}
