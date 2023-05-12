package com.example.projectmanagement.Reposirtory;

import com.example.projectmanagement.Domaine.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
