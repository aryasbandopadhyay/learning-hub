"""Thread-safe in-memory file-system service."""

from __future__ import annotations

import threading
from contextlib import contextmanager
from typing import Iterator

from .models import Directory, FileEntry, FileSystemEntry


class ReadWriteLock:
    """Small reader/writer lock built from stdlib primitives.

    Multiple readers may hold the lock together. Writers are exclusive, which is enough for this
    machine-coding MVP because every tree mutation happens under the write side.
    """

    def __init__(self) -> None:
        self._condition = threading.Condition(threading.Lock())
        self._readers = 0
        self._writer = False

    @contextmanager
    def read_lock(self) -> Iterator[None]:
        with self._condition:
            while self._writer:
                self._condition.wait()
            self._readers += 1
        try:
            yield
        finally:
            with self._condition:
                self._readers -= 1
                if self._readers == 0:
                    self._condition.notify_all()

    @contextmanager
    def write_lock(self) -> Iterator[None]:
        with self._condition:
            while self._writer or self._readers > 0:
                self._condition.wait()
            self._writer = True
        try:
            yield
        finally:
            with self._condition:
                self._writer = False
                self._condition.notify_all()


class InMemoryFileSystem:
    """LeetCode-588-style file system backed by a Composite tree.

    Public operations take a read/write lock around the whole traversal. That keeps the
    implementation easy to reason about: readers never see half-created paths, and concurrent
    writers cannot corrupt a directory's child map or a file's content list.
    """

    def __init__(self) -> None:
        self._root = Directory("")
        self._lock = ReadWriteLock()

    def ls(self, path: str) -> list[str]:
        """Return file name for a file, or sorted child names for a directory."""
        with self._lock.read_lock():
            entry = self._traverse(path)
            if isinstance(entry, FileEntry):
                return [entry.name]
            return entry.list_names()

    def mkdir(self, path: str) -> None:
        """Create all missing directories along ``path``."""
        with self._lock.write_lock():
            self._directory_for(path, create=True)

    def add_content_to_file(self, file_path: str, content: str) -> None:
        """Create the file if absent, then append content to it."""
        with self._lock.write_lock():
            parts = self._parts(file_path)
            if not parts:
                raise ValueError("File path must not be root")
            parent_path = "/".join(parts[:-1])
            parent = self._directory_for(parent_path, create=True)
            file_name = parts[-1]
            existing = parent.get_child(file_name)
            if existing is None:
                existing = FileEntry(file_name)
                parent.put_child(existing)
            if not isinstance(existing, FileEntry):
                raise ValueError(f"{file_path} is a directory, not a file")
            existing.append(content)

    def read_content_from_file(self, file_path: str) -> str:
        """Read the complete content of a file."""
        with self._lock.read_lock():
            entry = self._traverse(file_path)
            if not isinstance(entry, FileEntry):
                raise ValueError(f"{file_path} is not a file")
            return entry.read()

    def _traverse(self, path: str) -> FileSystemEntry:
        current: FileSystemEntry = self._root
        for part in self._parts(path):
            if not isinstance(current, Directory):
                raise ValueError(f"Cannot traverse through file: {current.name}")
            child = current.get_child(part)
            if child is None:
                raise ValueError(f"Path does not exist: {path}")
            current = child
        return current

    def _directory_for(self, path: str, create: bool) -> Directory:
        current = self._root
        for part in self._parts(path):
            child = current.get_child(part)
            if child is None:
                if not create:
                    raise ValueError(f"Path does not exist: {path}")
                child = Directory(part)
                current.put_child(child)
            if not isinstance(child, Directory):
                raise ValueError(f"{part} is a file, not a directory")
            current = child
        return current

    def _parts(self, path: str) -> list[str]:
        # Normalize '/', '//a//b', and even 'a/b' into the same segment list.
        return [part for part in path.split("/") if part]
