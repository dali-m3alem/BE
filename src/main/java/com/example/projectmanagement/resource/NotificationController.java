package com.example.projectmanagement.resource;

import com.example.projectmanagement.Domaine.Notification;
import com.example.projectmanagement.Reposirtory.NotificationRepository;
import com.example.projectmanagement.Service.NotificationHandler;
import com.example.projectmanagement.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationHandler notificationHandler;
    private final JwtService jwtService;
    @GetMapping("/getUserNotifications")
    public List<Notification> getUserNotifications(HttpServletRequest request){
            final String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            Long Id = Long.valueOf(jwtService.extractId(jwt));
        return notificationHandler.getUserNotifications(Id);
    }
    @PutMapping("/{userId}/markAsRead")
    public ResponseEntity<List<Notification>> markUserNotificationsAsRead(@PathVariable Long userId) {
        List<Notification> updatedNotifications = notificationHandler.updateIsRead(userId);
        return ResponseEntity.ok(updatedNotifications);
    }




}
