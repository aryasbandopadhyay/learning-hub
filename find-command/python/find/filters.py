"""Composable search filters using the Specification pattern."""

from __future__ import annotations

import fnmatch
from abc import ABC, abstractmethod
from enum import Enum

from .models import EntryType, FileNode, FileSystemEntry


class Filter(ABC):
    """Specification abstraction: one object answers one matching question."""

    @abstractmethod
    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        """Return True when the entry satisfies this filter."""


class NameFilter(Filter):
    """Match entry names exactly, or with shell-style glob wildcards '*' and '?'."""

    def __init__(self, pattern: str) -> None:
        self.pattern = pattern
        self._is_glob = "*" in pattern or "?" in pattern

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        if self._is_glob:
            return fnmatch.fnmatchcase(entry.name, self.pattern)
        return entry.name == self.pattern


class ExtensionFilter(Filter):
    """Match only files with a normalized extension such as '.txt'."""

    def __init__(self, extension: str) -> None:
        self.extension = extension if extension.startswith(".") else f".{extension}"

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return isinstance(entry, FileNode) and entry.extension == self.extension


class SizeComparison(Enum):
    GREATER_THAN = "GREATER_THAN"
    LESS_THAN = "LESS_THAN"
    EQUAL_TO = "EQUAL_TO"


class SizeFilter(Filter):
    """Match entries by size using either min/max bounds or a single comparison operator."""

    def __init__(
        self,
        min_size: int | None = None,
        max_size: int | None = None,
        comparison: SizeComparison | None = None,
        compare_to: int | None = None,
    ) -> None:
        self.min_size = min_size
        self.max_size = max_size
        self.comparison = comparison
        self.compare_to = compare_to

    @classmethod
    def greater_than(cls, bytes_: int) -> "SizeFilter":
        return cls(comparison=SizeComparison.GREATER_THAN, compare_to=bytes_)

    @classmethod
    def less_than(cls, bytes_: int) -> "SizeFilter":
        return cls(comparison=SizeComparison.LESS_THAN, compare_to=bytes_)

    @classmethod
    def equal_to(cls, bytes_: int) -> "SizeFilter":
        return cls(comparison=SizeComparison.EQUAL_TO, compare_to=bytes_)

    @classmethod
    def between(cls, min_size: int, max_size: int) -> "SizeFilter":
        return cls(min_size=min_size, max_size=max_size)

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        size = entry.size_bytes
        if self.comparison is SizeComparison.GREATER_THAN:
            return size > self.compare_to
        if self.comparison is SizeComparison.LESS_THAN:
            return size < self.compare_to
        if self.comparison is SizeComparison.EQUAL_TO:
            return size == self.compare_to
        return (self.min_size is None or size >= self.min_size) and (
            self.max_size is None or size <= self.max_size
        )


class TypeFilter(Filter):
    """Match files or directories, just like `find -type f` / `find -type d`."""

    def __init__(self, entry_type: EntryType) -> None:
        self.entry_type = entry_type

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return entry.type is self.entry_type


class MinDepthFilter(Filter):
    """Match only entries at or below a minimum traversal depth; root is depth 0."""

    def __init__(self, min_depth: int) -> None:
        if min_depth < 0:
            raise ValueError("min_depth must be non-negative")
        self.min_depth = min_depth

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return depth >= self.min_depth


class AndFilter(Filter):
    """Combine specifications with logical AND; all children must match."""

    def __init__(self, *filters: Filter) -> None:
        self.filters = tuple(filters)

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return all(f.matches(entry, depth) for f in self.filters)


class OrFilter(Filter):
    """Combine specifications with logical OR; any child may match."""

    def __init__(self, *filters: Filter) -> None:
        self.filters = tuple(filters)

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return any(f.matches(entry, depth) for f in self.filters)


class NotFilter(Filter):
    """Negate a specification, mirroring `find ! <expression>`."""

    def __init__(self, delegate: Filter) -> None:
        self.delegate = delegate

    def matches(self, entry: FileSystemEntry, depth: int = 0) -> bool:
        return not self.delegate.matches(entry, depth)
