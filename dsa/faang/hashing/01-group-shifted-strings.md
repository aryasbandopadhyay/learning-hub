# 01. Group Shifted Strings

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Meta, Google, Amazon

## Problem
Given a list of lowercase strings, group strings that belong to the same shifting sequence. A shift changes every character to the next letter with wraparound, so `abc -> bcd -> ... -> xyz`; strings are in the same group when one can be shifted some number of times to become the other.

Return a canonical deterministic answer: sort every group lexicographically, then sort the list of groups lexicographically by group contents.

Constraints: `1 <= len(strings) <= 2000`, `1 <= len(strings[i]) <= 50`, and all strings contain lowercase English letters.

## Examples
```text
Input: strings = ["abc", "bcd", "acef", "xyz", "az", "ba", "a", "z"]
Output: [["a", "z"], ["abc", "bcd", "xyz"], ["acef"], ["az", "ba"]]
Explanation: Each group shares the same cyclic difference pattern after normalizing by its first character.
```

## Understanding & Intuition
Absolute letters do not matter; only cyclic offsets from the first character matter. Normalizing each word to a signature lets equal shifting sequences collide in the same hashmap bucket. Sorting buckets and the outer list makes the naturally unordered grouping deterministic.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly choose an unused string and scan all remaining strings to find those with the same shift relationship.
```python
class Solution:
    def groupShiftedStrings(self, strings: list[str]) -> list[list[str]]:
        def same(a, b):
            if len(a) != len(b):
                return False
            shift = (ord(b[0]) - ord(a[0])) % 26
            for x, y in zip(a, b):
                if (ord(y) - ord(x)) % 26 != shift:
                    return False
            return True

        used = [False] * len(strings)
        ans = []
        for i in range(len(strings)):
            if used[i]:
                continue
            group = []
            for j in range(i, len(strings)):
                if not used[j] and same(strings[i], strings[j]):
                    used[j] = True
                    group.append(strings[j])
            ans.append(sorted(group))
        return sorted(ans)
```
- **Time:** O(n²k + n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use the tuple of cyclic differences between adjacent characters as the grouping key.
```python
class Solution:
    def groupShiftedStrings(self, strings):
        groups = {}
        for s in strings:
            key = []
            for i in range(1, len(s)):
                key.append((ord(s[i]) - ord(s[i - 1])) % 26)
            groups.setdefault((len(s), tuple(key)), []).append(s)
        ans = [sorted(group) for group in groups.values()]
        return sorted(ans)
```
- **Time:** O(nk + n log n) — **Space:** O(nk)

## Approach 3 — Optimal
**Idea:** Normalize every string by shifting its first character to `a`; equal normalized strings are exactly one group.
```python
class Solution:
    def groupShiftedStrings(self, strings):
        groups = {}
        for s in strings:
            base = ord(s[0]) - ord('a')
            key = ''.join(chr((ord(ch) - ord('a') - base) % 26 + ord('a')) for ch in s)
            groups.setdefault(key, []).append(s)
        ans = [sorted(group) for group in groups.values()]
        return sorted(ans)
```
- **Time:** O(nk + n log n) — **Space:** O(nk)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²k + n log n) | O(n) |
| Better | O(nk + n log n) | O(nk) |
| Optimal | O(nk + n log n) | O(nk) |

## Edge Cases & Pitfalls
- Single-character strings all belong to one group.
- Wraparound pairs such as `az` and `ba` must match.
- Sort both each group and the outer list before returning.

## Related
- Group Anagrams
- Isomorphic Strings
