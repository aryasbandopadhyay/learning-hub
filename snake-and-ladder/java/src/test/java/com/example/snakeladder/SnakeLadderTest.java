package com.example.snakeladder;

import com.example.snakeladder.exception.InvalidBoardException;
import com.example.snakeladder.game.Game;
import com.example.snakeladder.game.MoveResult;
import com.example.snakeladder.game.ScriptedDice;
import com.example.snakeladder.model.Board;
import com.example.snakeladder.model.Jump;
import com.example.snakeladder.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeLadderTest {

    @Test
    void ladderMovesPlayerUp() {
        Game game = new Game(
                new Board(20, List.of(Jump.ladder(4, 12))),
                new ScriptedDice(List.of(3)),
                List.of(new Player("Alice")));

        MoveResult result = game.playTurn();

        assertEquals(1, result.getFrom());
        assertEquals(4, result.getAttempted());
        assertEquals(12, result.getTo());
        assertEquals(12, game.getPlayers().get(0).getPosition());
        assertTrue(result.getJump().get().isLadder());
    }

    @Test
    void snakeMovesPlayerDown() {
        Game game = new Game(
                new Board(20, List.of(Jump.snake(4, 2))),
                new ScriptedDice(List.of(3)),
                List.of(new Player("Alice")));

        MoveResult result = game.playTurn();

        assertEquals(4, result.getAttempted());
        assertEquals(2, result.getTo());
        assertEquals(2, game.getPlayers().get(0).getPosition());
    }

    @Test
    void deterministicGameProducesExpectedWinner() {
        Game game = new Game(
                new Board(10, List.of(Jump.ladder(4, 8), Jump.snake(9, 5))),
                new ScriptedDice(List.of(3, 4, 2)),
                List.of(new Player("Alice"), new Player("Bob")));

        Player winner = game.playToCompletion();

        assertEquals("Alice", winner.getName());
        assertEquals(10, winner.getPosition());
    }

    @Test
    void overshootLeavesPlayerInPlace() {
        Game game = new Game(
                new Board(10, List.of()),
                new ScriptedDice(List.of(6, 5)),
                List.of(new Player("Alice")));

        game.playTurn(); // Alice: 1 -> 7
        MoveResult overshoot = game.playTurn(); // Alice: 7 + 5 would pass 10

        assertEquals(7, overshoot.getFrom());
        assertEquals(7, overshoot.getAttempted());
        assertEquals(7, overshoot.getTo());
        assertEquals(7, game.getPlayers().get(0).getPosition());
        assertTrue(game.getWinner().isEmpty());
    }

    @Test
    void boardRejectsDuplicateJumpStarts() {
        assertThrows(InvalidBoardException.class,
                () -> new Board(20, List.of(Jump.ladder(4, 12), Jump.snake(4, 2))));
    }
}
