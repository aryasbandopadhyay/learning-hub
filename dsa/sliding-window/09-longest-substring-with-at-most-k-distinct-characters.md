# 09. Longest Substring with At Most K Distinct Characters

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, LinkedIn

## Problem
Given a string `s` and integer `k`, return the length of the longest substring containing at most `k` distinct characters. Constraints: `0 <= len(s) <= 5 * 10^4`, `0 <= k <= 50`.

## Examples
```text
Input: s = "eceba", k = 2
Output: 3
Explanation: "ece" has at most two distinct characters.
```

## Understanding & Intuition
The window is valid while its distinct-character count is at most `k`. Adding a character can only increase that count by one, so invalid windows can be repaired by advancing `left`. This is the general form behind several sliding-window frequency problems.

## Approach 1 — Naive / Brute Force
**Idea:** Try every substring and stop when more than `k` distinct characters appear.
```python
class Solution:
    def lengthOfLongestSubstringKDistinct(self, s: str, k: int) -> int:
        best = 0
        for left in range(len(s)):
            seen = set()
            for right in range(left, len(s)):
                seen.add(s[right])
                if len(seen) > k:
                    break
                best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Track last indexes and remove the character whose last occurrence is leftmost when over limit.
```python
class Solution:
    def lengthOfLongestSubstringKDistinct(self, s: str, k: int) -> int:
        if k == 0:
            return 0
        last = {}
        left = best = 0
        for right, ch in enumerate(s):
            last[ch] = right
            if len(last) > k:
                drop = min(last, key=last.get)
                left = last[drop] + 1
                del last[drop]
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(nk) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Maintain frequencies and shrink until the number of keys is at most `k`.
```python
class Solution:
    def lengthOfLongestSubstringKDistinct(self, s: str, k: int) -> int:
        counts = {}
        left = best = 0
        for right, ch in enumerate(s):
            counts[ch] = counts.get(ch, 0) + 1
            while len(counts) > k:
                counts[s[left]] -= 1
                if counts[s[left]] == 0:
                    del counts[s[left]]
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(k) |
| Better | O(nk) | O(k) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- `k = 0` means no non-empty substring is valid.
- Remove zero-count characters from the dictionary.
- The answer may be the whole string.

## Related
- Fruit Into Baskets
- Longest Substring Without Repeating Characters

