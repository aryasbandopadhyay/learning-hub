# 04. Bag of Tokens

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Google, Amazon, Meta

## Problem
Given token powers and initial `power`, play tokens face up to spend power and gain score, or face down to spend score and gain power. Return the maximum score.

Constraints: `0 <= len(tokens) <= 1000`, `0 <= tokens[i], power <= 10^4`.

## Examples
```text
Input: tokens = [100,200,300,400], power = 200
Output: 2
Explanation: Buy 100 and 200, sell 400, then buy 300.
```

## Understanding & Intuition
Cheap tokens are best for score; expensive tokens are best for refunds. Sorting creates the classic buy-left/sell-right greedy choice. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def bagOfTokensScore(self, tokens: list[int], power: int) -> int:
        tokens = sorted(tokens)
        memo = {}
        def dfs(l, r, p, score):
            key = (l, r, p, score)
            if key in memo:
                return memo[key]
            best = score
            if l <= r and p >= tokens[l]:
                best = max(best, dfs(l + 1, r, p - tokens[l], score + 1))
            if l <= r and score > 0:
                best = max(best, dfs(l, r - 1, p + tokens[r], score - 1))
            memo[key] = best
            return best
        return dfs(0, len(tokens) - 1, power, 0)
```
- **Time:** O(n^2P) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def bagOfTokensScore(self, tokens: list[int], power: int) -> int:
        tokens = sorted(tokens)
        n = len(tokens)
        pref = [0]
        for x in tokens:
            pref.append(pref[-1] + x)
        suff = [0] * (n + 1)
        for i in range(n - 1, -1, -1):
            suff[i] = suff[i + 1] + tokens[i]
        best = 0
        for sells in range(n + 1):
            budget = power + suff[n - sells]
            lo, hi = 0, n - sells
            while lo < hi:
                mid = (lo + hi + 1) // 2
                if pref[mid] <= budget:
                    lo = mid
                else:
                    hi = mid - 1
            if lo >= sells:
                best = max(best, lo - sells)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def bagOfTokensScore(self, tokens: list[int], power: int) -> int:
        tokens = sorted(tokens)
        l, r, score, best = 0, len(tokens) - 1, 0, 0
        while l <= r:
            if power >= tokens[l]:
                power -= tokens[l]; score += 1; best = max(best, score); l += 1
            elif score > 0 and l < r:
                power += tokens[r]; score -= 1; r -= 1
            else:
                break
        return best
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2P) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Track best score, not final score.
- Do not sell if no future buy can benefit.
- Sorting is required for the greedy exchange.

## Related
- Assign Cookies
- Boats to Save People
