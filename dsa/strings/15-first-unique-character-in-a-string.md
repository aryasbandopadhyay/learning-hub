# 15. First Unique Character in a String

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Amazon, Bloomberg, Microsoft, Google

## Problem
Given a string `s`, return the index of the first non-repeating character. If no such character exists, return `-1`. Constraints: `1 <= len(s) <= 10^5`; `s` contains lowercase English letters.

## Examples
```text
Input: s = "leetcode"
Output: 0
Explanation: 'l' appears once and is the first unique character.
```

## Understanding & Intuition
The first unique character is determined by frequency and original order. Brute force recounts each character. A frequency table lets us scan once to count and once to find the first count of one.

## Approach 1 — Naive / Brute Force
**Idea:** For each index, scan the whole string to count that character.
```python
class Solution:
    def firstUniqChar(self, s: str) -> int:
        for i, ch in enumerate(s):
            count = 0
            for other in s:
                if other == ch:
                    count += 1
            if count == 1:
                return i
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a dictionary to count all characters, then scan in order.
```python
from collections import Counter

class Solution:
    def firstUniqChar(self, s: str) -> int:
        counts = Counter(s)
        for i, ch in enumerate(s):
            if counts[ch] == 1:
                return i
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use a fixed 26-slot array for lowercase letters and then scan for the first frequency one.
```python
class Solution:
    def firstUniqChar(self, s: str) -> int:
        counts = [0] * 26
        for ch in s:
            counts[ord(ch) - ord('a')] += 1
        for i, ch in enumerate(s):
            if counts[ord(ch) - ord('a')] == 1:
                return i
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return `-1` when every character repeats.
- Preserve original order when selecting the answer.
- Space is O(1) because the alphabet is fixed.

## Related
- Ransom Note
- Valid Anagram
- Queue of Characters
