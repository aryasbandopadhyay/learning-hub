# 06. Implement strStr() / Find the Index of the First Occurrence

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given strings `haystack` and `needle`, return the zero-based index of the first place `needle` appears as a contiguous substring of `haystack`, or `-1` if it never appears.

**Input**
- `haystack`: the string to search.
- `needle`: the non-empty pattern to find.

**Output**
- The smallest starting index of `needle` in `haystack`, or `-1`. This judge compares exactly.

## Constraints
- `1 <= haystack.length, needle.length <= 10^4`
- `haystack` and `needle` contain lowercase English letters.

## Examples
```text
Input: haystack = "sadbutsad", needle = "sad"
Output: 0
Explanation: `sad` occurs starting at index `0`, which is the first possible occurrence.
```

## Understanding & Intuition
This is exact substring search. A direct scan checks each possible start, while KMP avoids re-checking characters after a partial match. Python's built-in search is concise but KMP is the classic optimal interview solution.

## Approach 1 — Naive / Brute Force
**Idea:** Try every start index and compare characters one by one.
```python
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        n, m = len(haystack), len(needle)
        for start in range(n - m + 1):
            j = 0
            while j < m and haystack[start + j] == needle[j]:
                j += 1
            if j == m:
                return start
        return -1
```
- **Time:** O(n * m) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use slicing to compare each window with the pattern.
```python
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        m = len(needle)
        for start in range(len(haystack) - m + 1):
            if haystack[start:start + m] == needle:
                return start
        return -1
```
- **Time:** O(n * m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Build the KMP longest-prefix-suffix table and scan the text once.
```python
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        m = len(needle)
        lps = [0] * m
        length = 0
        for i in range(1, m):
            while length and needle[i] != needle[length]:
                length = lps[length - 1]
            if needle[i] == needle[length]:
                length += 1
                lps[i] = length

        j = 0
        for i, ch in enumerate(haystack):
            while j and ch != needle[j]:
                j = lps[j - 1]
            if ch == needle[j]:
                j += 1
                if j == m:
                    return i - m + 1
        return -1
```
- **Time:** O(n + m) — **Space:** O(m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * m) | O(1) |
| Better | O(n * m) | O(m) |
| Optimal | O(n + m) | O(m) |

## Edge Cases & Pitfalls
- In this problem, `needle` is non-empty; otherwise conventionally return `0`.
- Stop scanning when fewer than `m` characters remain.
- KMP fallback should use `lps[j - 1]`, not simply decrement by one.

## Related
- KMP
- Repeated Substring Pattern
- Rabin-Karp
