# 11. Successful Pairs of Spells and Potions

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given spell strengths `spells`, potion strengths `potions`, and an integer `success`.

A spell and potion form a successful pair if their product is at least `success`. For every spell, count how many potions form a successful pair with it.

**Input**
- `spells`: a list of spell strengths.
- `potions`: a list of potion strengths.
- `success`: the minimum required product.

**Output**
- A list of counts aligned with `spells`. **This judge compares exactly**, so `answer[i]` is the count for `spells[i]` in original order.

## Constraints
- `1 <= spells.length, potions.length <= 10^5`
- `1 <= spells[i], potions[i] <= 10^5`
- `1 <= success <= 10^10`

## Examples
```text
Input: spells = [5,1,3], potions = [1,2,3,4,5], success = 7
Output: [4,0,3]
Explanation: Spell `5` succeeds with four potions, spell `1` with none, and spell `3` with three potions.
```

## Understanding & Intuition
For each spell, the required potion strength is a lower bound. Sorting potions makes each count a binary search for the first potion meeting that bound.

## Approach 1 — Naive / Brute Force
**Idea:** Count every spell-potion product directly.
```python
class Solution:
    def successfulPairs(self, spells: list[int], potions: list[int], success: int) -> list[int]:
        ans = []
        for s in spells:
            count = 0
            for p in potions:
                if s * p >= success:
                    count += 1
            ans.append(count)
        return ans
```
- **Time:** O(nm) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Sort potions and use library lower bound for each required value.
```python
class Solution:
    def successfulPairs(self, spells: list[int], potions: list[int], success: int) -> list[int]:
        import bisect
        potions.sort()
        m = len(potions)
        ans = []
        for s in spells:
            need = (success + s - 1) // s
            pos = bisect.bisect_left(potions, need)
            ans.append(m - pos)
        return ans
```
- **Time:** O(m log m + n log m) — **Space:** O(1) excluding sorting and output

## Approach 3 — Optimal
**Idea:** Sort potions once and hand-code lower bound to avoid product overflow concerns by searching required potion values.
```python
class Solution:
    def successfulPairs(self, spells: list[int], potions: list[int], success: int) -> list[int]:
        potions.sort()
        m = len(potions)
        ans = []
        for s in spells:
            need = (success + s - 1) // s
            lo, hi = 0, m
            while lo < hi:
                mid = (lo + hi) // 2
                if potions[mid] < need:
                    lo = mid + 1
                else:
                    hi = mid
            ans.append(m - lo)
        return ans
```
- **Time:** O(m log m + n log m) — **Space:** O(1) excluding sorting and output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nm) | O(1) |
| Better | O(m log m + n log m) | O(1) |
| Optimal | O(m log m + n log m) | O(1) |

## Edge Cases & Pitfalls
- Preserve the order of `spells` in the answer.
- Compute the ceiling required potion using integers.
- A spell may have zero successful potions.

## Related
- Find K Closest Elements
- Search Insert Position
