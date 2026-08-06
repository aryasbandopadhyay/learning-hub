"""Read-only DFS traversal engine for the in-memory file tree."""

from __future__ import annotations

from .filters import Filter
from .models import DirectoryNode, FileSystemEntry


class FindEngine:
    """Traverse separately from matching, delegating all criteria to Filter objects.

    A Visitor could be introduced later if traversal needed multiple actions (print, delete, exec),
    but returning matches is enough for this MVP.
    """

    def find(self, root: DirectoryNode, filter_: Filter) -> list[str]:
        paths: list[str] = []
        self._dfs(root, f"/{root.name}", 0, filter_, paths, None)
        return paths

    def find_entries(self, root: DirectoryNode, filter_: Filter) -> list[FileSystemEntry]:
        entries: list[FileSystemEntry] = []
        self._dfs(root, f"/{root.name}", 0, filter_, None, entries)
        return entries

    def _dfs(
        self,
        entry: FileSystemEntry,
        path: str,
        depth: int,
        filter_: Filter,
        paths: list[str] | None,
        entries: list[FileSystemEntry] | None,
    ) -> None:
        if filter_.matches(entry, depth):
            if paths is not None:
                paths.append(path)
            if entries is not None:
                entries.append(entry)
        if isinstance(entry, DirectoryNode):
            for child in entry.children:
                self._dfs(child, f"{path}/{child.name}", depth + 1, filter_, paths, entries)
