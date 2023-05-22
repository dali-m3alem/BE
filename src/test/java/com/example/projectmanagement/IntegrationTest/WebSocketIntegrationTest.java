package com.example.projectmanagement.IntegrationTest;

import com.example.projectmanagement.Domaine.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class WebSocketIntegrationTest {

    @Test
    public void testWebSocketChat() throws InterruptedException, ExecutionException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
        StompSession stompSession = stompClient.connect("ws://localhost:8080/websocket/info", new StompSessionHandlerAdapter() {}).get();

        // Subscribe to a topic to receive messages
        stompSession.subscribe("/topic/chat", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Handle received message
                Message message = (Message) payload;
                // Assert and validate the received message
            }
        });

        // Send a message
        Message message = new Message();
        message.setContent("Hello, World!");
        stompSession.send("/app/chat.send", message);

        // Wait for some time to receive the message
        Thread.sleep(2000);

        // Assert and validate the received message

        // Disconnect the session
        stompSession.disconnect();
    }
}

