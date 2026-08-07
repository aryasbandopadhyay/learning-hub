"""Runnable demo for the In-Memory File System MVP.

Run:  python -m filesystem.main   (from the python/ directory)
"""

from __future__ import annotations

from .fs import InMemoryFileSystem


def _fmt(names: list[str]) -> str:
    return "[" + ", ".join(names) + "]"


def main() -> None:
    fs = InMemoryFileSystem()

    fs.mkdir("/docs/projects")
    fs.add_content_to_file("/docs/projects/notes.txt", "Hello")
    fs.add_content_to_file("/docs/projects/notes.txt", ", FileSystem!")

    print(f"ls / -> {_fmt(fs.ls('/'))}")
    print(f"ls /docs/projects -> {_fmt(fs.ls('/docs/projects'))}")
    print(
        "read /docs/projects/notes.txt -> "
        f"{fs.read_content_from_file('/docs/projects/notes.txt')}"
    )
    print(f"ls /docs/projects/notes.txt -> {_fmt(fs.ls('/docs/projects/notes.txt'))}")


if __name__ == "__main__":
    main()
