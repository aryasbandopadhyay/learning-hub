# 03. Repeated DNA Sequences

- **Difficulty:** Medium
- **Pattern:** advanced strings
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given a DNA string `s` containing only `A`, `C`, `G`, and `T`, return all length-10 substrings that appear more than once, sorted lexicographically.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `list[str]`. Return all length-10 substrings that appear more than once, sorted lexicographically. This judge compares the sequence exactly: return the repeated length-10 DNA substrings sorted lexicographically.

## Constraints
- `0 <= len(s) <= 100000`.

## Examples
```text
Input: s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"
Output: ["AAAAACCCCC", "CCCCCAAAAA"]
Explanation: These two length-10 sequences occur at least twice. The output is written in the required deterministic order.
```

## Understanding & Intuition
Every candidate has fixed length 10. Direct counting works, but DNA can be packed into two bits per character. Sorting the final repeated strings makes the output deterministic.

## Approach 1 — Naive / Brute Force
**Idea:** Count every length-10 slice directly.
```python
class Solution:
    def findRepeatedDnaSequences(self, s: str) -> list[str]:
        counts = {}
        for i in range(len(s) - 9):
            sub = s[i:i + 10]
            counts[sub] = counts.get(sub, 0) + 1
        return sorted([sub for sub, count in counts.items() if count > 1])
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store rolling 20-bit hashes and their representative strings.
```python
class Solution:
    def findRepeatedDnaSequences(self, s):
        code = {"A": 0, "C": 1, "G": 2, "T": 3}
        seen = {}
        repeated = set()
        value = 0
        mask = (1 << 20) - 1
        for i, ch in enumerate(s):
            value = ((value << 2) | code[ch]) & mask
            if i >= 9:
                sub = s[i - 9:i + 1]
                if value in seen:
                    repeated.add(sub)
                else:
                    seen[value] = sub
        return sorted(repeated)
```
- **Time:** O(n log r) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a rolling bitmask plus seen and repeated sets.
```python
class Solution:
    def findRepeatedDnaSequences(self, s):
        code = {"A": 0, "C": 1, "G": 2, "T": 3}
        seen = set()
        repeated = set()
        value = 0
        mask = (1 << 20) - 1
        for i, ch in enumerate(s):
            value = ((value << 2) | code[ch]) & mask
            if i >= 9:
                if value in seen:
                    repeated.add(s[i - 9:i + 1])
                else:
                    seen.add(value)
        return sorted(repeated)
```
- **Time:** O(n log r) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n log r) | O(n) |
| Optimal | O(n log r) | O(n) |

## Edge Cases & Pitfalls
- Strings shorter than 10 return `[]`.
- Return each repeated sequence once.
- Sort the output for deterministic judging.

## Related
- Rabin-Karp
- Sliding window
