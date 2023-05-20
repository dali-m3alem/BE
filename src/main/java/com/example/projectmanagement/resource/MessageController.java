package com.example.projectmanagement.resource;

import com.example.projectmanagement.Domaine.Message;
import com.example.projectmanagement.Domaine.User;
import com.example.projectmanagement.Reposirtory.MessageRepository;
import com.example.projectmanagement.Service.UserSer;
import com.example.projectmanagement.Service.WebSocketHandler;
import com.example.projectmanagement.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
  private final MessageRepository messageRepository;
  private SimpMessagingTemplate messagingTemplate;


  @MessageMapping("/chat.send")
  public Message sendMessage(@Payload Message message) {
    // Store the message in the database
    messageRepository.save(message);
    return message;
  }
  @MessageMapping("/chat.receive")
  public void receiveMessage(@Payload Message message) {
    // Process the received message
    // You can perform any necessary operations here, such as broadcasting the message to other clients, updating chat history, etc.


    // Broadcast the message to other connected clients
    messagingTemplate.convertAndSend("/topic/chat", message);
  }
  @GetMapping("/conversation/{conversationId}")
  public List<Message> getConversationHistory(@PathVariable String conversationId) {
    // Retrieve the conversation history from the database based on the conversation ID
    List<Message> conversationHistory = messageRepository.findByConversationId(conversationId);
    return conversationHistory;
  }

  }



