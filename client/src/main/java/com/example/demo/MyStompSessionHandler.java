package com.example.demo;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.example.demo.messages.Greeting;
import com.example.demo.messages.MoveResponse;
import com.example.demo.messages.MoveResponse;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    StompSession session;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        System.out.println("New session established: " + session.getSessionId());
        session.subscribe("/output/move", this);
        session.send("/input/new_session", new Greeting(session.getSessionId()));
    }

    // @Override
    // public Type getPayloadType(StompHeaders headers) {
    // return Greeting.class; // Tell the handler to expect a String
    // }

    // @Override
    // public void handleException(StompSession session, StompCommand command,
    // StompHeaders headers, byte[] payload, Throwable exception) {
    // // This will print the exact conversion error that is stopping your code
    // exception.printStackTrace();
    // }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null)
            return;
        MoveResponse response = (MoveResponse) payload;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                DemoApplication.board[row][col] = response.board()[row][col];
            }
        }
        DemoApplication.myTurn = response.myTurn();
    }
}
