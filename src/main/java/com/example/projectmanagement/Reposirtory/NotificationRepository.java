package com.example.projectmanagement.Reposirtory;

import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllBySendTo(User user);
}
