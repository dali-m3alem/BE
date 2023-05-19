package com.example.projectmanagement.resource;

import com.example.projectmanagement.DTO.MessageDTO;
import com.example.projectmanagement.Domaine.Message;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Service.UserSer;
import com.example.projectmanagement.Service.WebSocketHandler;
import com.example.projectmanagement.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class MessageController {
    private final WebSocketHandler webSocketHandler;
    private final UserSer userService;
    private final JwtService jwtService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO requestDTO) throws IOException {
        User sender = userService.findUserById(requestDTO.getSenderId());
        List<User> recipients = userService.findUsersByIds(requestDTO.getRecipientIds());

        Message message = new Message();
        message.setContent(requestDTO.getContent());

        webSocketHandler.createMessage(message, sender, recipients);
        webSocketHandler.sendMessage(message);

        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("/{userId}/AllMessages")
    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable("userId") Long userId) {
        try {
            List<Message> messages = webSocketHandler.getMessagesByUserId(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // or you can provide an error message if needed
        }
    }
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessagesBetweenTwoUsersById(@RequestParam("sender") Long senderId, @RequestParam("recipients") Long recipientsId) {
        try {
            List<Message> messages = webSocketHandler.getMessagesBetweenTwoUsersById(senderId, recipientsId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // or you can provide an error message if needed
        }
    }
}
