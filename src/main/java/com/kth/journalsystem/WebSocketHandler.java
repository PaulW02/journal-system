package com.kth.journalsystem;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Handle incoming messages here
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        // Send a response back (optional)
        session.sendMessage(new TextMessage("Received: " + payload));
    }
}

