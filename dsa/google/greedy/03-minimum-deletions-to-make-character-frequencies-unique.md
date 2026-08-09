# 03. Minimum Deletions to Make Character Frequencies Unique

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
You are given a string `s`.

Delete as few characters as possible so that no two different letters have the same positive frequency in the remaining string. Return the minimum deletions needed.

**Input**
- `s`: a lowercase English string.

**Output**
- The minimum number of deletions needed to make all remaining positive frequencies unique.

## Constraints
- `1 <= s.length <= 10^5`
- `s` contains only lowercase English letters.

## Examples
```text
Input: s = "aaabbbcc"
Output: 2
Explanation: The frequencies are `3`, `3`, and `2`. Deleting two characters from one frequency-3 letter leaves frequencies `3`, `2`, and `1`.
```

## Understanding & Intuition
Only the multiset of frequencies matters. If a frequency is already used, the only legal action is to delete characters and lower it. Keeping each lowered frequency as high as possible minimizes deletions.

## Approach 1 — Naive / Brute Force
**Idea:** Sort frequencies and decrement a colliding frequency until it is unused.
```python
class Solution:
    def minDeletions(self, s: str) -> int:
        from collections import Counter
        used = set()
        deletions = 0
        for f in sorted(Counter(s).values(), reverse=True):
            while f > 0 and f in used:
                f -= 1
                deletions += 1
            if f > 0:
                used.add(f)
        return deletions
```
- **Time:** O(n + k log k + n) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Count how many characters have each frequency and push duplicates down one level at a time.
```python
class Solution:
    def minDeletions(self, s):
        from collections import Counter
        buckets = Counter(Counter(s).values())
        deletions = 0
        for f in range(max(buckets) if buckets else 0, 0, -1):
            extra = buckets[f] - 1
            if extra > 0:
                deletions += extra
                buckets[f - 1] += extra
        return deletions
```
- **Time:** O(n + m) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a set of occupied frequencies and greedily lower only conflicted counts.
```python
class Solution:
    def minDeletions(self, s):
        from collections import Counter
        used = set()
        deletions = 0
        for f in Counter(s).values():
            while f > 0 and f in used:
                f -= 1
                deletions += 1
            if f > 0:
                used.add(f)
        return deletions
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + k log k + n) | O(k) |
| Better | O(n + m) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Many characters may be fully deleted to frequency `0`.
- Frequency `0` is not considered a character frequency.
- With lowercase English letters, at most 26 positive frequencies exist.

## Related
- Reorganize String
- Sort Characters By Frequency
