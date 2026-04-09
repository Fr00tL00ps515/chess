package com.example.demo.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.demo.assets.Chessboard;
import com.example.demo.assets.MoveRequest;
import com.example.demo.assets.Response;
import com.example.demo.assets.Greeting;

@Controller
public class ChatController {

    private Chessboard board = new Chessboard("player1", "player2");;
    private String player1;
    private String player2;
    private String myTurn;
    private boolean gameStarted = false;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/join_game")
    public void joinGame(Greeting greeting) {
        System.out.println(greeting.text() + " wants to join the game");
        if (player1 == null) {
            player1 = greeting.text();
            messagingTemplate.convertAndSend("/output/move" + player1,
                    new Response("player1", board.toString("player1"),
                            gameStarted && myTurn.equals("player1") ? true : false));

        } else if (player2 == null) {
            player2 = greeting.text();
            messagingTemplate.convertAndSend("/output/move" + player2,
                    new Response("player2", board.toString("player2"),
                            !gameStarted || myTurn.equals("player2") ? true : false));
            if (!gameStarted)
                gameStarted = true;
        }
        // TODO: add the third case
    }

    @MessageMapping("/leave_game")
    public void leaveGame(Greeting greeting) {
        String id = greeting.text();
        if (id.equals(player1))
            player1 = null;
        else if (id.equals(player2))
            player2 = null;
        // TODO: add the third case
    }

    @MessageMapping("/test")
    @SendTo("/output")
    public Greeting test(Greeting message) {
        System.out.println("Got a message: " + message);
        return new Greeting("Server says HI");
    }

    @MessageMapping("/player1")
    public void handleMove1(MoveRequest move) {

        System.out.println("player1 wants make a move");

        if (!board.movePiece(move.posRow(), move.posCol(), move.row(), move.col(), "player1")) {
            System.out.println("Move is not allowed");
            messagingTemplate.convertAndSend("/output/move" + player1,
                    new Response(null, board.toString("player1"), true));
            return;
        }
        messagingTemplate.convertAndSend("/output/move" + player1,
                new Response(null, board.toString("player1"), false));
        messagingTemplate.convertAndSend("/output/move" + player2,
                new Response(null, board.toString("player2"), true));
        myTurn = "player2";
    }

    @MessageMapping("/player2")
    public void handleMove2(MoveRequest move) {
        System.out.println("player2 wants make a move");

        if (!board.movePiece(move.posRow(), move.posCol(), move.row(), move.col(), "player2")) {
            System.out.println("Move is not allowed");
            messagingTemplate.convertAndSend("/output/move" + player2,
                    new Response(null, board.toString("player2"), true));
            return;
        }
        messagingTemplate.convertAndSend("/output/move" + player1,
                new Response(null, board.toString("player1"), true));
        messagingTemplate.convertAndSend("/output/move" + player2,
                new Response(null, board.toString("player2"), false));
        myTurn = "player1";
    }
}
