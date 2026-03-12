package com.example.demo.assets;

public abstract class ChessPiece {

    public String owner;
    public boolean playerAboveBoard;
    public int[] pos = new int[2];

    public ChessPiece(int row, int col, boolean playerAboveBoard) {
        pos[0] = row;
        pos[1] = col;
        this.playerAboveBoard = playerAboveBoard;
    }

    public abstract boolean canMove(int row, int col, ChessPiece[][] board);
}

class Pawn extends ChessPiece {

    public boolean hasTwoSteps = true;

    public Pawn(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public void firstMove() {
        hasTwoSteps = false;
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[1] != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;

        if (playerAboveBoard) {
            for (int i = pos[0] + 1; i <= row; i++) {
                if (board[i][col] != null)
                    return false;
            }
            return row == (pos[0] + 1) || row == (pos[0] + 2) && hasTwoSteps ? true : false;
        }
        for (int i = pos[0] - 1; i >= row; i--) {
            if (board[i][col] != null)
                return false;
        }
        return row == (pos[0] - 1) || row == (pos[0] - 2) && hasTwoSteps ? true : false;
    }
}

class Rook extends ChessPiece {

    public Rook(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[0] != row && pos[1] != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;
        if (pos[0] == row) {
            if (pos[1] < col) {
                for (int i = pos[1] + 1; i <= col; i++) {
                    if (board[row][i] != null)
                        return false;
                }
            } else {
                for (int i = pos[1] - 1; i >= col; i--) {
                    if (board[row][i] != null)
                        return false;
                }
            }
        } else {
            if (pos[0] < row) {
                for (int i = pos[0] + 1; i <= row; i++) {
                    if (board[i][col] != null)
                        return false;
                }
            } else {
                for (int i = pos[0] - 1; i >= row; i--) {
                    if (board[i][col] != null)
                        return false;
                }
            }
        }
        return true;

    }
}