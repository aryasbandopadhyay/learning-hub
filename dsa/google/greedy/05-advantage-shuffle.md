# 05. Advantage Shuffle

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
Given two equal-length integer arrays `nums1` and `nums2`, permute `nums1` to maximize the number of indices `i` with `nums1[i] > nums2[i]`. Return the canonical maximizing permutation produced by assigning, in ascending order of `nums2`, the smallest available winning value when possible, otherwise the smallest available losing value.

Constraints: `1 <= len(nums1) == len(nums2) <= 10^5`, `0 <= nums1[i], nums2[i] <= 10^9`.

## Examples
```text
Input: nums1 = [12,24,8,32], nums2 = [13,25,32,11]
Output: [24,32,8,12]
Explanation: The result wins at indices 0, 1, and 3, which is maximum.
```

## Understanding & Intuition
A small `nums1` value that can beat a small `nums2` value should be used there, saving larger values for harder opponents. If no value can beat the current opponent, sacrifice the smallest remaining value. Processing opponents in ascending order makes the canonical answer deterministic.

## Approach 1 — Naive / Brute Force
**Idea:** Keep a sorted list; for each opponent in ascending order, linearly find the smallest winning value or sacrifice the smallest.
```python
class Solution:
    def advantageCount(self, nums1: list[int], nums2: list[int]) -> list[int]:
        remaining = sorted(nums1)
        ans = [0] * len(nums2)
        for value, idx in sorted((v, i) for i, v in enumerate(nums2)):
            pick = 0
            while pick < len(remaining) and remaining[pick] <= value:
                pick += 1
            if pick == len(remaining):
                pick = 0
            ans[idx] = remaining.pop(pick)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use binary search to find the winning value in a sorted list, while deletion still costs linear time.
```python
class Solution:
    def advantageCount(self, nums1, nums2):
        import bisect
        remaining = sorted(nums1)
        ans = [0] * len(nums2)
        for value, idx in sorted((v, i) for i, v in enumerate(nums2)):
            pick = bisect.bisect_right(remaining, value)
            if pick == len(remaining):
                pick = 0
            ans[idx] = remaining.pop(pick)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a Fenwick tree over sorted value counts to find the smallest available winner, or the smallest available sacrifice.
```python
class Solution:
    def advantageCount(self, nums1, nums2):
        import bisect
        values = sorted(set(nums1))
        n = len(values)
        bit = [0] * (n + 1)
        def add(i, delta):
            i += 1
            while i <= n:
                bit[i] += delta
                i += i & -i
        def prefix(i):
            s = 0
            while i > 0:
                s += bit[i]
                i -= i & -i
            return s
        def kth(k):
            idx = 0
            step = 1 << (n.bit_length() - 1)
            while step:
                nxt = idx + step
                if nxt <= n and bit[nxt] < k:
                    idx = nxt
                    k -= bit[nxt]
                step >>= 1
            return idx
        for x in nums1:
            add(bisect.bisect_left(values, x), 1)
        ans = [0] * len(nums2)
        for value, idx in sorted((v, i) for i, v in enumerate(nums2)):
            pos = bisect.bisect_right(values, value)
            before = prefix(pos)
            total = prefix(n)
            pick = kth(before + 1) if before < total else kth(1)
            ans[idx] = values[pick]
            add(pick, -1)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- The problem has many valid permutations, so this statement fixes a canonical one.
- Use `bisect_right`, because equal values do not win.
- Sacrificed values should be the smallest available values.

## Related
- Assign Cookies
- Boats to Save People

