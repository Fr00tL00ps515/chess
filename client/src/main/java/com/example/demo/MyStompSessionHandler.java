package com.example.demo;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.example.demo.messages.Greeting;
import com.example.demo.messages.Response;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        System.out.println("New session established: " + session.getSessionId());
        session.subscribe("/output/move" + session.getSessionId(), this);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Response.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Response response = (Response) payload;
        System.out.println(response.myTurn());
        if (response.newPlayer() != null) {
            DemoApplication.player = response.newPlayer();
        }
        if (response.board() != null) {
            DemoApplication.board = new String[8][8];
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    DemoApplication.board[row][col] = response.board()[row][col];
                }
            }
        }
        DemoApplication.myTurn = response.myTurn();
    }
}
