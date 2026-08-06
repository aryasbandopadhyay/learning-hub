package com.example.chess;

import com.example.chess.game.Game;

/** Runnable demo: play a tiny legal opening and print the resulting turn/capture state. */
public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        System.out.println("Starting turn: " + game.getCurrentTurn());

        game.makeMove("e2", "e4");
        System.out.println("White plays e2 -> e4");
        game.makeMove("e7", "e5");
        System.out.println("Black plays e7 -> e5");
        game.makeMove("g1", "f3");
        System.out.println("White plays g1 -> f3");
        game.makeMove("b8", "c6");
        System.out.println("Black plays b8 -> c6");

        System.out.println("Next turn: " + game.getCurrentTurn());
        System.out.println("Captured pieces: " + game.getCapturedPieces().size());
    }
}
