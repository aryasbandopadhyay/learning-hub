"""Runnable demo: build an in-memory tree, compose filters, and print matched paths.

Run:  python -m find.main   (from the python/ directory)
"""

from __future__ import annotations

from .engine import FindEngine
from .filters import AndFilter, ExtensionFilter, NameFilter, SizeFilter, TypeFilter
from .models import DirectoryNode, EntryType, FileNode


def sample_tree() -> DirectoryNode:
    root = DirectoryNode("workspace")
    docs = DirectoryNode("docs")
    docs.add_child(FileNode("readme.txt", 120))
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


def main() -> None:
    root = sample_tree()
    engine = FindEngine()

    print("Text files:")
    for path in engine.find(root, NameFilter("*.txt")):
        print(" ", path)

    print("Large log files:")
    for path in engine.find(root, AndFilter(ExtensionFilter(".log"), SizeFilter.greater_than(1_000))):
        print(" ", path)

    print("Directories:")
    for path in engine.find(root, TypeFilter(EntryType.DIRECTORY)):
        print(" ", path)


if __name__ == "__main__":
    main()
