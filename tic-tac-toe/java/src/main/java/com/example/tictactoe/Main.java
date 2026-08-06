package com.example.tictactoe;

import com.example.tictactoe.game.Game;
import com.example.tictactoe.model.Mark;
import com.example.tictactoe.model.Player;

/** Runnable demo: a short deterministic game where X wins the top row. */
public class Main {

    public static void main(String[] args) {
        Player x = new Player("Alice", Mark.X);
        Player o = new Player("Bob", Mark.O);
        Game game = new Game(x, o);

        System.out.println("Starting Tic Tac Toe (3x3)");
        play(game, x, 0, 0);
        play(game, o, 1, 0);
        play(game, x, 0, 1);
        play(game, o, 1, 1);
        play(game, x, 0, 2);
        System.out.println("Final status: " + game.getStatus());
        System.out.println(game.getBoard());
    }

    private static void play(Game game, Player player, int row, int col) {
        System.out.println(player.name() + " places " + player.mark() + " at (" + row + ", " + col + ")");
        game.makeMove(player, row, col);
    }
}
