package com.example.snakeladder;

import com.example.snakeladder.game.Game;
import com.example.snakeladder.game.MoveResult;
import com.example.snakeladder.game.ScriptedDice;
import com.example.snakeladder.model.Board;
import com.example.snakeladder.model.Jump;
import com.example.snakeladder.model.Player;

import java.util.List;

/** Runnable deterministic demo. Real games can swap ScriptedDice for RandomDice. */
public class Main {

    public static void main(String[] args) {
        Board board = new Board(10, List.of(
                Jump.ladder(4, 8),
                Jump.snake(9, 5)));
        Game game = new Game(
                board,
                new ScriptedDice(List.of(3, 4, 2)),
                List.of(new Player("Alice"), new Player("Bob")));

        int turn = 1;
        while (game.getWinner().isEmpty()) {
            printTurn(turn++, game.playTurn());
        }
        System.out.println("Winner: " + game.getWinner().get().getName());
    }

    private static void printTurn(int turn, MoveResult result) {
        StringBuilder line = new StringBuilder()
                .append("Turn ").append(turn).append(": ")
                .append(result.getPlayerName()).append(" rolled ").append(result.getRoll())
                .append(" and moved ").append(result.getFrom()).append(" -> ").append(result.getAttempted());
        result.getJump().ifPresent(jump -> line
                .append(jump.isLadder() ? ", ladder to " : ", snake to ")
                .append(jump.getTo()));
        if (result.hasWon()) {
            line.append(" and won");
        }
        System.out.println(line);
    }
}
