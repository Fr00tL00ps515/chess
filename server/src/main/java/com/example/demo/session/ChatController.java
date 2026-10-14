package com.example.demo.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.assets.Chessboard;
import com.example.demo.assets.ErrorResponse;
import com.example.demo.assets.MoveRequest;
import com.example.demo.assets.PlayerResponse;
import com.example.demo.assets.Response;

@Controller
public class ChatController {

    private Chessboard board = new Chessboard();
    private String player1;
    private String player2;
    private List<String> waiting = new ArrayList<>();
    private Set<String> connected = new HashSet<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public synchronized void onConnect(SessionConnectEvent event) {
        connected.add(SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders()));
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        leaveGame(event.getSessionId());
    }

    @MessageMapping("/join_game")
    public synchronized void joinGame(@Header("simpSessionId") String id) {
        System.out.println(id + " wants to join the game");
        if (!connected.contains(id) || id.equals(player1) || id.equals(player2) || waiting.contains(id))
            return;

        if (player1 == null) {
            player1 = id;
            sendToPlayer(id, "/output/player", new PlayerResponse("WHITE", 0));
        } else if (player2 == null) {
            player2 = id;
            sendToPlayer(id, "/output/player", new PlayerResponse("BLACK", 0));
        } else {
            waiting.add(id);
            System.out.println(id + " waits, position " + waiting.size());
            sendToPlayer(id, "/output/player", new PlayerResponse(null, waiting.size()));
            return;
        }
        sendBoard();
    }

    public synchronized void leaveGame(String id) {
        connected.remove(id);
        if (waiting.remove(id)) {
            updateWaiting();
            return;
        }

        if (id.equals(player1))
            player1 = null;
        else if (id.equals(player2))
            player2 = null;
        else
            return;
        System.out.println(id + " left the game");

        if (player1 == null && player2 == null)
            board = new Chessboard();

        if (!waiting.isEmpty()) {
            String next = waiting.remove(0);
            if (player1 == null) {
                player1 = next;
                sendToPlayer(next, "/output/player", new PlayerResponse("WHITE", 0));
            } else {
                player2 = next;
                sendToPlayer(next, "/output/player", new PlayerResponse("BLACK", 0));
            }
            updateWaiting();
        }
        sendBoard();
    }

    @MessageMapping("/move")
    public synchronized void handleMove(@Header("simpSessionId") String id, MoveRequest move) {
        String color = colorOf(id);
        System.out.println(color + " wants make a move");

        if (color == null) {
            sendError(id, "You are not playing in this game");
            return;
        }
        if (player1 == null || player2 == null) {
            sendError(id, "Wait for your opponent to join");
            return;
        }
        if (!board.myTurn.equals(color)) {
            sendError(id, "It is not your turn");
            return;
        }
        if (!board.movePiece(move.fromRow(), move.fromCol(), move.toRow(), move.toCol(), move.promotion())) {
            System.out.println("Move is not allowed");
            sendError(id, "Illegal move");
            return;
        }
        sendBoard();
    }

    @MessageMapping("/restart")
    public synchronized void restart(@Header("simpSessionId") String id) {
        if (colorOf(id) == null || !board.isOver())
            return;
        System.out.println(id + " started a new game");
        board = new Chessboard();
        sendBoard();
    }

    private String colorOf(String id) {
        if (id.equals(player1))
            return "WHITE";
        if (id.equals(player2))
            return "BLACK";
        return null;
    }

    private void updateWaiting() {
        for (int i = 0; i < waiting.size(); i++) {
            sendToPlayer(waiting.get(i), "/output/player", new PlayerResponse(null, i + 1));
        }
    }

    private void sendBoard() {
        List<String> players = new ArrayList<>();
        if (player1 != null)
            players.add("WHITE");
        if (player2 != null)
            players.add("BLACK");
        messagingTemplate.convertAndSend("/output/game", new Response(board.printBoard(), board.myTurn,
                board.getStatus(), board.getLegalMoves(), board.lastMove, players));
    }

    private void sendError(String id, String message) {
        sendToPlayer(id, "/output/errors", new ErrorResponse(message));
    }

    private void sendToPlayer(String id, String destination, Object payload) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setSessionId(id);
        headers.setLeaveMutable(true);
        messagingTemplate.convertAndSendToUser(id, destination, payload, headers.getMessageHeaders());
    }
}
