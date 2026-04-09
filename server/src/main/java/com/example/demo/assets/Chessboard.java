package com.example.demo.assets;

public class Chessboard {
    String player1;
    String player2;
    private ChessPiece[][] board1 = {
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null } };

    private ChessPiece[][] board2 = {
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null } };

    public Chessboard(String player1, String player2) {
        // Initialize player1
        this.player1 = player1;
        for (int i = 0; i < 8; i++) {
            board1[1][i] = new Pawn(1, i, player2);
            board1[6][i] = new Pawn(6, i, player1);
        }
        board1[0][0] = new Rook(0, 0, player2);
        board1[0][1] = new Knight(0, 1, player2);
        board1[0][2] = new Bishop(0, 2, player2);
        board1[0][3] = new Queen(0, 3, player2);
        board1[0][4] = new King(0, 4, player2);
        board1[0][5] = new Bishop(0, 5, player2);
        board1[0][6] = new Knight(0, 6, player2);
        board1[0][7] = new Rook(0, 7, player2);

        board1[7][0] = new Rook(7, 0, player1);
        board1[7][1] = new Knight(7, 1, player1);
        board1[7][2] = new Bishop(7, 2, player1);
        board1[7][3] = new Queen(7, 3, player1);
        board1[7][4] = new King(7, 4, player1);
        board1[7][5] = new Bishop(7, 5, player1);
        board1[7][6] = new Knight(7, 6, player1);
        board1[7][7] = new Rook(7, 7, player1);

        // Initialize player2
        this.player2 = player2;
        for (int i = 0; i < 8; i++) {
            board2[1][i] = new Pawn(1, i, player1);
            board2[6][i] = new Pawn(6, i, player2);
        }
        board2[0][0] = new Rook(0, 0, player1);
        board2[0][1] = new Knight(0, 1, player1);
        board2[0][2] = new Bishop(0, 2, player1);
        board2[0][3] = new Queen(0, 3, player1);
        board2[0][4] = new King(0, 4, player1);
        board2[0][5] = new Bishop(0, 5, player1);
        board2[0][6] = new Knight(0, 6, player1);
        board2[0][7] = new Rook(0, 7, player1);

        board2[7][0] = new Rook(7, 0, player2);
        board2[7][1] = new Knight(7, 1, player2);
        board2[7][2] = new Bishop(7, 2, player2);
        board2[7][3] = new Queen(7, 3, player2);
        board2[7][4] = new King(7, 4, player2);
        board2[7][5] = new Bishop(7, 5, player2);
        board2[7][6] = new Knight(7, 6, player2);
        board2[7][7] = new Rook(7, 7, player2);
    }

    public boolean movePiece(int posRow, int posCol, int row, int col, String player) {
        ChessPiece[][] playersBoard = player.equals(player1) ? board1 : board2;
        ChessPiece[][] otherBoard = player.equals(player1) ? board2 : board1;
        if (playersBoard[posRow][posCol] == null || !playersBoard[posRow][posCol].owner.equals(player)
                || !playersBoard[posRow][posCol].canMove(row, col, playersBoard))
            return false;
        playersBoard[row][col] = playersBoard[posRow][posCol];
        playersBoard[row][col].posRow = row;
        playersBoard[row][col].posCol = col;
        playersBoard[posRow][posCol] = null;
        otherBoard[7 - row][7 - col] = otherBoard[7 - posRow][7 - posCol];
        otherBoard[7 - row][7 - col].posRow = 7 - row;
        otherBoard[7 - row][7 - col].posCol = 7 - col;
        otherBoard[7 - posRow][7 - posCol] = null;
        return true;
    }

    public String[][] toString(String player) {
        ChessPiece[][] board;
        if (player == player1)
            board = board1;
        else if (player == player2)
            board = board2;
        else {
            return null;
        }
        String[][] res = new String[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                res[row][col] = board[row][col] == null ? "   "
                        : board[row][col].print();
            }
        }
        return res;
    }

}
