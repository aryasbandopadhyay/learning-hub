# 06. Divide Chocolate

- **Difficulty:** Hard
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Amazon, Facebook

## Problem
Given chunk sweetness values, make `k` cuts into `k + 1` contiguous pieces, give away `k` pieces, and maximize the minimum sweetness of the piece you keep. Constraints: `1 <= len(sweetness) <= 10^5`, `0 <= k < len(sweetness)`.

## Examples
```text
Input: sweetness = [1, 2, 3, 4, 5, 6, 7, 8, 9], k = 5
Output: 6
Explanation: Six pieces can all have sweetness at least 6.
```

## Understanding & Intuition
A target minimum is feasible if greedy cuts can create at least `k + 1` pieces. Smaller targets remain feasible, so maximize the feasible target.

## Approach 1 — Naive / Brute Force
**Idea:** scan targets.
```python
class Solution:
    def maximizeSweetness(self, sweetness, k):
        need = k + 1
        def can(target):
            pieces = cur = 0
            for s in sweetness:
                cur += s
                if cur >= target:
                    pieces += 1; cur = 0
            return pieces >= need
        ans=0
        for t in range(1, sum(sweetness)//need + 1):
            if can(t): ans=t
        return ans
```

- **Time:** O(nS) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search the full sum range.
```python
class Solution:
    def maximizeSweetness(self, sweetness, k):
        need = k + 1
        def can(target):
            pieces = cur = 0
            for s in sweetness:
                cur += s
                if cur >= target:
                    pieces += 1; cur = 0
            return pieces >= need
        lo,hi,ans=1,sum(sweetness),0
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; lo=mid+1
            else: hi=mid-1
        return ans
```

- **Time:** O(n log S) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** binary-search with a tight average upper bound.
```python
class Solution:
    def maximizeSweetness(self, sweetness, k):
        need=k+1
        def can(target):
            pieces=cur=0
            for s in sweetness:
                cur += s
                if cur >= target:
                    pieces += 1
                    if pieces == need: return True
                    cur = 0
            return False
        lo,hi,ans=min(sweetness),sum(sweetness)//need,0
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; lo=mid+1
            else: hi=mid-1
        return ans
```

- **Time:** O(n log S) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nS) | O(1) |
| Better | O(n log S) | O(1) |
| Optimal | O(n log S) | O(1) |


## Edge Cases & Pitfalls
- Need `k + 1` pieces.
- Positive sweetness makes greedy early cuts safe.
- More than enough pieces proves feasibility.


## Related
- Maximum Candies Allocated to K Children
- Split Array Largest Sum
