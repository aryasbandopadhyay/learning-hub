"""End-to-end tests for the In-Memory File System MVP."""

from __future__ import annotations

import threading

from filesystem import InMemoryFileSystem


def test_mkdir_creates_nested_directories_and_ls_shows_children() -> None:
    fs = InMemoryFileSystem()
    fs.mkdir("/a/b/c")
    assert fs.ls("/") == ["a"]
    assert fs.ls("/a") == ["b"]
    assert fs.ls("/a/b") == ["c"]


def test_add_content_creates_appends_and_reads_file() -> None:
    fs = InMemoryFileSystem()
    fs.add_content_to_file("/a/b/file.txt", "Hello")
    fs.add_content_to_file("/a/b/file.txt", " World")
    assert fs.read_content_from_file("/a/b/file.txt") == "Hello World"


def test_ls_on_root_is_lexicographic() -> None:
    fs = InMemoryFileSystem()
    fs.mkdir("/zeta")
    fs.mkdir("/alpha")
    fs.add_content_to_file("/middle.txt", "m")
    assert fs.ls("/") == ["alpha", "middle.txt", "zeta"]


def test_ls_on_file_returns_only_file_name() -> None:
    fs = InMemoryFileSystem()
    fs.add_content_to_file("/logs/today.txt", "entry")
    assert fs.ls("/logs/today.txt") == ["today.txt"]


def test_nested_paths_support_directories_and_files_together() -> None:
    fs = InMemoryFileSystem()
    fs.mkdir("/company/salesforce/docs")
    fs.add_content_to_file("/company/salesforce/docs/design.md", "LLD")
    fs.add_content_to_file("/company/salesforce/readme.txt", "root doc")
    assert fs.ls("/company/salesforce") == ["docs", "readme.txt"]
    assert fs.read_content_from_file("/company/salesforce/docs/design.md") == "LLD"


def test_concurrent_writers_to_different_files_do_not_corrupt_tree() -> None:
    writers = 40
    fs = InMemoryFileSystem()
    start = threading.Event()
    completed: list[int] = []
    completed_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all writers together for maximum tree contention
        fs.add_content_to_file(f"/shared/file-{i}.txt", f"content-{i}")
        with completed_lock:
            completed.append(i)

    threads = [threading.Thread(target=worker, args=(i,)) for i in range(writers)]
    for thread in threads:
        thread.start()
    start.set()
    for thread in threads:
        thread.join(timeout=10)

    assert len(completed) == writers
    assert len(fs.ls("/shared")) == writers
    for i in range(writers):
        assert fs.read_content_from_file(f"/shared/file-{i}.txt") == f"content-{i}"
