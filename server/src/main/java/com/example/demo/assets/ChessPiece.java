package com.example.demo.assets;

public abstract class ChessPiece {

    public String owner;
    public String letter;
    public int posRow;
    public int posCol;
    public boolean hasMoved = false;

    public ChessPiece(int row, int col, String owner) {
        posRow = row;
        posCol = col;
        this.owner = owner;
    }

    public abstract boolean canMove(int row, int col, ChessPiece[][] board);

    // Only for straight and diagonal moves
    public boolean pathIsFree(int row, int col, ChessPiece[][] board) {
        int oneStepBelow = Integer.signum(row - posRow);
        int oneStepRight = Integer.signum(col - posCol);
        for (int ver = posRow + oneStepBelow, hor = posCol + oneStepRight; ver != row
                || hor != col; ver += oneStepBelow, hor += oneStepRight) {
            if (board[ver][hor] != null)
                return false;
        }
        return true;
    }

    public String print() {
        return owner.equals("WHITE") ? letter : letter.toLowerCase();
    }
}

class Pawn extends ChessPiece {

    public Pawn(int row, int col, String owner) {
        super(row, col, owner);
        letter = "P";
    }

    public int direction() {
        return owner.equals("WHITE") ? -1 : 1;
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        if (col == posCol) {
            if (board[row][col] != null)
                return false;
            if (row == posRow + direction())
                return true;
            return !hasMoved && row == posRow + 2 * direction() && board[posRow + direction()][col] == null;
        }
        return canAttack(row, col) && board[row][col] != null;
    }

    public boolean canAttack(int row, int col) {
        return row == posRow + direction() && Math.abs(col - posCol) == 1;
    }
}

class Rook extends ChessPiece {

    public Rook(int row, int col, String owner) {
        super(row, col, owner);
        letter = "R";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        return (posRow == row || posCol == col) && pathIsFree(row, col, board);
    }
}

class Bishop extends ChessPiece {

    public Bishop(int row, int col, String owner) {
        super(row, col, owner);
        letter = "B";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        return Math.abs(posRow - row) == Math.abs(posCol - col) && pathIsFree(row, col, board);
    }
}

class Queen extends ChessPiece {

    public Queen(int row, int col, String owner) {
        super(row, col, owner);
        letter = "Q";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        boolean straight = posRow == row || posCol == col;
        boolean diagonal = Math.abs(posRow - row) == Math.abs(posCol - col);
        return (straight || diagonal) && pathIsFree(row, col, board);
    }
}

class Knight extends ChessPiece {

    public Knight(int row, int col, String owner) {
        super(row, col, owner);
        letter = "N";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        return Math.abs(posRow - row) == 1 && Math.abs(posCol - col) == 2
                || Math.abs(posRow - row) == 2 && Math.abs(posCol - col) == 1;
    }
}

class King extends ChessPiece {

    public King(int row, int col, String owner) {
        super(row, col, owner);
        letter = "K";
    }

    public boolean canMove(int row, int col, ChessPiece[][] board) {
        return Math.abs(posRow - row) <= 1 && Math.abs(posCol - col) <= 1;
    }
}
