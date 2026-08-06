"""Domain models: FileSystemEntry, FileNode, and DirectoryNode.

The tree uses the Composite pattern: FindEngine can traverse every node through the shared
FileSystemEntry abstraction while DirectoryNode alone stores children.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from enum import Enum
from pathlib import PurePosixPath

from .exceptions import InvalidFileSystemError


class EntryType(Enum):
    FILE = "FILE"
    DIRECTORY = "DIRECTORY"


class FileSystemEntry(ABC):
    """Base type in the Composite pattern: both files and directories have name + size."""

    def __init__(self, name: str, size_bytes: int) -> None:
        if not name:
            raise ValueError("entry name must be non-empty")
        if size_bytes < 0:
            raise ValueError("size must be non-negative")
        self.name = name
        self.size_bytes = size_bytes

    @property
    @abstractmethod
    def type(self) -> EntryType:
        """Whether this node is a file or directory."""

    @property
    def is_file(self) -> bool:
        return self.type is EntryType.FILE

    @property
    def is_directory(self) -> bool:
        return self.type is EntryType.DIRECTORY


class FileNode(FileSystemEntry):
    """Leaf node in the Composite pattern. A file has content size and an extension."""

    def __init__(self, name: str, size_bytes: int) -> None:
        super().__init__(name, size_bytes)
        suffix = PurePosixPath(name).suffix
        self.extension = suffix if suffix else ""

    @property
    def type(self) -> EntryType:
        return EntryType.FILE


class DirectoryNode(FileSystemEntry):
    """Composite node: a directory can contain files and other directories.

    Children are returned as a tuple so callers cannot mutate the tree during a read-only search.
    """

    def __init__(self, name: str) -> None:
        super().__init__(name, 0)
        self._children: list[FileSystemEntry] = []

    @property
    def type(self) -> EntryType:
        return EntryType.DIRECTORY

    def add_child(self, child: FileSystemEntry) -> "DirectoryNode":
        if child is None:
            raise InvalidFileSystemError("child cannot be None")
        if any(existing.name == child.name for existing in self._children):
            raise InvalidFileSystemError(f"duplicate child name: {child.name}")
        self._children.append(child)
        return self

    @property
    def children(self) -> tuple[FileSystemEntry, ...]:
        return tuple(self._children)
