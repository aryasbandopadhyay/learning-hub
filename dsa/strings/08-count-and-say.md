# 08. Count and Say

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Return the `n`th term of the count-and-say sequence. The sequence starts with `"1"`; each next term describes consecutive groups in the previous term as `<count><digit>`.

**Input**
- `n`: the one-based term index.

**Output**
- The `n`th sequence term as a string. This judge compares exactly, so return the generated digits in order.

## Constraints
- `1 <= n <= 30`
- Generated terms contain only digit characters.

## Examples
```text
Input: n = 4
Output: "1211"
Explanation: The first four terms are `1`, `11`, `21`, and `1211`, so term four is `1211`.
```

## Understanding & Intuition
Each term depends only on the previous term. We repeatedly compress runs of equal characters. The key is correctly flushing the final run of each term.

## Approach 1 — Naive / Brute Force
**Idea:** For each step, repeatedly slice off the next equal-character run.
```python
class Solution:
    def countAndSay(self, n: int) -> str:
        term = "1"
        for _ in range(n - 1):
            next_term = ""
            while term:
                digit = term[0]
                count = 0
                while count < len(term) and term[count] == digit:
                    count += 1
                next_term += str(count) + digit
                term = term[count:]  # Slicing repeatedly copies data.
            term = next_term
        return term
```
- **Time:** O(n * L^2) — **Space:** O(L)

## Approach 2 — Better
**Idea:** Scan each previous term once and append run descriptions to a list.
```python
class Solution:
    def countAndSay(self, n: int) -> str:
        term = "1"
        for _ in range(n - 1):
            pieces = []
            i = 0
            while i < len(term):
                j = i
                while j < len(term) and term[j] == term[i]:
                    j += 1
                pieces.append(str(j - i))
                pieces.append(term[i])
                i = j
            term = ''.join(pieces)
        return term
```
- **Time:** O(n * L) — **Space:** O(L)

## Approach 3 — Optimal
**Idea:** Use a sentinel to force the final group to flush in one clean scan.
```python
class Solution:
    def countAndSay(self, n: int) -> str:
        term = "1"
        for _ in range(n - 1):
            pieces = []
            count = 0
            prev = term[0]
            for ch in term + "#":  # Sentinel differs from every digit.
                if ch == prev:
                    count += 1
                else:
                    pieces.append(str(count))
                    pieces.append(prev)
                    prev = ch
                    count = 1
            term = ''.join(pieces)
        return term
```
- **Time:** O(n * L) — **Space:** O(L)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * L^2) | O(L) |
| Better | O(n * L) | O(L) |
| Optimal | O(n * L) | O(L) |

## Edge Cases & Pitfalls
- `n = 1` returns `"1"` directly.
- Remember to append both count and digit.
- Flush the last run; it has no following different digit.

## Related
- Run-Length Encoding
- String Compression
- Recurrence Simulation
