package com.example.demo;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("New session established: " + session.getSessionId());
        session.subscribe("/topic/messages", this);
        session.send("/app/chat", new Greeting("Hello Server"));
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Greeting.class; // Tell the handler to expect a String
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
            StompHeaders headers, byte[] payload, Throwable exception) {
        // This will print the exact conversion error that is stopping your code
        exception.printStackTrace();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Received message: " + ((Greeting) payload).text());
    }
}
