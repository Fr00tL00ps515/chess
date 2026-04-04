package com.example.demo.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.demo.assets.Chessboard;
import com.example.demo.assets.MoveRequest;

@Controller
public class ChatController {

    private Chessboard board;
    private String player1;
    private String player2;
    private int gameCounter = 0;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/new_session")
    @SendTo("/output/messages")
    public String[][] createNewSession(Greeting greeting) {
        if (player1 == null) {
            player1 = greeting.text();
            System.out.println(player1);
            return null;
        }
        player2 = greeting.text();
        System.out.println(player2);
        board = new Chessboard(player1, player2);
        return board.toString(player1);
    }

    @MessageMapping("/player1")
    public void handleMove1(MoveRequest move) {
        if (!player1.equals(move.id()))
            ;
        System.out.println("player1 wants make a move");
        if (gameCounter != 0) {
            messagingTemplate.convertAndSendToUser(player1, "/output/move", null);
            return;
        }
        if (!board.movePiece(move.posRow(), move.posCol(), move.row(), move.col(), player1)) {
            messagingTemplate.convertAndSendToUser(player1, "/output/move", null);
            return;
        }
        messagingTemplate.convertAndSendToUser(player1, "/output/move", board.toString(player1));
        messagingTemplate.convertAndSendToUser(player2, "/output/move", board.toString(player2));
        gameCounter = 1;
    }

    @MessageMapping("/player2")
    public void handleMove2(MoveRequest move) {
        System.out.println("player2 wants make a move");
        if (gameCounter != 1) {
            messagingTemplate.convertAndSendToUser(player2, "/output/move", null);
            return;
        }
        if (!board.movePiece(move.posRow(), move.posCol(), move.row(), move.col(), player2)) {
            messagingTemplate.convertAndSendToUser(player2, "/output/move", null);
            return;
        }
        messagingTemplate.convertAndSendToUser(player1, "/output/move", board.toString(player1));
        messagingTemplate.convertAndSendToUser(player2, "/output/move", board.toString(player2));
        gameCounter = 0;
    }
}
