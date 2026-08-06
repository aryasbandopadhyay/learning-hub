# 05. Maximize the Confusion of an Exam

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given a string `answerKey` containing only `'T'` and `'F'`, and an integer `k`, change at most `k` characters. Return the maximum length of a contiguous segment containing all the same character after changes.

Constraints: `1 <= len(answerKey) <= 100000`; `0 <= k <= len(answerKey)`.

## Examples
```text
Input: answerKey = "TTFF", k = 2
Output: 4
Explanation: Change both `F` answers to `T`.
```

## Understanding & Intuition
A window can be made all one character when the minority count is at most `k`. This mirrors character replacement with an alphabet of size two. Sliding the left edge restores feasibility after adding a bad character.

## Approach 1 — Naive / Brute Force
**Idea:** Check every substring and count how many flips make it all `T` or all `F`.
```python
class Solution:
    def maxConsecutiveAnswers(self, answerKey: str, k: int) -> int:
        n = len(answerKey)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                t = 0
                f = 0
                for p in range(i, j + 1):
                    if answerKey[p] == 'T':
                        t += 1
                    else:
                        f += 1
                if min(t, f) <= k:
                    ans = max(ans, j - i + 1)
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Run one sliding window for making all `T` and another for making all `F`.
```python
class Solution:
    def maxConsecutiveAnswers(self, answerKey: str, k: int) -> int:
        def longest(target: str) -> int:
            left = 0
            flips = 0
            best = 0
            for right, ch in enumerate(answerKey):
                if ch != target:
                    flips += 1
                while flips > k:
                    if answerKey[left] != target:
                        flips -= 1
                    left += 1
                best = max(best, right - left + 1)
            return best
        return max(longest('T'), longest('F'))
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Maintain counts in one window; a window is usable if length minus the dominant count is at most `k`.
```python
class Solution:
    def maxConsecutiveAnswers(self, answerKey: str, k: int) -> int:
        count = {'T': 0, 'F': 0}
        left = 0
        best_count = 0
        ans = 0
        for right, ch in enumerate(answerKey):
            count[ch] += 1
            best_count = max(best_count, count[ch])
            while right - left + 1 - best_count > k:
                count[answerKey[left]] -= 1
                left += 1
            ans = max(ans, right - left + 1)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `k = 0` asks for the longest existing run.
- Keeping a non-decreasing dominant count is safe for maximizing length.

## Related
- Longest Repeating Character Replacement
- Max Consecutive Ones III
