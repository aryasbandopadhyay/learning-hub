"""End-to-end tests for the in-memory Find Command MVP."""

from __future__ import annotations

from find.engine import FindEngine
from find.filters import (
    AndFilter,
    ExtensionFilter,
    MinDepthFilter,
    NameFilter,
    NotFilter,
    OrFilter,
    SizeFilter,
    TypeFilter,
)
from find.models import DirectoryNode, EntryType, FileNode


def make_tree() -> DirectoryNode:
    root = DirectoryNode("workspace")
    docs = DirectoryNode("docs")
    docs.add_child(FileNode("readme.txt", 120))
    docs.add_child(FileNode("guide.txt", 1_500))
    docs.add_child(FileNode("design.md", 300))

    src = DirectoryNode("src")
    src.add_child(FileNode("app.py", 900))
    logs = DirectoryNode("logs")
    logs.add_child(FileNode("app.log", 2_048))
    logs.add_child(FileNode("old.log", 500))
    src.add_child(logs)

    root.add_child(docs)
    root.add_child(src)
    root.add_child(FileNode("notes.txt", 2_000))
    return root


def test_name_filter_with_glob_returns_txt_files():
    paths = FindEngine().find(make_tree(), NameFilter("*.txt"))
    assert paths == [
        "/workspace/docs/readme.txt",
        "/workspace/docs/guide.txt",
        "/workspace/notes.txt",
    ]


def test_extension_filter_returns_matching_files():
    paths = FindEngine().find(make_tree(), ExtensionFilter(".log"))
    assert paths == ["/workspace/src/logs/app.log", "/workspace/src/logs/old.log"]


def test_size_filter_returns_large_entries():
    paths = FindEngine().find(make_tree(), SizeFilter.greater_than(1_000))
    assert paths == [
        "/workspace/docs/guide.txt",
        "/workspace/src/logs/app.log",
        "/workspace/notes.txt",
    ]


def test_type_filter_directory_returns_only_directories():
    paths = FindEngine().find(make_tree(), TypeFilter(EntryType.DIRECTORY))
    assert paths == ["/workspace", "/workspace/docs", "/workspace/src", "/workspace/src/logs"]


def test_and_filter_composes_name_and_size():
    paths = FindEngine().find(
        make_tree(), AndFilter(NameFilter("*.txt"), SizeFilter.greater_than(1_000))
    )
    assert paths == ["/workspace/docs/guide.txt", "/workspace/notes.txt"]


def test_or_and_not_filters_compose_across_two_levels():
    paths = FindEngine().find(
        make_tree(),
        AndFilter(
            OrFilter(ExtensionFilter(".txt"), ExtensionFilter(".md")),
            NotFilter(NameFilter("readme.txt")),
        ),
    )
    assert paths == [
        "/workspace/docs/guide.txt",
        "/workspace/docs/design.md",
        "/workspace/notes.txt",
    ]


def test_traversal_reaches_nested_directories_with_min_depth():
    paths = FindEngine().find(make_tree(), AndFilter(MinDepthFilter(3), NameFilter("*.log")))
    assert paths == ["/workspace/src/logs/app.log", "/workspace/src/logs/old.log"]
