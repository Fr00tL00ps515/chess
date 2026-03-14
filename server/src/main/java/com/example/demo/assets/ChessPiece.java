package com.example.demo.assets;

//WITHOUT CHECKMATE!!!
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

    // May not be used for Pawn and Knight
    public boolean pathIsFree(int row, int col, ChessPiece[][] board) {
        int oneStepRight = 0;
        int oneStepBelow = 0;
        if (pos[0] == row && pos[1] == col)
            return true;
        if (board[row][col] != null)
            return false;
        oneStepBelow = (row - pos[0]) / Math.abs(row - pos[0]);
        oneStepRight = (col - pos[1]) / Math.abs(col - pos[1]);
        // Move Right/Left
        for (int ver = pos[0] + oneStepBelow, hor = pos[1] + oneStepRight; ver != row
                || hor != col; ver += oneStepBelow, hor += oneStepRight) {
            if (board[ver][hor] != null)
                return false;
        }
        return true;
    }
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
        if (pos[0] == row && pos[1] == col)
            return true;
        if (pos[1] != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;

        if (!pathIsFree(row, col, board))
            return false;
        if (playerAboveBoard) {
            return row == (pos[0] + 1) || row == (pos[0] + 2) && hasTwoSteps ? true : false;
        }
        return row == (pos[0] - 1) || row == (pos[0] - 2) && hasTwoSteps ? true : false;
    }
}

class Rook extends ChessPiece {

    public Rook(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[0] == row && pos[1] == col)
            return true;
        if (!pathIsFree(row, col, board) || pos[0] != row && pos[1] != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;
        return true;

    }
}

class Bishop extends ChessPiece {
    public Bishop(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[0] == row && pos[1] == col)
            return true;
        if (!pathIsFree(row, col, board) || Math.abs(pos[0] - row) != Math.abs(pos[1] - col) || col < 0 || row < 0
                || col > 7 || row > 7)
            return false;

        return true;
    }
}

class Queen extends ChessPiece {
    public Queen(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[0] == row && pos[1] == col)
            return true;
        if (!pathIsFree(row, col, board) || Math.abs(pos[0] - row) != Math.abs(pos[1] - col)
                && (pos[0] != row && pos[1] != col) || col < 0 || row < 0 || col > 7 || row > 7)
            return false;

        return true;
    }
}

class Knight extends ChessPiece {
    public Knight(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (pos[0] == row && pos[1] == col)
            return true;
        if (board[row][col] != null)
            return false;
        if (Math.abs(pos[0] - row) == 1 && Math.abs(pos[1] - col) == 2
                || Math.abs(pos[0] - row) == 2 && Math.abs(pos[1] - col) == 1)
            return true;
        return false;
    }
}

class King extends ChessPiece {
    public King(int row, int col, boolean playerAboveBoard) {
        super(row, col, playerAboveBoard);
    }

    // Should be changed for Checkmate
    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (!pathIsFree(row, col, board))
            return false;
        return Math.abs(pos[0] - row) <= 1 && Math.abs(pos[1] - col) <= 1 ? true : false;
    }
}