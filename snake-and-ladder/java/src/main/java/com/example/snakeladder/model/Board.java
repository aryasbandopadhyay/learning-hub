package com.example.snakeladder.model;

import com.example.snakeladder.exception.InvalidBoardException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable board definition. It validates all snakes/ladders up front so game turns stay simple.
 *
 * <p>Cells are numbered 1..size. Cell 1 is the starting square and {@code size} is the winning
 * square. A cell can have at most one outgoing jump; otherwise landing there would be ambiguous.
 */
public class Board {

    public static final int DEFAULT_SIZE = 100;

    private final int size;
    private final List<Cell> cells;
    private final Map<Integer, Jump> jumpsByStart;

    public Board(List<Jump> jumps) {
        this(DEFAULT_SIZE, jumps);
    }

    public Board(int size, List<Jump> jumps) {
        if (size < 2) {
            throw new InvalidBoardException("Board must have at least two cells");
        }
        this.size = size;
        this.jumpsByStart = validateAndIndexJumps(size, jumps);
        this.cells = buildCells(size, jumpsByStart);
    }

    private static Map<Integer, Jump> validateAndIndexJumps(int size, List<Jump> jumps) {
        Map<Integer, Jump> indexed = new HashMap<>();
        for (Jump jump : jumps) {
            if (jump.getFrom() < 1 || jump.getFrom() > size || jump.getTo() < 1 || jump.getTo() > size) {
                throw new InvalidBoardException("Jump endpoints must be inside the board");
            }
            if (indexed.putIfAbsent(jump.getFrom(), jump) != null) {
                throw new InvalidBoardException("Only one snake or ladder may start at cell " + jump.getFrom());
            }
        }
        return Map.copyOf(indexed);
    }

    private static List<Cell> buildCells(int size, Map<Integer, Jump> jumpsByStart) {
        List<Cell> built = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            built.add(new Cell(i, jumpsByStart.get(i)));
        }
        return List.copyOf(built);
    }

    /** Return the final cell after applying a snake/ladder, or the same cell if no jump exists. */
    public int applyJump(int cellNumber) {
        return findJump(cellNumber).map(Jump::getTo).orElse(cellNumber);
    }

    public Optional<Jump> findJump(int cellNumber) {
        return Optional.ofNullable(jumpsByStart.get(cellNumber));
    }

    public int getSize() {
        return size;
    }

    public List<Cell> getCells() {
        return cells;
    }
}
