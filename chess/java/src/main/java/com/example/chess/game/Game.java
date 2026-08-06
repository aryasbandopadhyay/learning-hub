package com.example.chess.game;

import com.example.chess.exception.InvalidMoveException;
import com.example.chess.model.Board;
import com.example.chess.model.Color;
import com.example.chess.model.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Application service for turn-based play.
 *
 * <p>Game enforces orchestration rules (whose turn, source has a piece, no own capture), then
 * delegates geometry to the piece polymorphically. Because chess is turn-based, this MVP needs no
 * concurrency control; a UI/API would serialize one move request at a time.
 */
public class Game {
    private final Board board;
    private final List<Piece> capturedPieces = new ArrayList<>();
    private Color currentTurn;

    public Game() {
        this(new Board(), Color.WHITE);
    }

    public Game(Board board, Color startingTurn) {
        this.board = board;
        this.currentTurn = startingTurn;
    }

    public void makeMove(String from, String to) {
        makeMove(Move.of(from, to));
    }

    public void makeMove(Move move) {
        Piece piece = board.getPiece(move.from())
                .orElseThrow(() -> new InvalidMoveException("No piece at " + move.from().toAlgebraic()));
        if (piece.getColor() != currentTurn) {
            throw new InvalidMoveException("It is " + currentTurn + "'s turn, not " + piece.getColor());
        }
        if (board.hasOwnPiece(move.to(), currentTurn)) {
            throw new InvalidMoveException("Cannot capture your own piece at " + move.to().toAlgebraic());
        }
        if (!piece.isValidMove(board, move.from(), move.to())) {
            throw new InvalidMoveException("Illegal " + piece.getClass().getSimpleName()
                    + " move: " + move.from().toAlgebraic() + " -> " + move.to().toAlgebraic());
        }
        board.getPiece(move.to()).ifPresent(capturedPieces::add);
        board.movePiece(move.from(), move.to());
        currentTurn = currentTurn.opposite();
    }

    public boolean isInCheck(Color color) {
        return board.isInCheck(color);
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public List<Piece> getCapturedPieces() {
        return List.copyOf(capturedPieces);
    }
}
