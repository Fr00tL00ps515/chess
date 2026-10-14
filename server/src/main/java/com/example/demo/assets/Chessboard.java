package com.example.demo.assets;

import java.util.ArrayList;
import java.util.List;

public class Chessboard {

    public ChessPiece[][] board = new ChessPiece[8][8];
    public String myTurn = "WHITE";
    public Move lastMove;

    public Chessboard() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(1, i, "BLACK");
            board[6][i] = new Pawn(6, i, "WHITE");
        }
        putPieces(0, "BLACK");
        putPieces(7, "WHITE");
    }

    private void putPieces(int row, String owner) {
        board[row][0] = new Rook(row, 0, owner);
        board[row][1] = new Knight(row, 1, owner);
        board[row][2] = new Bishop(row, 2, owner);
        board[row][3] = new Queen(row, 3, owner);
        board[row][4] = new King(row, 4, owner);
        board[row][5] = new Bishop(row, 5, owner);
        board[row][6] = new Knight(row, 6, owner);
        board[row][7] = new Rook(row, 7, owner);
    }

    public boolean movePiece(int posRow, int posCol, int row, int col, String promotion) {
        if (!isLegal(posRow, posCol, row, col))
            return false;

        ChessPiece piece = board[posRow][posCol];
        ChessPiece promoted = null;
        if (piece instanceof Pawn && (row == 0 || row == 7)) {
            promoted = createPiece(promotion, row, col, piece.owner);
            if (promoted == null)
                return false;
        }

        if (piece instanceof Pawn && col != posCol && board[row][col] == null)
            board[posRow][col] = null;

        if (piece instanceof King && Math.abs(col - posCol) == 2) {
            int rookCol = col > posCol ? 7 : 0;
            int newRookCol = (posCol + col) / 2;
            ChessPiece rook = board[posRow][rookCol];
            board[posRow][newRookCol] = rook;
            board[posRow][rookCol] = null;
            rook.posCol = newRookCol;
            rook.hasMoved = true;
        }

        board[row][col] = promoted != null ? promoted : piece;
        board[posRow][posCol] = null;
        piece.posRow = row;
        piece.posCol = col;
        piece.hasMoved = true;

        lastMove = new Move(posRow, posCol, row, col);
        myTurn = other(myTurn);
        return true;
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (int posRow = 0; posRow < 8; posRow++) {
            for (int posCol = 0; posCol < 8; posCol++) {
                if (board[posRow][posCol] == null || !board[posRow][posCol].owner.equals(myTurn))
                    continue;
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (isLegal(posRow, posCol, row, col))
                            moves.add(new Move(posRow, posCol, row, col));
                    }
                }
            }
        }
        return moves;
    }

    public String getStatus() {
        boolean check = isInCheck(myTurn);
        if (getLegalMoves().isEmpty())
            return check ? "CHECKMATE" : "STALEMATE";
        return check ? "CHECK" : "ACTIVE";
    }

    public boolean isOver() {
        String status = getStatus();
        return status.equals("CHECKMATE") || status.equals("STALEMATE");
    }

    public String[][] printBoard() {
        String[][] res = new String[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                res[row][col] = board[row][col] == null ? null : board[row][col].print();
            }
        }
        return res;
    }

    public boolean isInCheck(String player) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] instanceof King && board[row][col].owner.equals(player))
                    return isAttacked(row, col, other(player));
            }
        }
        return false;
    }

    private boolean isLegal(int posRow, int posCol, int row, int col) {
        if (posRow < 0 || posCol < 0 || row < 0 || col < 0 || posRow > 7 || posCol > 7 || row > 7 || col > 7)
            return false;
        if (posRow == row && posCol == col)
            return false;

        ChessPiece piece = board[posRow][posCol];
        ChessPiece target = board[row][col];
        if (piece == null || !piece.owner.equals(myTurn))
            return false;
        if (target != null && target.owner.equals(myTurn))
            return false;

        boolean possible;
        if (piece instanceof King && posRow == row && Math.abs(col - posCol) == 2)
            possible = canCastle(posRow, posCol, col);
        else if (piece instanceof Pawn && col != posCol && target == null)
            possible = ((Pawn) piece).canAttack(row, col) && isEnPassant(row, col);
        else
            possible = piece.canMove(row, col, board);

        return possible && !leavesKingInCheck(posRow, posCol, row, col);
    }

    private boolean canCastle(int posRow, int posCol, int col) {
        int rookCol = col > posCol ? 7 : 0;
        ChessPiece king = board[posRow][posCol];
        ChessPiece rook = board[posRow][rookCol];
        if (king.hasMoved || !(rook instanceof Rook) || rook.hasMoved || !rook.owner.equals(king.owner))
            return false;

        int step = Integer.signum(rookCol - posCol);
        for (int i = posCol + step; i != rookCol; i += step) {
            if (board[posRow][i] != null)
                return false;
        }
        String enemy = other(myTurn);
        return !isAttacked(posRow, posCol, enemy) && !isAttacked(posRow, posCol + step, enemy);
    }

    private boolean isEnPassant(int row, int col) {
        return lastMove != null
                && board[lastMove.toRow()][lastMove.toCol()] instanceof Pawn
                && Math.abs(lastMove.toRow() - lastMove.fromRow()) == 2
                && col == lastMove.toCol()
                && row == (lastMove.fromRow() + lastMove.toRow()) / 2;
    }

    private boolean leavesKingInCheck(int posRow, int posCol, int row, int col) {
        ChessPiece piece = board[posRow][posCol];
        int capturedRow = row;
        if (piece instanceof Pawn && col != posCol && board[row][col] == null)
            capturedRow = posRow;
        ChessPiece captured = board[capturedRow][col];

        board[capturedRow][col] = null;
        board[row][col] = piece;
        board[posRow][posCol] = null;
        piece.posRow = row;
        piece.posCol = col;

        boolean check = isInCheck(piece.owner);

        piece.posRow = posRow;
        piece.posCol = posCol;
        board[posRow][posCol] = piece;
        board[row][col] = null;
        board[capturedRow][col] = captured;
        return check;
    }

    private boolean isAttacked(int row, int col, String by) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board[r][c];
                if (piece == null || !piece.owner.equals(by))
                    continue;
                if (piece instanceof Pawn) {
                    if (((Pawn) piece).canAttack(row, col))
                        return true;
                } else if (piece.canMove(row, col, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChessPiece createPiece(String letter, int row, int col, String owner) {
        if (letter == null)
            return new Queen(row, col, owner);
        switch (letter) {
            case "Q":
                return new Queen(row, col, owner);
            case "R":
                return new Rook(row, col, owner);
            case "B":
                return new Bishop(row, col, owner);
            case "N":
                return new Knight(row, col, owner);
            default:
                return null;
        }
    }

    public static String other(String player) {
        return player.equals("WHITE") ? "BLACK" : "WHITE";
    }
}
