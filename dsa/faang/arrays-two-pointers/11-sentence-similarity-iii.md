# 11. Sentence Similarity III

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Google, Meta, Amazon

## Problem
Implement `areSentencesSimilar` for **Sentence Similarity III**. Two sentences are similar if one can become the other by inserting an arbitrary sentence at one position. Return whether `sentence1` and `sentence2` are similar.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `sentence1`: string; first sentence.
- `sentence2`: string; second sentence.

**Output**
- `True` or `False`.

## Constraints
- `1 <= len(sentence1), len(sentence2) <= 10^5`
- words are separated by single spaces

## Examples
```text
Input: sentence1 = "My name is Haley", sentence2 = "My Haley"
Output: True
Explanation: Insert "name is" between "My" and "Haley". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The shorter sentence must match a prefix plus a suffix of the longer sentence. The unmatched middle is the inserted block. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def areSentencesSimilar(self, sentence1: str, sentence2: str) -> bool:
        a,b=sentence1.split(),sentence2.split()
        if len(a) < len(b): a,b=b,a
        n=len(a)
        for l in range(n+1):
            for r in range(l,n+1):
                if a[:l]+a[r:] == b: return True
        return False
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def areSentencesSimilar(self, sentence1: str, sentence2: str) -> bool:
        a,b=sentence1.split(),sentence2.split()
        if len(a) < len(b): a,b=b,a
        pref=0
        while pref < len(b) and a[pref] == b[pref]: pref += 1
        suff=0
        while suff < len(b)-pref and a[len(a)-1-suff] == b[len(b)-1-suff]: suff += 1
        return pref + suff == len(b)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def areSentencesSimilar(self, sentence1: str, sentence2: str) -> bool:
        a,b=sentence1.split(),sentence2.split()
        if len(a) < len(b): a,b=b,a
        l=0
        while l < len(b) and a[l] == b[l]: l += 1
        ra, rb = len(a)-1, len(b)-1
        while rb >= l and a[ra] == b[rb]:
            ra -= 1; rb -= 1
        return rb < l
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Insertion may be empty.
- Always compare the shorter sentence against the longer.
- Do not double-count overlapping suffix words.

## Related
- Is Subsequence
- Expressive Words
