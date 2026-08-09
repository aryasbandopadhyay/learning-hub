# 04. Group Anagrams

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a list of strings `strs`, group together strings that are anagrams of one another. Every input string must appear in exactly one group.

**Input**
- `strs`: a list of lowercase strings.

**Output**
- A list of anagram groups. **This judge compares exactly**: groups must appear in the order their anagram class is first encountered in `strs`, and strings inside each group must keep original input order.

## Constraints
- `1 <= strs.length <= 10^4`
- `0 <= strs[i].length <= 100`
- `strs[i]` consists of lowercase English letters.

## Examples
```text
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["eat","tea","ate"],["tan","nat"],["bat"]]
Explanation: `eat`, `tea`, and `ate` share letters; `tan` and `nat` share letters; `bat` is alone. Groups keep first-seen input order.
```

## Understanding & Intuition
Each group needs a canonical key. Sorted characters are simple; a fixed-size count tuple avoids per-word sorting for lowercase strings.

## Approach 1 — Naive / Brute Force
**Idea:** Pick an unused word and scan for matching anagrams.
```python
class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        used = [False] * len(strs)
        ans = []
        for i, word in enumerate(strs):
            if used[i]:
                continue
            key = sorted(word)
            group = []
            for j in range(i, len(strs)):
                if not used[j] and sorted(strs[j]) == key:
                    used[j] = True
                    group.append(strs[j])
            ans.append(group)
        return ans
```
- **Time:** O(n^2*m log m) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Map sorted-string keys to lists.
```python
class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        groups = {}
        for word in strs:
            key = ''.join(sorted(word))
            groups.setdefault(key, []).append(word)
        return list(groups.values())
```
- **Time:** O(n*m log m) — **Space:** O(n*m)

## Approach 3 — Optimal
**Idea:** Map 26-count tuple keys to lists.
```python
class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        groups = {}
        for word in strs:
            cnt = [0] * 26
            for ch in word:
                cnt[ord(ch) - ord('a')] += 1
            groups.setdefault(tuple(cnt), []).append(word)
        return list(groups.values())
```
- **Time:** O(n*m) — **Space:** O(n*m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2*m log m) | O(n) |
| Better | O(n*m log m) | O(n*m) |
| Optimal | O(n*m) | O(n*m) |

## Edge Cases & Pitfalls
- Group order is irrelevant.
- Count tuple assumes lowercase English letters.
- Empty strings share one key.

## Related
- Valid Anagram
- Group Shifted Strings
