"""Runnable deterministic demo.

Run:  python -m autocomplete.main   (from the python/ directory)
"""

from __future__ import annotations

from .autocomplete import AutocompleteSystem


def _fmt(items: list[str]) -> str:
    """Match Java List.toString() formatting so both demos print identical output."""
    return "[" + ", ".join(items) + "]"


def main() -> None:
    autocomplete = AutocompleteSystem(
        ["i love you", "island", "i love leetcode", "ironman"],
        [5, 3, 2, 2],
    )

    print("Suggestions for 'i':", _fmt(autocomplete.suggest("i", 3)))
    print("Suggestions for 'i ':", _fmt(autocomplete.suggest("i ", 3)))

    print("Interactive input 'i':", _fmt(autocomplete.input("i")))
    print("Interactive input ' ':", _fmt(autocomplete.input(" ")))
    print("Interactive input 'a':", _fmt(autocomplete.input("a")))
    print("Interactive input '#':", _fmt(autocomplete.input("#")))

    print("Suggestions for 'i a':", _fmt(autocomplete.suggest("i a", 3)))


if __name__ == "__main__":
    main()
