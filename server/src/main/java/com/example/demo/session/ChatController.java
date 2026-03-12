package com.example.demo.session;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private String[][] board = {
            { "5", "4", "3", "2", "1", "3", "4", "5" },
            { "6", "6", "6", "6", "6", "6", "6", "6" },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { " ", " ", " ", " ", " ", " ", " ", " " },
            { "6", "6", "6", "6", "6", "6", "6", "6" },
            { "5", "4", "3", "2", "1", "3", "4", "5" } };

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Greeting handleMove(Greeting greeting) {
        System.out.println("Got message: " + greeting.text());
        return new Greeting("Hi  Client");
    }
}
