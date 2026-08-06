# 09. Stickers to Spell Word

- **Difficulty:** Hard
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Facebook, Amazon

## Problem
Given sticker strings `stickers` and a `target`, each sticker may be used unlimited times and contributes its letters once per use. Return the minimum stickers needed to form `target`, or `-1` if impossible.
Constraints: `1 <= len(stickers) <= 50`, `1 <= len(target) <= 15`, stickers and target contain lowercase English letters.

## Examples
```text
Input: stickers = ["with","example","science"], target = "thehat"
Output: 3
Explanation: Two "with" stickers and one "example" sticker can supply the letters.
```

## Understanding & Intuition
The remaining multiset of target letters is the DP state. Applying a sticker subtracts available letters and creates a smaller canonical remaining string.

## Approach 1 — Naive / Brute Force
**Idea:** Try every sticker against the remaining sorted target string without memoization.
```python
class Solution:
    def minStickers(self, stickers: list[str], target: str) -> int:
        counts = []
        for st in stickers:
            d = {}
            for ch in st:
                d[ch] = d.get(ch, 0) + 1
            counts.append(d)
        def apply(rem: str, d: dict) -> str:
            left = []
            used = d.copy()
            for ch in rem:
                if used.get(ch, 0):
                    used[ch] -= 1
                else:
                    left.append(ch)
            return ''.join(left)
        def dfs(rem: str) -> int:
            if not rem:
                return 0
            best = 10**9
            for d in counts:
                if rem[0] not in d:
                    continue
                nxt = apply(rem, d)
                if len(nxt) < len(rem):
                    sub = dfs(nxt)
                    if sub != -1:
                        best = min(best, 1 + sub)
            return -1 if best == 10**9 else best
        return dfs(''.join(sorted(target)))
```
- **Time:** O(s^t * t) — **Space:** O(t)

## Approach 2 — Better
**Idea:** Memoize by the canonical remaining string.
```python
class Solution:
    def minStickers(self, stickers: list[str], target: str) -> int:
        from functools import lru_cache
        counts = []
        for st in stickers:
            d = {}
            for ch in st:
                d[ch] = d.get(ch, 0) + 1
            counts.append(d)
        def apply(rem: str, d: dict) -> str:
            left = []
            used = d.copy()
            for ch in rem:
                if used.get(ch, 0):
                    used[ch] -= 1
                else:
                    left.append(ch)
            return ''.join(left)
        @lru_cache(None)
        def dfs(rem: str) -> int:
            if not rem:
                return 0
            best = 10**9
            for d in counts:
                if rem[0] not in d:
                    continue
                nxt = apply(rem, d)
                if len(nxt) < len(rem):
                    sub = dfs(nxt)
                    if sub != -1:
                        best = min(best, 1 + sub)
            return -1 if best == 10**9 else best
        return dfs(''.join(sorted(target)))
```
- **Time:** O(2^t * s * t) — **Space:** O(2^t * t)

## Approach 3 — Optimal
**Idea:** Use bitmask DP over target positions; each sticker greedily fills currently missing matching letters.
```python
class Solution:
    def minStickers(self, stickers: list[str], target: str) -> int:
        n = len(target)
        full = (1 << n) - 1
        dp = [10**9] * (1 << n)
        dp[0] = 0
        for mask in range(1 << n):
            if dp[mask] == 10**9:
                continue
            for st in stickers:
                nxt = mask
                for ch in st:
                    for i, need in enumerate(target):
                        if need == ch and not (nxt >> i) & 1:
                            nxt |= 1 << i
                            break
                if dp[mask] + 1 < dp[nxt]:
                    dp[nxt] = dp[mask] + 1
        return -1 if dp[full] == 10**9 else dp[full]
```
- **Time:** O(2^t * s * L * t) — **Space:** O(2^t)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(s^t * t) | O(t) |
| Better | O(2^t * s * t) | O(2^t * t) |
| Optimal | O(2^t * s * L * t) | O(2^t) |

## Edge Cases & Pitfalls
- Use a canonical remaining string so memo states merge.
- Return `-1` when no sticker reduces the remaining target.

## Related
- Word Break
- Distinct Subsequences
