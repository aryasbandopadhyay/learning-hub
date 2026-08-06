# 01. Longest Substring Without Repeating Characters

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a string `s`, return the length of the longest substring without repeating characters. Constraints: `0 <= len(s) <= 5 * 10^4`; `s` may contain letters, digits, symbols, and spaces.

## Examples
```text
Input: s = "abcabcbb"
Output: 3
Explanation: "abc" is the longest substring without duplicate characters.
```

## Understanding & Intuition
A substring is contiguous, so when a duplicate appears, only the left boundary needs to move. The sliding window keeps the current valid substring and grows greedily. Better implementations avoid rechecking characters that are already known to be valid.

## Approach 1 — Naive / Brute Force
**Idea:** Try every substring start and stop as soon as a repeated character appears.
```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        best = 0
        for left in range(len(s)):
            seen = set()
            for right in range(left, len(s)):
                if s[right] in seen:
                    break
                seen.add(s[right])
                best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(min(n, charset))

## Approach 2 — Better
**Idea:** Maintain a valid window with a set and remove from the left until the new character fits.
```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        seen = set()
        left = best = 0
        for right, ch in enumerate(s):
            # Shrink until ch is no longer duplicated in the window.
            while ch in seen:
                seen.remove(s[left])
                left += 1
            seen.add(ch)
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(min(n, charset))

## Approach 3 — Optimal
**Idea:** Store each character's latest index and jump `left` past the duplicate in one step.
```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        last_seen = {}
        left = best = 0
        for right, ch in enumerate(s):
            if ch in last_seen and last_seen[ch] >= left:
                # Jump over the previous occurrence instead of popping one by one.
                left = last_seen[ch] + 1
            last_seen[ch] = right
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(min(n, charset))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(min(n, charset)) |
| Better | O(n) | O(min(n, charset)) |
| Optimal | O(n) | O(min(n, charset)) |

## Edge Cases & Pitfalls
- Empty strings return `0`.
- Move `left` only forward; old duplicate indexes before `left` are irrelevant.
- Spaces and symbols are valid characters.

## Related
- Longest Substring with At Most K Distinct Characters
- Fruit Into Baskets

