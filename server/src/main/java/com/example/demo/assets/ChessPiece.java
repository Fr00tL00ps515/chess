package com.example.demo.assets;

//WITHOUT CHECKMATE!!!
public abstract class ChessPiece {

    public String owner;
    public String letter;
    public boolean playerAboveBoard;
    public int posRow;
    public int posCol;

    public ChessPiece(int row, int col, boolean playerAboveBoard, String owner) {
        posRow = row;
        posCol = col;
        this.playerAboveBoard = playerAboveBoard;
        this.owner = owner;
    }

    public abstract boolean canMove(int row, int col, ChessPiece[][] board);

    // May not be used for Pawn and Knight
    public boolean pathIsFree(int row, int col, ChessPiece[][] board) {
        int oneStepRight = 0;
        int oneStepBelow = 0;
        if (posRow == row && posCol == col)
            return true;
        if (board[row][col] != null)
            return false;
        oneStepBelow = (row - posRow) / Math.abs(row - posRow);
        oneStepRight = (col - posCol) / Math.abs(col - posCol);
        // Move Right/Left
        for (int ver = posRow + oneStepBelow, hor = posCol + oneStepRight; ver != row
                || hor != col; ver += oneStepBelow, hor += oneStepRight) {
            if (board[ver][hor] != null)
                return false;
        }
        return true;
    }
}

class Pawn extends ChessPiece {

    public boolean hasTwoSteps = true;

    public Pawn(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "P";
    }

    public void firstMove() {
        hasTwoSteps = false;
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (posRow == row && posCol == col)
            return true;
        if (posCol != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;

        if (!pathIsFree(row, col, board))
            return false;
        if (playerAboveBoard) {
            return row == (posRow + 1) || row == (posRow + 2) && hasTwoSteps ? true : false;
        }
        return row == (posRow - 1) || row == (posRow - 2) && hasTwoSteps ? true : false;
    }
}

class Rook extends ChessPiece {

    public Rook(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "R";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (posRow == row && posCol == col)
            return true;
        if (!pathIsFree(row, col, board) || posRow != row && posCol != col || col < 0 || row < 0 || col > 7 || row > 7)
            return false;
        return true;

    }
}

class Bishop extends ChessPiece {

    public Bishop(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "B";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (posRow == row && posCol == col)
            return true;
        if (!pathIsFree(row, col, board) || Math.abs(posRow - row) != Math.abs(posCol - col) || col < 0 || row < 0
                || col > 7 || row > 7)
            return false;

        return true;
    }
}

class Queen extends ChessPiece {
    public Queen(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "Q";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (posRow == row && posCol == col)
            return true;
        if (!pathIsFree(row, col, board) || Math.abs(posRow - row) != Math.abs(posCol - col)
                && (posRow != row && posCol != col) || col < 0 || row < 0 || col > 7 || row > 7)
            return false;

        return true;
    }
}

class Knight extends ChessPiece {
    public Knight(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "Kn";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (posRow == row && posCol == col)
            return true;
        if (board[row][col] != null)
            return false;
        if (Math.abs(posRow - row) == 1 && Math.abs(posCol - col) == 2
                || Math.abs(posRow - row) == 2 && Math.abs(posCol - col) == 1)
            return true;
        return false;
    }
}

class King extends ChessPiece {
    public King(int row, int col, boolean playerAboveBoard, String owner) {
        super(row, col, playerAboveBoard, owner);
        letter = "K";
    }

    // Should be changed for Checkmate
    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (!pathIsFree(row, col, board))
            return false;
        return Math.abs(posRow - row) <= 1 && Math.abs(posCol - col) <= 1 ? true : false;
    }
}