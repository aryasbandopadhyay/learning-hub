"""Find Command — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one so the two can be compared:

    models.py      -> FileSystemEntry, FileNode, DirectoryNode, EntryType
    filters.py     -> Filter plus Name/Extension/Size/Type/MinDepth and And/Or/Not
    engine.py      -> FindEngine DFS traversal
    exceptions.py  -> InvalidFileSystemError
    main.py        -> runnable demo

Concurrency: none is required because the tree is built in memory and searched read-only. If a
future version reads a mutable real filesystem, that adapter can own the synchronization policy.
"""
