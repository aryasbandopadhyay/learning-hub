"""End-to-end tests for the Trie Autocomplete MVP."""

from __future__ import annotations

from autocomplete import AutocompleteSystem


def sample_system() -> AutocompleteSystem:
    return AutocompleteSystem(
        ["i love you", "island", "i love leetcode", "ironman"],
        [5, 3, 2, 2],
    )


def test_suggestions_rank_by_frequency_then_lexicographic_order():
    system = AutocompleteSystem(
        ["app", "apple", "ape", "apricot"],
        [4, 4, 2, 4],
    )

    assert system.suggest("ap", 10) == ["app", "apple", "apricot", "ape"]


def test_k_limit_is_respected():
    system = sample_system()

    assert system.suggest("i", 2) == ["i love you", "island"]


def test_prefix_with_no_matches_returns_empty_list():
    system = sample_system()

    assert system.suggest("z", 3) == []


def test_updating_frequency_changes_ranking():
    system = AutocompleteSystem(["hello", "helium"], [2, 5])

    assert system.suggest("hel", 2) == ["helium", "hello"]
    system.add_term("hello", 4)  # hello now has 6 total occurrences.
    assert system.suggest("hel", 2) == ["hello", "helium"]


def test_longer_corpus_and_interactive_commit_work_together():
    system = AutocompleteSystem(
        [
            "salesforce crm",
            "salesforce cloud",
            "salesforce careers",
            "search autocomplete",
            "search analytics",
            "service cloud",
            "slack integration",
            "sandbox refresh",
            "schema builder",
        ],
        [8, 7, 3, 6, 4, 5, 2, 2, 1],
    )

    assert system.suggest("sales", 3) == [
        "salesforce crm",
        "salesforce cloud",
        "salesforce careers",
    ]
    assert system.suggest("search", 5) == ["search autocomplete", "search analytics"]

    assert system.input("s") == ["salesforce crm", "salesforce cloud", "search autocomplete"]
    assert system.input("a") == ["salesforce crm", "salesforce cloud", "salesforce careers"]
    assert system.input("a") == []
    assert system.input("#") == []
    assert system.suggest("saa", 3) == ["saa"]
