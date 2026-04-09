package com.example.demo;

import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import com.example.demo.*;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Created new Session");
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        session.send("/input/test", new Greeting(""));
    }

}
