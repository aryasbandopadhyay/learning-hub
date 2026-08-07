# 25. Zuma Game

- **Difficulty:** Hard
- **Pattern:** Backtracking / Memoization
- **Asked at:** Salesforce, Google, Amazon

## Problem
Given a board and hand of colored balls, return the minimum inserted balls needed to clear the board, or `-1` if impossible.

## Examples
```text
Input: board = "WRRBBW", hand = "RB"
Output: -1
Explanation: No insertion sequence clears the board.
```

## Understanding & Intuition
Insertions can trigger chain reactions. Memoized DFS should shrink the board after each action and reuse repeated states.

## Approach 1 — Naive / Brute Force
**Idea:** Try every ball at every position without memoization.
```python
class Solution:
    def findMinStep(self, board: str, hand: str) -> int:
        def shrink(s: str) -> str:
            changed = True
            while changed:
                changed = False; i = 0; parts = []
                while i < len(s):
                    j = i
                    while j < len(s) and s[j] == s[i]: j += 1
                    if j - i >= 3: changed = True
                    else: parts.append(s[i:j])
                    i = j
                s = "".join(parts)
            return s
        best = float("inf")
        def dfs(cur: str, balls: str, used: int) -> None:
            nonlocal best
            if not cur: best = min(best, used); return
            if not balls or used >= best: return
            for i in range(len(cur) + 1):
                for j, ch in enumerate(balls): dfs(shrink(cur[:i] + ch + cur[i:]), balls[:j] + balls[j + 1:], used + 1)
        dfs(board, hand, 0)
        return -1 if best == float("inf") else best
```
- **Time:** O((mn)^h) — **Space:** O(h + n)

## Approach 2 — Better
**Idea:** Memoize by current board and sorted remaining hand.
```python
from functools import lru_cache
class Solution:
    def findMinStep(self, board: str, hand: str) -> int:
        def shrink(s: str) -> str:
            i = 0
            while i < len(s):
                j = i
                while j < len(s) and s[j] == s[i]: j += 1
                if j - i >= 3: return shrink(s[:i] + s[j:])
                i = j
            return s
        @lru_cache(None)
        def dfs(cur: str, balls: str) -> int:
            if not cur: return 0
            ans = float("inf")
            for i in range(len(cur) + 1):
                for j, ch in enumerate(balls):
                    ans = min(ans, 1 + dfs(shrink(cur[:i] + ch + cur[i:]), balls[:j] + balls[j + 1:]))
            return ans
        ans = dfs(board, "".join(sorted(hand)))
        return -1 if ans == float("inf") else ans
```
- **Time:** Exponential with memoization — **Space:** Exponential

## Approach 3 — Optimal
**Idea:** Count hand balls and only spend the balls needed to complete an existing run.
```python
from functools import lru_cache
class Solution:
    def findMinStep(self, board: str, hand: str) -> int:
        def shrink(s: str) -> str:
            i = 0
            while i < len(s):
                j = i
                while j < len(s) and s[j] == s[i]: j += 1
                if j - i >= 3: return shrink(s[:i] + s[j:])
                i = j
            return s
        colors = "RYBGW"; start = tuple(hand.count(c) for c in colors)
        @lru_cache(None)
        def dfs(cur: str, counts: tuple[int, ...]) -> int:
            cur = shrink(cur)
            if not cur: return 0
            best = float("inf"); i = 0
            while i < len(cur):
                j = i
                while j < len(cur) and cur[j] == cur[i]: j += 1
                need = 3 - (j - i); idx = colors.index(cur[i])
                if counts[idx] >= need:
                    nxt = list(counts); nxt[idx] -= need
                    sub = dfs(cur[:i] + cur[j:], tuple(nxt))
                    if sub != float("inf"): best = min(best, need + sub)
                i = j
            return best
        ans = dfs(board, start)
        return -1 if ans == float("inf") else ans
```
- **Time:** Exponential with pruning — **Space:** Exponential

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((mn)^h) | O(h + n) |
| Better | Exponential with memoization | Exponential |
| Optimal | Exponential with pruning | Exponential |

## Edge Cases & Pitfalls
- Shrink chain reactions repeatedly.
- Memoize by both board and remaining hand.
- Optimized search inserts only useful balls.

## Related
- Remove Invalid Parentheses
- Candy Crush
