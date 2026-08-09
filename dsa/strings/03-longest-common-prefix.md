# 03. Longest Common Prefix

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given a non-empty list of strings `strs`, return the longest prefix shared by every string. A prefix begins at index `0`; return `""` if no non-empty prefix is common.

**Input**
- `strs`: a list of strings.

**Output**
- The longest common prefix. This judge compares exactly, so return the prefix characters in left-to-right order.

## Constraints
- `1 <= strs.length <= 200`
- `0 <= strs[i].length <= 200`
- `strs[i]` contains lowercase English letters.

## Examples
```text
Input: strs = ["flower","flow","flight"]
Output: "fl"
Explanation: All words start with `fl`; the next character differs, so `fl` is the longest shared prefix.
```

## Understanding & Intuition
A common prefix cannot be longer than the shortest string. We can test prefix lengths directly, shrink a candidate, or compare columns. The vertical scan stops as soon as one string mismatches.

## Approach 1 — Naive / Brute Force
**Idea:** Try every prefix of the first word from longest to shortest and test all strings.
```python
from typing import List

class Solution:
    def longestCommonPrefix(self, strs: List[str]) -> str:
        first = strs[0]
        for length in range(len(first), -1, -1):
            prefix = first[:length]
            if all(word.startswith(prefix) for word in strs):
                return prefix
        return ""
```
- **Time:** O(n * m^2) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Keep shrinking the current prefix until each word starts with it.
```python
from typing import List

class Solution:
    def longestCommonPrefix(self, strs: List[str]) -> str:
        prefix = strs[0]
        for word in strs[1:]:
            while not word.startswith(prefix):
                prefix = prefix[:-1]
                if not prefix:
                    return ""
        return prefix
```
- **Time:** O(n * m^2) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Scan character by character across all strings and stop at the first mismatch.
```python
from typing import List

class Solution:
    def longestCommonPrefix(self, strs: List[str]) -> str:
        for i, ch in enumerate(strs[0]):
            for word in strs[1:]:
                if i == len(word) or word[i] != ch:
                    return strs[0][:i]
        return strs[0]
```
- **Time:** O(n * m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * m^2) | O(m) |
| Better | O(n * m^2) | O(m) |
| Optimal | O(n * m) | O(1) |

## Edge Cases & Pitfalls
- One empty string makes the answer empty.
- A single string is its own longest common prefix.
- Do not assume all strings have the same length.

## Related
- Trie
- Longest Common Suffix
- String Matching
