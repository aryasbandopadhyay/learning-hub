package com.example.snakeladder.game;

import com.example.snakeladder.exception.GameAlreadyOverException;
import com.example.snakeladder.model.Board;
import com.example.snakeladder.model.Jump;
import com.example.snakeladder.model.Player;

import java.util.List;
import java.util.Optional;

/**
 * Turn orchestrator. It owns only game progression; board rules and dice policy stay injected.
 *
 * <p>Overshoot rule used here: if a roll would pass the final cell, the token does not move. The
 * player must reach the last cell exactly. This is documented because real variants differ.
 */
public class Game {

    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private int currentPlayerIndex;
    private Player winner;

    public Game(Board board, Dice dice, List<Player> players) {
        if (players.isEmpty()) {
            throw new IllegalArgumentException("At least one player is required");
        }
        this.board = board;
        this.dice = dice;
        this.players = List.copyOf(players);
    }

    public MoveResult playTurn() {
        if (winner != null) {
            throw new GameAlreadyOverException("Game already has a winner: " + winner.getName());
        }

        Player player = players.get(currentPlayerIndex);
        int from = player.getPosition();
        int roll = dice.roll();
        int attempted = from + roll;

        Jump jump = null;
        int to = attempted;
        if (attempted > board.getSize()) {
            // Exact-roll-to-win MVP choice: too far means no movement at all.
            attempted = from;
            to = from;
        } else {
            jump = board.findJump(attempted).orElse(null);
            to = board.applyJump(attempted);
            player.moveTo(to);
        }

        boolean won = to == board.getSize();
        if (won) {
            winner = player;
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        return new MoveResult(player.getName(), roll, from, attempted, to, jump, won);
    }

    public Player playToCompletion() {
        while (winner == null) {
            playTurn();
        }
        return winner;
    }

    public Optional<Player> getWinner() {
        return Optional.ofNullable(winner);
    }

    public List<Player> getPlayers() {
        return players;
    }
}
