# 02. Longest Repeating Character Replacement

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given an uppercase string `s` and integer `k`, return the length of the longest substring that can be changed into one repeated character by replacing at most `k` characters. Constraints: `1 <= len(s) <= 10^5`, `0 <= k <= len(s)`.

## Examples
```text
Input: s = "AABABBA", k = 1
Output: 4
Explanation: Replace one 'A' in "ABBA" or one 'B' in "AABA" to get length 4.
```

## Understanding & Intuition
For a window to be valid, all non-majority characters must be replaceable. If `window_size - max_frequency <= k`, the window can become one repeated character. Sliding windows work because expanding right only increases the window by one character.

## Approach 1 — Naive / Brute Force
**Idea:** Check every substring and count its most frequent character.
```python
class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        best = 0
        for left in range(len(s)):
            counts = {}
            max_freq = 0
            for right in range(left, len(s)):
                counts[s[right]] = counts.get(s[right], 0) + 1
                max_freq = max(max_freq, counts[s[right]])
                if right - left + 1 - max_freq <= k:
                    best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a sliding window but recompute the current max frequency when deciding to shrink.
```python
class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        counts = {}
        left = best = 0
        for right, ch in enumerate(s):
            counts[ch] = counts.get(ch, 0) + 1
            # Recomputing over at most 26 letters keeps this linear for uppercase input.
            while right - left + 1 - max(counts.values()) > k:
                counts[s[left]] -= 1
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(26n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Keep a nondecreasing historical `max_freq`; a stale value never misses the best length.
```python
class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        counts = {}
        left = best = max_freq = 0
        for right, ch in enumerate(s):
            counts[ch] = counts.get(ch, 0) + 1
            max_freq = max(max_freq, counts[ch])
            if right - left + 1 - max_freq > k:
                counts[s[left]] -= 1
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(26n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `k = 0` reduces to longest existing repeated-character run.
- Do not require recalculating `max_freq` in the optimal version.
- The usual LeetCode input is uppercase English letters.

## Related
- Max Consecutive Ones III
- Longest Substring with At Most K Distinct Characters
