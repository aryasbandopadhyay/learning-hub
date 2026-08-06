package com.example.tictactoe.game;

import com.example.tictactoe.exception.InvalidMoveException;
import com.example.tictactoe.model.Cell;
import com.example.tictactoe.model.Mark;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Owns the NxN grid and all board-level rules: bounds, occupancy, writes, and winner scans.
 * Keeping this logic here lets Game focus only on turn orchestration and state transitions.
 */
public class Board {

    public static final int DEFAULT_SIZE = 3;

    private final int size;
    private final Cell[][] cells;
    private int filledCells;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Board size must be at least 3");
        }
        this.size = size;
        this.cells = new Cell[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cells[r][c] = new Cell();
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Mark markAt(int row, int col) {
        validateBounds(row, col);
        return cells[row][col].getMark();
    }

    public void placeMark(int row, int col, Mark mark) {
        validateBounds(row, col);
        if (!cells[row][col].isEmpty()) {
            throw new InvalidMoveException("Cell (" + row + ", " + col + ") is already occupied");
        }
        cells[row][col].place(mark);
        filledCells++;
    }

    public boolean isFull() {
        return filledCells == size * size;
    }

    public boolean hasWinningLine(Mark mark) {
        return hasWinningRow(mark) || hasWinningColumn(mark) || hasWinningDiagonal(mark);
    }

    private boolean hasWinningRow(Mark mark) {
        for (int r = 0; r < size; r++) {
            boolean complete = true;
            for (int c = 0; c < size; c++) {
                complete = complete && cells[r][c].getMark() == mark;
            }
            if (complete) {
                return true;
            }
        }
        return false;
    }

    private boolean hasWinningColumn(Mark mark) {
        for (int c = 0; c < size; c++) {
            boolean complete = true;
            for (int r = 0; r < size; r++) {
                complete = complete && cells[r][c].getMark() == mark;
            }
            if (complete) {
                return true;
            }
        }
        return false;
    }

    private boolean hasWinningDiagonal(Mark mark) {
        boolean main = true;
        boolean anti = true;
        for (int i = 0; i < size; i++) {
            main = main && cells[i][i].getMark() == mark;
            anti = anti && cells[i][size - 1 - i].getMark() == mark;
        }
        return main || anti;
    }

    private void validateBounds(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new InvalidMoveException("Cell (" + row + ", " + col + ") is outside the " + size + "x" + size + " board");
        }
    }

    @Override
    public String toString() {
        return Arrays.stream(cells)
                .map(row -> Arrays.stream(row)
                        .map(cell -> cell.getMark() == Mark.EMPTY ? "." : cell.getMark().name())
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
