package com.example.tictactoe.game;

import com.example.tictactoe.exception.InvalidMoveException;
import com.example.tictactoe.model.GameStatus;
import com.example.tictactoe.model.Mark;
import com.example.tictactoe.model.Player;

/**
 * The game engine: validates whose turn it is, delegates board writes, then advances the state.
 * It is intentionally single-threaded/turn-based; callers serialize moves like players do in real play.
 */
public class Game {

    private final Board board;
    private final Player xPlayer;
    private final Player oPlayer;
    private Player currentPlayer;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(Player xPlayer, Player oPlayer) {
        this(Board.DEFAULT_SIZE, xPlayer, oPlayer);
    }

    public Game(int size, Player xPlayer, Player oPlayer) {
        if (xPlayer.mark() != Mark.X || oPlayer.mark() != Mark.O) {
            throw new IllegalArgumentException("Game requires one X player and one O player");
        }
        this.board = new Board(size);
        this.xPlayer = xPlayer;
        this.oPlayer = oPlayer;
        this.currentPlayer = xPlayer;
    }

    public GameStatus makeMove(Player player, int row, int col) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game is already over with status " + status);
        }
        if (!player.equals(currentPlayer)) {
            throw new InvalidMoveException("It is " + currentPlayer.name() + "'s turn");
        }

        board.placeMark(row, col, player.mark());
        if (board.hasWinningLine(player.mark())) {
            status = player.mark() == Mark.X ? GameStatus.X_WON : GameStatus.O_WON;
        } else if (board.isFull()) {
            status = GameStatus.DRAW;
        } else {
            currentPlayer = currentPlayer.equals(xPlayer) ? oPlayer : xPlayer;
        }
        return status;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }
}
