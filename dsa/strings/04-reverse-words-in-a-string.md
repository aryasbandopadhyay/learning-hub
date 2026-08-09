# 04. Reverse Words in a String

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Given a string `s` containing words separated by spaces, return the words in reverse order. A word is a maximal sequence of non-space characters. The result must use one space between words and no leading or trailing spaces.

**Input**
- `s`: a string with at least one word and possibly extra spaces.

**Output**
- A string containing the same words in reverse order with single-space separation. This judge compares exactly, so spacing must match.

## Constraints
- `1 <= s.length <= 10^4`
- `s` contains printable characters and spaces.
- `s` contains at least one non-space character.

## Examples
```text
Input: s = "  hello world  "
Output: "world hello"
Explanation: The words are `hello` and `world`; reversing them and trimming extra spaces gives `world hello`.
```

## Understanding & Intuition
The task is not character reversal; it is token order reversal with whitespace normalization. Python's split handles repeated whitespace. For interview settings, a manual parser shows the same idea without relying on library tokenization.

## Approach 1 — Naive / Brute Force
**Idea:** Manually collect every word, insert each at the front, then join.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        words = []
        i = 0
        while i < len(s):
            while i < len(s) and s[i] == ' ':
                i += 1
            start = i
            while i < len(s) and s[i] != ' ':
                i += 1
            if start < i:
                words.insert(0, s[start:i])  # Front insertion shifts existing words.
        return ' '.join(words)
```
- **Time:** O(n + w^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Split into words, reverse the list, and join with one space.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        words = s.split()
        words.reverse()
        return ' '.join(words)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan from right to left and append words directly in reversed order.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        result = []
        i = len(s) - 1
        while i >= 0:
            while i >= 0 and s[i] == ' ':
                i -= 1
            end = i
            while i >= 0 and s[i] != ' ':
                i -= 1
            if i < end:
                result.append(s[i + 1:end + 1])
        return ' '.join(result)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + w^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Multiple spaces must collapse to one.
- Leading and trailing spaces must disappear.
- A string with one word should return that word.

## Related
- Reverse Words in a String III
- Trim and Split
- Two Pointers
