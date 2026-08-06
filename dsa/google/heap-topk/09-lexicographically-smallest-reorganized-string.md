# 09. Lexicographically Smallest Reorganized String

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given a lowercase string `s`, rearrange its characters so no two adjacent characters are equal. Return the lexicographically smallest valid rearrangement, or `""` if none exists. Constraints: `1 <= len(s) <= 500`.

## Examples
```text
Input: s = "vvvlo"
Output: "vlvov"
Explanation: No valid rearrangement can start with l or o, so the lexicographically smallest valid string starts with v.
```

## Understanding & Intuition
The usual heap solution can produce any valid answer, but this version needs the smallest lexicographic one. At each position, try characters in ascending order and choose the first one that still allows the remainder to be completed. A feasibility check compares each remaining count with the slots available after the previous character.

## Approach 1 — Naive / Brute Force
**Idea:** Backtrack in lexicographic order and return the first full valid permutation, with memoization to avoid repeated states.
```python
class Solution:
    def reorganizeStringSmallest(self, s: str) -> str:
        from collections import Counter
        from functools import lru_cache
        chars = tuple(sorted(set(s)))
        start = tuple(Counter(s).get(ch, 0) for ch in chars)
        def feasible(counts, prev):
            total = sum(counts)
            for i, c in enumerate(counts):
                limit = total // 2 if chars[i] == prev else (total + 1) // 2
                if c > limit:
                    return False
            return True
        if not feasible(start, ''):
            return ""
        @lru_cache(None)
        def dfs(counts, prev):
            if sum(counts) == 0:
                return ""
            for i, ch in enumerate(chars):
                if counts[i] and ch != prev:
                    nxt = list(counts)
                    nxt[i] -= 1
                    nxt = tuple(nxt)
                    if feasible(nxt, ch):
                        tail = dfs(nxt, ch)
                        if tail is not None:
                            return ch + tail
            return None
        return dfs(start, '') or ""
```
- **Time:** O(n * a * states) — **Space:** O(states)

## Approach 2 — Better
**Idea:** Greedily scan the alphabet at each position and use the feasibility test to prove the prefix can be extended.
```python
class Solution:
    def reorganizeStringSmallest(self, s: str) -> str:
        from collections import Counter
        counts = Counter(s)
        chars = sorted(counts)
        def feasible(prev):
            total = sum(counts.values())
            for ch in chars:
                limit = total // 2 if ch == prev else (total + 1) // 2
                if counts[ch] > limit:
                    return False
            return True
        if not feasible(''):
            return ""
        ans = []
        prev = ''
        for _ in range(len(s)):
            for ch in chars:
                if counts[ch] == 0 or ch == prev:
                    continue
                counts[ch] -= 1
                if feasible(ch):
                    ans.append(ch)
                    prev = ch
                    break
                counts[ch] += 1
            else:
                return ""
        return ''.join(ans)
```
- **Time:** O(n * a^2) — **Space:** O(a)

## Approach 3 — Optimal
**Idea:** Use a min-heap of available characters at each position, temporarily skipping choices that would make completion impossible.
```python
class Solution:
    def reorganizeStringSmallest(self, s: str) -> str:
        from collections import Counter
        import heapq
        counts = Counter(s)
        chars = sorted(counts)
        def feasible(prev):
            total = sum(counts.values())
            for ch in chars:
                limit = total // 2 if ch == prev else (total + 1) // 2
                if counts[ch] > limit:
                    return False
            return True
        if not feasible(''):
            return ""
        heap = chars[:]
        heapq.heapify(heap)
        ans = []
        prev = ''
        for _ in range(len(s)):
            skipped = []
            placed = None
            while heap:
                ch = heapq.heappop(heap)
                if counts[ch] == 0:
                    continue
                if ch == prev:
                    skipped.append(ch)
                    continue
                counts[ch] -= 1
                if feasible(ch):
                    placed = ch
                    break
                counts[ch] += 1
                skipped.append(ch)
            for ch in skipped:
                heapq.heappush(heap, ch)
            if placed is None:
                return ""
            ans.append(placed)
            prev = placed
            if counts[placed] > 0:
                heapq.heappush(heap, placed)
        return ''.join(ans)
```
- **Time:** O(n * a^2 log a) — **Space:** O(a)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * a * states) | O(states) |
| Better | O(n * a^2) | O(a) |
| Optimal | O(n * a^2 log a) | O(a) |

## Edge Cases & Pitfalls
- If the most frequent character is too common, return the empty string.
- Lexicographically smallest is stricter than any valid arrangement.
- The previous character reduces available first slots for that same character.

## Related
- Reorganize String
- Task Scheduler
