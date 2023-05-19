package com.example.projectmanagement.Reposirtory;

import com.example.projectmanagement.Domaine.Message;
import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Domaine.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository  extends JpaRepository<Message, Long> {
    List<Message> findByRecipientsId(Long recipientId);
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :senderId AND :recipientsId MEMBER OF m.recipients) OR (m.sender.id = :recipientsId AND :senderId MEMBER OF m.recipients)")
    List<Message> findMessagesBetweenTwoUsersById(@Param("senderId") Long senderId, @Param("recipientsId") Long recipientsId);
}
