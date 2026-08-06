package com.example.tictactoe;

import com.example.tictactoe.exception.InvalidMoveException;
import com.example.tictactoe.game.Game;
import com.example.tictactoe.model.GameStatus;
import com.example.tictactoe.model.Mark;
import com.example.tictactoe.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicTacToeTest {

    private final Player x = new Player("Alice", Mark.X);
    private final Player o = new Player("Bob", Mark.O);

    @Test
    void xWinsOnTopRow() {
        Game game = new Game(x, o);
        assertEquals(GameStatus.IN_PROGRESS, game.makeMove(x, 0, 0));
        game.makeMove(o, 1, 0);
        game.makeMove(x, 0, 1);
        game.makeMove(o, 1, 1);
        assertEquals(GameStatus.X_WON, game.makeMove(x, 0, 2));
    }

    @Test
    void xWinsOnMainDiagonal() {
        Game game = new Game(x, o);
        game.makeMove(x, 0, 0);
        game.makeMove(o, 0, 1);
        game.makeMove(x, 1, 1);
        game.makeMove(o, 0, 2);
        assertEquals(GameStatus.X_WON, game.makeMove(x, 2, 2));
    }

    @Test
    void oWinsOnColumn() {
        Game game = new Game(x, o);
        game.makeMove(x, 0, 0);
        game.makeMove(o, 0, 1);
        game.makeMove(x, 1, 0);
        game.makeMove(o, 1, 1);
        game.makeMove(x, 2, 2);
        assertEquals(GameStatus.O_WON, game.makeMove(o, 2, 1));
    }

    @Test
    void drawWhenBoardFillsWithNoWinner() {
        Game game = new Game(x, o);
        game.makeMove(x, 0, 0);
        game.makeMove(o, 0, 1);
        game.makeMove(x, 0, 2);
        game.makeMove(o, 1, 1);
        game.makeMove(x, 1, 0);
        game.makeMove(o, 1, 2);
        game.makeMove(x, 2, 1);
        game.makeMove(o, 2, 0);
        assertEquals(GameStatus.DRAW, game.makeMove(x, 2, 2));
    }

    @Test
    void invalidMovesAreRejectedClearly() {
        Game outOfBounds = new Game(x, o);
        assertThrows(InvalidMoveException.class, () -> outOfBounds.makeMove(x, 3, 0));

        Game occupied = new Game(x, o);
        occupied.makeMove(x, 0, 0);
        assertThrows(InvalidMoveException.class, () -> occupied.makeMove(o, 0, 0));

        Game wrongTurn = new Game(x, o);
        assertThrows(InvalidMoveException.class, () -> wrongTurn.makeMove(o, 0, 0));

        Game over = new Game(x, o);
        over.makeMove(x, 0, 0);
        over.makeMove(o, 1, 0);
        over.makeMove(x, 0, 1);
        over.makeMove(o, 1, 1);
        over.makeMove(x, 0, 2);
        assertThrows(InvalidMoveException.class, () -> over.makeMove(o, 2, 2));
    }
}
