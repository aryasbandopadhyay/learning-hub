# 19. Check Whether Two Strings are Almost Equivalent

- **Difficulty:** Easy
- **Pattern:** String / Counting
- **Asked at:** Salesforce, Amazon, Google

## Problem
Return whether every lowercase letter's frequency differs by at most 3 between two strings.

## Examples
```text
Input: word1 = "aaaa", word2 = "bccb"
Output: false
Explanation: The frequency difference for 'a' is 4.
```

## Understanding & Intuition
Count character frequency differences, then ensure no absolute difference exceeds 3.

## Approach 1 — Naive / Brute Force
**Idea:** Count each letter by scanning both words.
```python
class Solution:
    def checkAlmostEquivalent(self, word1: str, word2: str) -> bool:
        for code in range(ord("a"), ord("z") + 1):
            ch = chr(code)
            if abs(word1.count(ch) - word2.count(ch)) > 3: return False
        return True
```
- **Time:** O(26n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a dictionary of frequency differences.
```python
class Solution:
    def checkAlmostEquivalent(self, word1: str, word2: str) -> bool:
        diff = {}
        for ch in word1: diff[ch] = diff.get(ch, 0) + 1
        for ch in word2: diff[ch] = diff.get(ch, 0) - 1
        return all(abs(x) <= 3 for x in diff.values())
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use a fixed array for 26 lowercase letters.
```python
class Solution:
    def checkAlmostEquivalent(self, word1: str, word2: str) -> bool:
        diff = [0] * 26
        for a, b in zip(word1, word2):
            diff[ord(a) - ord("a")] += 1
            diff[ord(b) - ord("a")] -= 1
        return all(abs(x) <= 3 for x in diff)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(26n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Check letters absent from one string.
- Difference 4 fails.
- Constraints use lowercase English letters.

## Related
- Valid Anagram
- Ransom Note
