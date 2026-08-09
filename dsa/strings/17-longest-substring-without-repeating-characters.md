# 17. Longest Substring Without Repeating Characters

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, Meta, Google, Microsoft

## Problem
Given a string `s`, return the length of the longest contiguous substring with no repeated characters.

**Input**
- `s`: the string to inspect.

**Output**
- The maximum length of a substring whose characters are all distinct.

## Constraints
- `0 <= s.length <= 5 * 10^4`
- `s` may contain letters, digits, symbols, and spaces.

## Examples
```text
Input: s = "abcabcbb"
Output: 3
Explanation: `abc` is a longest window with all distinct characters; every longer window repeats a character.
```

## Understanding & Intuition
A valid window contains no duplicate characters. Brute force checks each substring. Sliding window maintains a duplicate-free range and moves the left boundary only when needed.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every substring and test whether all characters are unique.
```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        best = 0
        for left in range(len(s)):
            for right in range(left, len(s)):
                sub = s[left:right + 1]
                if len(set(sub)) == len(sub):
                    best = max(best, len(sub))
        return best
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** For each start, extend right until a duplicate appears.
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
- **Time:** O(n^2) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Track each character's last index and jump `left` past duplicates inside the window.
```python
class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        last_seen = {}
        left = 0
        best = 0
        for right, ch in enumerate(s):
            if ch in last_seen and last_seen[ch] >= left:
                left = last_seen[ch] + 1
            last_seen[ch] = right
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(k) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- The empty string returns `0`.
- Do not move `left` backward when seeing an old duplicate outside the current window.
- Spaces and symbols are valid characters.

## Related
- Sliding Window
- Minimum Window Substring
- Longest Repeating Character Replacement
