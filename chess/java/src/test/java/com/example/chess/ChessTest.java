package com.example.chess;

import com.example.chess.exception.InvalidMoveException;
import com.example.chess.game.Game;
import com.example.chess.model.Board;
import com.example.chess.model.Cell;
import com.example.chess.model.Color;
import com.example.chess.model.pieces.Bishop;
import com.example.chess.model.pieces.King;
import com.example.chess.model.pieces.Knight;
import com.example.chess.model.pieces.Pawn;
import com.example.chess.model.pieces.Piece;
import com.example.chess.model.pieces.Queen;
import com.example.chess.model.pieces.Rook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessTest {

    private Game gameWithWhitePiece(String square, Piece piece) {
        Board board = Board.empty();
        board.setPiece(Cell.fromAlgebraic(square), piece);
        return new Game(board, Color.WHITE);
    }

    @Test
    void rookMovesStraightButNotDiagonal() {
        Game game = gameWithWhitePiece("a1", new Rook(Color.WHITE));
        game.makeMove("a1", "a4");
        assertTrue(game.getBoard().getPiece(Cell.fromAlgebraic("a4")).orElseThrow() instanceof Rook);

        Game illegal = gameWithWhitePiece("a1", new Rook(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> illegal.makeMove("a1", "b2"));
    }

    @Test
    void bishopMovesDiagonalButNotStraight() {
        Game game = gameWithWhitePiece("c1", new Bishop(Color.WHITE));
        game.makeMove("c1", "g5");
        assertTrue(game.getBoard().getPiece(Cell.fromAlgebraic("g5")).orElseThrow() instanceof Bishop);

        Game illegal = gameWithWhitePiece("c1", new Bishop(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> illegal.makeMove("c1", "c3"));
    }

    @Test
    void knightMovesInLShapeAndCanJump() {
        Board board = Board.empty();
        board.setPiece(Cell.fromAlgebraic("g1"), new Knight(Color.WHITE));
        board.setPiece(Cell.fromAlgebraic("g2"), new Pawn(Color.WHITE)); // would block sliders, not a knight
        Game game = new Game(board, Color.WHITE);
        game.makeMove("g1", "f3");
        assertTrue(game.getBoard().getPiece(Cell.fromAlgebraic("f3")).orElseThrow() instanceof Knight);

        Game illegal = gameWithWhitePiece("g1", new Knight(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> illegal.makeMove("g1", "g3"));
    }

    @Test
    void pawnMovesForwardRejectsSidewaysAndCapturesDiagonally() {
        Game forward = gameWithWhitePiece("e2", new Pawn(Color.WHITE));
        forward.makeMove("e2", "e4");
        assertTrue(forward.getBoard().getPiece(Cell.fromAlgebraic("e4")).orElseThrow() instanceof Pawn);

        Game sideways = gameWithWhitePiece("e2", new Pawn(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> sideways.makeMove("e2", "f2"));

        Board board = Board.empty();
        board.setPiece(Cell.fromAlgebraic("e4"), new Pawn(Color.WHITE));
        board.setPiece(Cell.fromAlgebraic("d5"), new Pawn(Color.BLACK));
        Game capture = new Game(board, Color.WHITE);
        capture.makeMove("e4", "d5");
        assertEquals(1, capture.getCapturedPieces().size());
    }

    @Test
    void kingMovesOneSquareButNotTwo() {
        Game game = gameWithWhitePiece("e1", new King(Color.WHITE));
        game.makeMove("e1", "e2");
        assertTrue(game.getBoard().getPiece(Cell.fromAlgebraic("e2")).orElseThrow() instanceof King);

        Game illegal = gameWithWhitePiece("e1", new King(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> illegal.makeMove("e1", "e3"));
    }

    @Test
    void queenMovesStraightAndDiagonalButRejectsKnightShape() {
        Game straight = gameWithWhitePiece("d1", new Queen(Color.WHITE));
        straight.makeMove("d1", "d4");

        Game diagonal = gameWithWhitePiece("d1", new Queen(Color.WHITE));
        diagonal.makeMove("d1", "h5");

        Game illegal = gameWithWhitePiece("d1", new Queen(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> illegal.makeMove("d1", "e3"));
    }

    @Test
    void slidingPiecesCannotMoveThroughBlockers() {
        Board rookBoard = Board.empty();
        rookBoard.setPiece(Cell.fromAlgebraic("a1"), new Rook(Color.WHITE));
        rookBoard.setPiece(Cell.fromAlgebraic("a2"), new Pawn(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> new Game(rookBoard, Color.WHITE).makeMove("a1", "a4"));

        Board bishopBoard = Board.empty();
        bishopBoard.setPiece(Cell.fromAlgebraic("c1"), new Bishop(Color.WHITE));
        bishopBoard.setPiece(Cell.fromAlgebraic("d2"), new Pawn(Color.BLACK));
        assertThrows(InvalidMoveException.class, () -> new Game(bishopBoard, Color.WHITE).makeMove("c1", "e3"));
    }

    @Test
    void cannotCaptureOwnPieceButEnemyCaptureIsRecorded() {
        Board own = Board.empty();
        own.setPiece(Cell.fromAlgebraic("a1"), new Rook(Color.WHITE));
        own.setPiece(Cell.fromAlgebraic("a4"), new Pawn(Color.WHITE));
        assertThrows(InvalidMoveException.class, () -> new Game(own, Color.WHITE).makeMove("a1", "a4"));

        Board enemy = Board.empty();
        enemy.setPiece(Cell.fromAlgebraic("a1"), new Rook(Color.WHITE));
        enemy.setPiece(Cell.fromAlgebraic("a4"), new Pawn(Color.BLACK));
        Game game = new Game(enemy, Color.WHITE);
        game.makeMove("a1", "a4");
        assertEquals(1, game.getCapturedPieces().size());
        assertTrue(game.getBoard().getPiece(Cell.fromAlgebraic("a4")).orElseThrow() instanceof Rook);
    }

    @Test
    void turnEnforcementRejectsMovingOutOfTurn() {
        Game game = new Game();
        assertThrows(InvalidMoveException.class, () -> game.makeMove("e7", "e5"));
    }

    @Test
    void simpleCheckDetectionFindsAttackedKing() {
        Board board = Board.empty();
        board.setPiece(Cell.fromAlgebraic("e1"), new King(Color.WHITE));
        board.setPiece(Cell.fromAlgebraic("e8"), new Rook(Color.BLACK));
        assertTrue(new Game(board, Color.WHITE).isInCheck(Color.WHITE));

        board.setPiece(Cell.fromAlgebraic("e4"), new Pawn(Color.WHITE));
        assertFalse(new Game(board, Color.WHITE).isInCheck(Color.WHITE));
    }
}
