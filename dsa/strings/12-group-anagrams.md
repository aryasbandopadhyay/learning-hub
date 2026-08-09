# 12. Group Anagrams

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, Meta, Google, Microsoft

## Problem
Given a list of strings `strs`, group strings that are anagrams of each other. Two strings are in the same group when their lowercase letter counts are identical.

**Input**
- `strs`: a list of lowercase English strings.

**Output**
- A list of anagram groups. This judge compares exactly: groups appear when their first member is first seen in `strs`, and words inside each group keep input order.

## Constraints
- `1 <= strs.length <= 10^4`
- `0 <= strs[i].length <= 100`
- `strs[i]` contains lowercase English letters.

## Examples
```text
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["eat","tea","ate"],["tan","nat"],["bat"]]
Explanation: `eat`, `tea`, and `ate` share one signature; `tan` and `nat` share another; `bat` stands alone.
```

## Understanding & Intuition
Anagrams share an order-independent signature. Sorting a word creates one simple signature. Counting letters avoids sorting each word and is faster for fixed alphabets.

## Approach 1 — Naive / Brute Force
**Idea:** Build groups by comparing each word with the representative of existing groups.
```python
from typing import List

class Solution:
    def groupAnagrams(self, strs: List[str]) -> List[List[str]]:
        groups = []
        for word in strs:
            placed = False
            for group in groups:
                if sorted(word) == sorted(group[0]):
                    group.append(word)
                    placed = True
                    break
            if not placed:
                groups.append([word])
        return groups
```
- **Time:** O(n^2 * k log k) — **Space:** O(n * k)

## Approach 2 — Better
**Idea:** Use the sorted word as a dictionary key.
```python
from collections import defaultdict
from typing import List

class Solution:
    def groupAnagrams(self, strs: List[str]) -> List[List[str]]:
        groups = defaultdict(list)
        for word in strs:
            key = ''.join(sorted(word))
            groups[key].append(word)
        return list(groups.values())
```
- **Time:** O(n * k log k) — **Space:** O(n * k)

## Approach 3 — Optimal
**Idea:** Use a 26-count tuple as the anagram signature.
```python
from collections import defaultdict
from typing import List

class Solution:
    def groupAnagrams(self, strs: List[str]) -> List[List[str]]:
        groups = defaultdict(list)
        for word in strs:
            counts = [0] * 26
            for ch in word:
                counts[ord(ch) - ord('a')] += 1
            groups[tuple(counts)].append(word)
        return list(groups.values())
```
- **Time:** O(n * k) — **Space:** O(n * k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 * k log k) | O(n * k) |
| Better | O(n * k log k) | O(n * k) |
| Optimal | O(n * k) | O(n * k) |

## Edge Cases & Pitfalls
- Empty strings group together.
- Return order of groups is not important.
- Count tuple works because the alphabet is fixed to lowercase English letters.

## Related
- Valid Anagram
- Top K Frequent Words
- Hashing
