# 07. Expressive Words

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given target string `s` and list `words`, return how many words can become `s` by stretching groups of equal characters. A group may be stretched only if the corresponding group in `s` has length at least three.

Implement `Solution.expressiveWords` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.
- `words`: a `list[str]`; the list of candidate words.

**Output**
- A `int` value representing the result described above.

## Constraints
- 1 <= s.length <= 100
- 1 <= words.length <= 100
- 1 <= words[i].length <= 100
- `s` and every `words[i]` contain lowercase English letters

## Examples
```text
Input: s = "heeellooo", words = ["hello","hi","helo"]
Output: 1
Explanation: Only "hello" can stretch to the target.
```

## Understanding & Intuition
Stretching preserves character group order. Each word group count must be equal to the target count, or smaller when the target group is long enough to stretch.

## Approach 1 — Naive / Brute Force
**Idea:** Compare runs using two pointers for every word.
```python
class Solution:
    def expressiveWords(self, s: str, words: list[str]) -> int:
        def ok(w):
            i=j=0
            while i<len(s) and j<len(w):
                if s[i]!=w[j]: return False
                i0,j0=i,j
                while i<len(s) and s[i]==s[i0]: i+=1
                while j<len(w) and w[j]==w[j0]: j+=1
                a,b=i-i0,j-j0
                if a<b or (a!=b and a<3): return False
            return i==len(s) and j==len(w)
        return sum(ok(w) for w in words)
```
- **Time:** O(total characters) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Compress strings into `(character, count)` runs, then compare run lists.
```python
class Solution:
    def expressiveWords(self, s: str, words: list[str]) -> int:
        def groups(x):
            ans=[]; i=0
            while i<len(x):
                j=i
                while j<len(x) and x[j]==x[i]: j+=1
                ans.append((x[i],j-i)); i=j
            return ans
        target=groups(s); ans=0
        for w in words:
            g=groups(w)
            if len(g)==len(target) and all(tc==wc and tn>=wn and (tn==wn or tn>=3) for (tc,tn),(wc,wn) in zip(target,g)): ans+=1
        return ans
```
- **Time:** O(total characters) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store target runs once and stream each word's runs without storing them all.
```python
class Solution:
    def expressiveWords(self, s: str, words: list[str]) -> int:
        runs=[]; i=0
        while i<len(s):
            j=i
            while j<len(s) and s[j]==s[i]: j+=1
            runs.append((s[i],j-i)); i=j
        def ok(w):
            idx=j=0
            while j<len(w) and idx<len(runs):
                c,need=runs[idx]
                if w[j]!=c: return False
                k=j
                while k<len(w) and w[k]==c: k+=1
                have=k-j
                if need<have or (need!=have and need<3): return False
                idx+=1; j=k
            return idx==len(runs) and j==len(w)
        return sum(ok(w) for w in words)
```
- **Time:** O(total characters) — **Space:** O(groups in s)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(total characters) | O(1) |
| Better | O(total characters) | O(n) |
| Optimal | O(total characters) | O(n) |

## Edge Cases & Pitfalls
- Target runs of length two cannot absorb shorter groups.
- Group characters must match in order.
- Extra word groups invalidate the word.

## Related
- Is Subsequence
- Run-Length Encoding
