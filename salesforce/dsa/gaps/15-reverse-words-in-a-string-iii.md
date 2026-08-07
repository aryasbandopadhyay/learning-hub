# 15. Reverse Words in a String III

- **Difficulty:** Easy
- **Pattern:** String / Two Pointers
- **Asked at:** Salesforce, Amazon, Apple

## Problem
Reverse the characters in each word while preserving the original word order and spaces.

## Examples
```text
Input: s = "Let's take LeetCode contest"
Output: "s'teL ekat edoCteeL tsetnoc"
Explanation: Each word is reversed independently.
```

## Understanding & Intuition
Words stay in place; only their internal character order changes.

## Approach 1 — Naive / Brute Force
**Idea:** Build each reversed word by prepending characters.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        out = []
        for word in s.split(" "):
            rev = ""
            for ch in word: rev = ch + rev
            out.append(rev)
        return " ".join(out)
```
- **Time:** O(n^2) worst-case — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use slicing for each word.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        return " ".join(word[::-1] for word in s.split(" "))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reverse each word range in a character list.
```python
class Solution:
    def reverseWords(self, s: str) -> str:
        chars = list(s); start = 0
        for end in range(len(chars) + 1):
            if end == len(chars) or chars[end] == " ":
                l, r = start, end - 1
                while l < r:
                    chars[l], chars[r] = chars[r], chars[l]; l += 1; r -= 1
                start = end + 1
        return "".join(chars)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) worst-case | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Preserve word order.
- Python strings are immutable.
- Splitting on `" "` preserves explicit spaces better than default split.

## Related
- Reverse Words in a String
- Reverse String
