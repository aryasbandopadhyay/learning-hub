# 08. Minimum Window Subsequence

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given strings `s1` and `s2`, return the shortest substring of `s1` containing `s2` as a subsequence. If tied, return the leftmost shortest substring; if impossible, return `""`.

Implement `Solution.minWindow` with the parameters below and return the requested value.

**Input**
- `s1`: a `str`; the source string to search within.
- `s2`: a `str`; the subsequence to match.

**Output**
- The shortest substring of `s1` containing `s2` as a subsequence; if tied, return the leftmost one; if none exists, return `""`.

## Constraints
- 1 <= s1.length <= 2 * 10^4
- 1 <= s2.length <= 100
- `s1` and `s2` contain lowercase English letters

## Examples
```text
Input: s1 = "abcdebdde", s2 = "bde"
Output: "bcde"
Explanation: It is the leftmost shortest window containing bde as a subsequence.
```

## Understanding & Intuition
A window is valid once all of `s2` is matched in order. After finding an end, a backward scan tightens the start. Position lists can also jump directly to next matches.

## Approach 1 — Naive / Brute Force
**Idea:** Try substrings and test subsequence membership.
```python
class Solution:
    def minWindow(self, s1: str, s2: str) -> str:
        best=""; n=len(s1)
        for i in range(n):
            for j in range(i+1,n+1):
                if best and j-i>=len(best): break
                p=0
                for c in s1[i:j]:
                    if p<len(s2) and c==s2[p]: p+=1
                if p==len(s2): best=s1[i:j]; break
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Store positions of characters and binary-search the next needed occurrence.
```python
class Solution:
    def minWindow(self, s1: str, s2: str) -> str:
        from collections import defaultdict
        from bisect import bisect_left
        pos=defaultdict(list)
        for i,c in enumerate(s1): pos[c].append(i)
        bl,br=-1,len(s1)+1
        for start in pos.get(s2[0],[]):
            cur=start; ok=True
            for c in s2:
                arr=pos.get(c,[]); k=bisect_left(arr,cur)
                if k==len(arr): ok=False; break
                cur=arr[k]+1
            if ok and cur-start<br-bl: bl,br=start,cur
        return "" if bl<0 else s1[bl:br]
```
- **Time:** O(n + ab log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan forward to match all of `s2`, then scan backward to minimize that window.
```python
class Solution:
    def minWindow(self, s1: str, s2: str) -> str:
        n,m=len(s1),len(s2); best_l=0; best_len=n+1; i=0
        while i<n:
            j=0
            while i<n:
                if s1[i]==s2[j]:
                    j+=1
                    if j==m: break
                i+=1
            if i==n: break
            end=i+1; j=m-1
            while i>=0:
                if s1[i]==s2[j]:
                    j-=1
                    if j<0: break
                i-=1
            if end-i<best_len: best_l,best_len=i,end-i
            i+=1
        return "" if best_len==n+1 else s1[best_l:best_l+best_len]
```
- **Time:** O(nm) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n + ab log n) | O(n) |
| Optimal | O(nm) | O(1) |

## Edge Cases & Pitfalls
- Keep the earlier window on equal length.
- Return empty string if matching fails.
- Restart just after the shrunken start.

## Related
- Minimum Window Substring
- Is Subsequence
