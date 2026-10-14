package com.example.demo.assets;

import java.util.List;

public record Response(String[][] board, String turn, String status, List<Move> legalMoves, Move lastMove,
        List<String> players) {

}
