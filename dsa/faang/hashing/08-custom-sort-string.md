# 08. Custom Sort String

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Meta, Amazon, Google

## Problem
Given a string `order` containing distinct lowercase letters and a string `s`, reorder the characters of `s` so that characters appearing in `order` follow that relative order. Characters not present in `order` should appear after ordered characters, sorted lexicographically for deterministic output.

Return the reordered string.

Constraints: `1 <= len(order) <= 26`, `1 <= len(s) <= 10^5`, and both strings contain lowercase English letters.

## Examples
```text
Input: order = "cba", s = "abcd"
Output: "cbad"
Explanation: c, b, and a follow the custom order; d is not in order and appears afterward.
```

## Understanding & Intuition
Only character frequencies of `s` matter. Emit all occurrences of characters in `order` first, in custom order. Then emit remaining characters in normal sorted order to make the answer canonical.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan the remaining characters for each ordered character, then sort leftovers.
```python
class Solution:
    def customSortString(self, order: str, s: str) -> str:
        remaining = list(s)
        out = []
        for ch in order:
            next_remaining = []
            for cur in remaining:
                if cur == ch:
                    out.append(cur)
                else:
                    next_remaining.append(cur)
            remaining = next_remaining
        out.extend(sorted(remaining))
        return ''.join(out)
```
- **Time:** O(σn + n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Count characters with a hashmap, emit custom-order counts, and sort only leftover keys.
```python
class Solution:
    def customSortString(self, order, s):
        counts = {}
        for ch in s:
            counts[ch] = counts.get(ch, 0) + 1
        out = []
        for ch in order:
            if ch in counts:
                out.append(ch * counts.pop(ch))
        for ch in sorted(counts):
            out.append(ch * counts[ch])
        return ''.join(out)
```
- **Time:** O(n + σ log σ) — **Space:** O(σ + n)

## Approach 3 — Optimal
**Idea:** Use a fixed 26-slot count array, then emit ordered and unordered letters.
```python
class Solution:
    def customSortString(self, order, s):
        counts = [0] * 26
        for ch in s:
            counts[ord(ch) - ord('a')] += 1
        out = []
        for ch in order:
            idx = ord(ch) - ord('a')
            if counts[idx]:
                out.append(ch * counts[idx])
                counts[idx] = 0
        for idx in range(26):
            if counts[idx]:
                out.append(chr(idx + ord('a')) * counts[idx])
        return ''.join(out)
```
- **Time:** O(n + σ) — **Space:** O(n + σ)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(σn + n log n) | O(n) |
| Better | O(n + σ log σ) | O(σ + n) |
| Optimal | O(n + σ) | O(n + σ) |

## Edge Cases & Pitfalls
- `order` may omit characters that appear in `s`.
- Characters in `order` are distinct, so no conflict exists.
- Sorting leftover characters avoids multiple valid outputs.

## Related
- Sort Characters By Frequency
- Valid Anagram
