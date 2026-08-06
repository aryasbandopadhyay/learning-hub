"""Runnable demo for the cache MVP."""

from .cache import Cache
from .policies import LfuEvictionPolicy, LruEvictionPolicy


def main() -> None:
    lru: Cache[str, str] = Cache(2, LruEvictionPolicy())
    lru.put("a", "Alpha")
    lru.put("b", "Beta")
    print(f"LRU get a: {lru.get('a') or 'MISS'}")
    lru.put("c", "Gamma")
    print(f"LRU contains a: {str(lru.contains_key('a')).lower()}")
    print(f"LRU contains b: {str(lru.contains_key('b')).lower()}")
    print(f"LRU contains c: {str(lru.contains_key('c')).lower()}")

    lfu: Cache[str, str] = Cache(2, LfuEvictionPolicy())
    lfu.put("a", "Alpha")
    lfu.put("b", "Beta")
    lfu.get("a")
    lfu.get("a")
    lfu.get("b")
    lfu.put("c", "Gamma")
    print(f"LFU contains a: {str(lfu.contains_key('a')).lower()}")
    print(f"LFU contains b: {str(lfu.contains_key('b')).lower()}")
    print(f"LFU contains c: {str(lfu.contains_key('c')).lower()}")


if __name__ == "__main__":
    main()
