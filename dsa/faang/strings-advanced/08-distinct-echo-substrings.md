# 08. Distinct Echo Substrings

- **Difficulty:** Hard
- **Pattern:** advanced strings
- **Asked at:** Google, Amazon

## Problem
An echo substring is a non-empty substring equal to `a + a` for some string `a`. Given `text`, return the number of distinct echo substrings.

**Input**
- `text`: a `str`; the input text.

**Output**
- A `int`. Return the number of distinct echo substrings.

## Constraints
- `1 <= len(text) <= 2000`.
- `text` contains lowercase English letters.

## Examples
```text
Input: text = "abcabcabc"
Output: 3
Explanation: The distinct echo substrings are "abcabc", "bcabca", and "cabcab".
```

## Understanding & Intuition
Echo substrings have even length and equal halves. Enumerating them is simple but costly. Longest-common-prefix information lets us test adjacent equal blocks quickly.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate even-length substrings and compare their halves.
```python
class Solution:
    def distinctEchoSubstrings(self, text: str) -> int:
        found = set()
        n = len(text)
        for start in range(n):
            for length in range(2, n - start + 1, 2):
                half = length // 2
                sub = text[start:start + length]
                if sub[:half] == sub[half:]:
                    found.add(sub)
        return len(found)
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Use rolling hashes to compare adjacent blocks, verifying matches before storing.
```python
class Solution:
    def distinctEchoSubstrings(self, text):
        n = len(text)
        mod, base = 1_000_000_007, 911382323
        pref = [0] * (n + 1)
        power = [1] * (n + 1)
        for i, ch in enumerate(text):
            pref[i + 1] = (pref[i] * base + ord(ch)) % mod
            power[i + 1] = (power[i] * base) % mod
        def get(l, r):
            return (pref[r] - pref[l] * power[r - l]) % mod
        found = set()
        for half in range(1, n // 2 + 1):
            for start in range(n - 2 * half + 1):
                mid = start + half
                end = mid + half
                if get(start, mid) == get(mid, end):
                    sub = text[start:end]
                    if sub[:half] == sub[half:]:
                        found.add(sub)
        return len(found)
```
- **Time:** O(n^2) expected — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Compute LCP for every suffix pair; adjacent halves match if their LCP is at least the half length.
```python
class Solution:
    def distinctEchoSubstrings(self, text):
        n = len(text)
        lcp = [[0] * (n + 1) for _ in range(n + 1)]
        for i in range(n - 1, -1, -1):
            for j in range(n - 1, -1, -1):
                if text[i] == text[j]:
                    lcp[i][j] = 1 + lcp[i + 1][j + 1]
        found = set()
        for half in range(1, n // 2 + 1):
            for start in range(n - 2 * half + 1):
                if lcp[start][start + half] >= half:
                    found.add(text[start:start + 2 * half])
        return len(found)
```
- **Time:** O(n^2) plus slicing — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n^2) |
| Better | O(n^2) expected | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- Only even-length substrings can qualify.
- Count distinct echo substrings, not occurrences.
- Verify hash matches when correctness must be exact.

## Related
- Rolling hash
- LCP dynamic programming
