package com.example.demo.assets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChessboardTest {

    private final Chessboard board = new Chessboard();

    @Test
    void whiteStartsWithTwentyMoves() {
        assertThat(board.myTurn).isEqualTo("WHITE");
        assertThat(board.getLegalMoves()).hasSize(20);
        assertThat(board.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void playersMoveInTurns() {
        assertThat(move("e7", "e5")).isFalse();
        assertThat(move("e2", "e4")).isTrue();
        assertThat(move("d2", "d4")).isFalse();
        assertThat(board.myTurn).isEqualTo("BLACK");
    }

    @Test
    void piecesCannotJumpOverOtherPieces() {
        assertThat(move("a1", "a3")).isFalse();
        assertThat(move("d1", "d3")).isFalse();
        assertThat(move("c1", "e3")).isFalse();
        assertThat(move("g1", "f3")).isTrue();
    }

    @Test
    void pawnMovesTwoSquaresOnlyStraightFromItsStartingRow() {
        assertThat(move("e2", "f4")).isFalse();
        play("e2-e4", "a7-a6");
        assertThat(move("e4", "e6")).isFalse();
    }

    @Test
    void movesOutsideTheBoardAreRejected() {
        assertThat(board.movePiece(6, 4, 8, 4, null)).isFalse();
        assertThat(board.movePiece(-1, 0, 0, 0, null)).isFalse();
    }

    @Test
    void foolsMate() {
        play("f2-f3", "e7-e5", "g2-g4", "d8-h4");

        assertThat(board.getStatus()).isEqualTo("CHECKMATE");
        assertThat(board.getLegalMoves()).isEmpty();
    }

    @Test
    void checkHasToBeAnswered() {
        play("e2-e4", "d7-d6", "f1-b5");

        assertThat(board.getStatus()).isEqualTo("CHECK");
        assertThat(move("a7", "a6")).isFalse();
        assertThat(move("c7", "c6")).isTrue();
    }

    @Test
    void pinnedPieceCannotMove() {
        play("e2-e4", "d7-d6", "f1-b5", "c7-c6", "a2-a3");

        assertThat(move("c6", "c5")).isFalse();
    }

    @Test
    void castlingMovesTheRookToo() {
        play("e2-e4", "e7-e5", "g1-f3", "b8-c6", "f1-c4", "f8-c5");

        assertThat(move("e1", "g1")).isTrue();
        assertThat(piece("g1")).isInstanceOf(King.class);
        assertThat(piece("f1")).isInstanceOf(Rook.class);
        assertThat(piece("h1")).isNull();
    }

    @Test
    void cannotCastleAfterTheKingHasMoved() {
        play("e2-e4", "e7-e5", "g1-f3", "b8-c6", "f1-c4", "f8-c5", "e1-e2", "g8-f6", "e2-e1", "f6-g8");

        assertThat(move("e1", "g1")).isFalse();
    }

    @Test
    void cannotCastleThroughAnAttackedSquare() {
        play("e2-e4", "b7-b6", "g1-f3", "c8-a6", "g2-g3", "b8-c6", "f1-h3", "g8-f6");

        assertThat(move("e1", "g1")).isFalse();
    }

    @Test
    void cannotCastleIntoCheck() {
        play("e2-e4", "e7-e5", "f2-f4", "f8-c5", "g1-f3", "d7-d6", "f1-c4", "g8-f6");

        assertThat(move("e1", "g1")).isFalse();
    }

    @Test
    void enPassant() {
        play("e2-e4", "a7-a6", "e4-e5", "d7-d5");

        assertThat(move("e5", "d6")).isTrue();
        assertThat(piece("d5")).isNull();
    }

    @Test
    void enPassantIsOnlyPossibleRightAfterTheDoubleStep() {
        play("e2-e4", "a7-a6", "e4-e5", "d7-d5", "a2-a3", "a6-a5");

        assertThat(move("e5", "d6")).isFalse();
    }

    @Test
    void pawnPromotesToTheChosenPiece() {
        play("h2-h4", "g7-g5", "h4-g5", "f7-f6", "g5-g6", "a7-a6", "g6-h7", "a6-a5");

        assertThat(board.movePiece(row("h7"), col("h7"), row("g8"), col("g8"), "N")).isTrue();
        assertThat(piece("g8")).isInstanceOf(Knight.class);
        assertThat(piece("g8").owner).isEqualTo("WHITE");
    }

    @Test
    void pawnPromotesToQueenByDefault() {
        play("h2-h4", "g7-g5", "h4-g5", "f7-f6", "g5-g6", "a7-a6", "g6-h7", "a6-a5", "h7-g8");

        assertThat(piece("g8")).isInstanceOf(Queen.class);
    }

    @Test
    void stalemate() {
        play("e2-e3", "a7-a5", "d1-h5", "a8-a6", "h5-a5", "h7-h5", "h2-h4", "a6-h6", "a5-c7", "f7-f6",
                "c7-d7", "e8-f7", "d7-b7", "d8-d3", "b7-b8", "d3-h7", "b8-c8", "f7-g6", "c8-e6");

        assertThat(board.getStatus()).isEqualTo("STALEMATE");
    }

    private void play(String... moves) {
        for (String move : moves) {
            String[] squares = move.split("-");
            assertThat(move(squares[0], squares[1])).as(move).isTrue();
        }
    }

    private boolean move(String from, String to) {
        return board.movePiece(row(from), col(from), row(to), col(to), null);
    }

    private ChessPiece piece(String square) {
        return board.board[row(square)][col(square)];
    }

    private static int row(String square) {
        return '8' - square.charAt(1);
    }

    private static int col(String square) {
        return square.charAt(0) - 'a';
    }
}
