"""Composite model nodes for the in-memory file-system tree."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field


@dataclass
class FileSystemEntry(ABC):
    """Common abstraction for both files and directories.

    This is the base of the Composite pattern: leaf nodes (``FileEntry``) and composite nodes
    (``Directory``) share the same parent type, so the service can traverse a tree of entries.
    """

    name: str

    @property
    @abstractmethod
    def is_file(self) -> bool:
        """Return True for leaf file nodes."""

    @property
    def is_directory(self) -> bool:
        return not self.is_file


@dataclass
class Directory(FileSystemEntry):
    """Composite node that owns named child entries."""

    children: dict[str, FileSystemEntry] = field(default_factory=dict)

    @property
    def is_file(self) -> bool:
        return False

    def get_child(self, name: str) -> FileSystemEntry | None:
        return self.children.get(name)

    def put_child(self, entry: FileSystemEntry) -> None:
        self.children[entry.name] = entry

    def list_names(self) -> list[str]:
        # Sorting at read time keeps writes O(1) and still matches LeetCode's lexicographic ls.
        return sorted(self.children)


@dataclass
class FileEntry(FileSystemEntry):
    """Leaf node storing append-only text content."""

    _content_parts: list[str] = field(default_factory=list)

    @property
    def is_file(self) -> bool:
        return True

    def append(self, text: str) -> None:
        self._content_parts.append(text)

    def read(self) -> str:
        return "".join(self._content_parts)
