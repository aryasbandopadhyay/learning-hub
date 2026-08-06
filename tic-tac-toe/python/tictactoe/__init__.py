"""Tic Tac Toe — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one:

    models.py      -> Mark, GameStatus, Player, Cell, Board
    game.py        -> Game (the orchestrating engine)
    exceptions.py  -> InvalidMoveError
    main.py        -> runnable demo

Thread-safety: not needed here because one Tic Tac Toe game is single-threaded and turn-based.
"""
