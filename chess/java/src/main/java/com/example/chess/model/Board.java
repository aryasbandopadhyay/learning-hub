package com.example.chess.model;

import com.example.chess.model.pieces.Bishop;
import com.example.chess.model.pieces.King;
import com.example.chess.model.pieces.Knight;
import com.example.chess.model.pieces.Pawn;
import com.example.chess.model.pieces.Piece;
import com.example.chess.model.pieces.Queen;
import com.example.chess.model.pieces.Rook;

import java.util.Optional;

/**
 * 8x8 board with piece storage and board-level helpers.
 *
 * <p>The board knows where pieces are and whether a sliding path is clear. It does not decide whose
 * turn it is; that orchestration belongs to {@code Game}. This separation keeps model rules focused.
 */
public class Board {
    public static final int SIZE = 8;

    private final Piece[][] grid = new Piece[SIZE][SIZE];

    /** Creates a standard chess starting position. */
    public Board() {
        setupInitialPosition();
    }

    private Board(boolean setup) {
        if (setup) {
            setupInitialPosition();
        }
    }

    /** Factory used by tests and demos that want to place only a few pieces. */
    public static Board empty() {
        return new Board(false);
    }

    public Optional<Piece> getPiece(Cell cell) {
        return Optional.ofNullable(grid[cell.row()][cell.col()]);
    }

    public void setPiece(Cell cell, Piece piece) {
        grid[cell.row()][cell.col()] = piece;
    }

    public Piece removePiece(Cell cell) {
        Piece piece = grid[cell.row()][cell.col()];
        grid[cell.row()][cell.col()] = null;
        return piece;
    }

    public void movePiece(Cell from, Cell to) {
        Piece piece = removePiece(from);
        setPiece(to, piece);
    }

    public boolean isEmpty(Cell cell) {
        return grid[cell.row()][cell.col()] == null;
    }

    public boolean hasOwnPiece(Cell cell, Color color) {
        return getPiece(cell).map(p -> p.getColor() == color).orElse(false);
    }

    public boolean hasEnemyPiece(Cell cell, Color color) {
        return getPiece(cell).map(p -> p.getColor() != color).orElse(false);
    }

    /** True when all squares strictly between {@code from} and {@code to} are empty. */
    public boolean isPathClear(Cell from, Cell to) {
        int rowStep = Integer.compare(to.row(), from.row());
        int colStep = Integer.compare(to.col(), from.col());
        int row = from.row() + rowStep;
        int col = from.col() + colStep;
        while (row != to.row() || col != to.col()) {
            if (grid[row][col] != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    /** Locate a king, then ask every enemy piece polymorphically whether it attacks that square. */
    public boolean isInCheck(Color color) {
        Cell kingCell = findKing(color).orElseThrow(() -> new IllegalStateException("No king for " + color));
        return isSquareAttacked(kingCell, color.opposite());
    }

    public boolean isSquareAttacked(Cell target, Color byColor) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getColor() == byColor
                        && piece.isValidMove(this, new Cell(r, c), target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Optional<Cell> findKing(Color color) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece piece = grid[r][c];
                if (piece instanceof King && piece.getColor() == color) {
                    return Optional.of(new Cell(r, c));
                }
            }
        }
        return Optional.empty();
    }

    private void setupInitialPosition() {
        setBackRank(0, Color.BLACK);
        setPawns(1, Color.BLACK);
        setPawns(6, Color.WHITE);
        setBackRank(7, Color.WHITE);
    }

    private void setPawns(int row, Color color) {
        for (int col = 0; col < SIZE; col++) {
            grid[row][col] = new Pawn(color);
        }
    }

    private void setBackRank(int row, Color color) {
        grid[row][0] = new Rook(color);
        grid[row][1] = new Knight(color);
        grid[row][2] = new Bishop(color);
        grid[row][3] = new Queen(color);
        grid[row][4] = new King(color);
        grid[row][5] = new Bishop(color);
        grid[row][6] = new Knight(color);
        grid[row][7] = new Rook(color);
    }
}
