# 12. Tuple With Same Product

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Google, Amazon, Meta

## Problem
Given an array `nums` of distinct positive integers, return the number of tuples `(a, b, c, d)` such that `a * b == c * d` and `a`, `b`, `c`, and `d` are values from four distinct indices in `nums`.

Tuples are ordered: if two disjoint unordered pairs have the same product, they contribute 8 ordered tuples.

Constraints: `1 <= len(nums) <= 1000`, all values are distinct positive integers.

## Examples
```text
Input: nums = [2, 3, 4, 6]
Output: 8
Explanation: The pairs (2,6) and (3,4) have product 12, generating 8 ordered tuples.
```

## Understanding & Intuition
Equal pair products are the only thing that matters. Because all numbers are distinct and positive, two different pairs with the same product cannot share an index: sharing one value would force the other value to be equal. Therefore, every pair-pair match contributes exactly 8 ordered tuples.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every ordered quadruple of distinct indices and test product equality.
```python
class Solution:
    def tupleSameProduct(self, nums: list[int]) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            for j in range(n):
                if j == i:
                    continue
                for k in range(n):
                    if k == i or k == j:
                        continue
                    for l in range(n):
                        if l == i or l == j or l == k:
                            continue
                        if nums[i] * nums[j] == nums[k] * nums[l]:
                            ans += 1
        return ans
```
- **Time:** O(n⁴) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count how many unordered index pairs produce each product, then choose two pairs per product.
```python
class Solution:
    def tupleSameProduct(self, nums):
        products = {}
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                p = nums[i] * nums[j]
                products[p] = products.get(p, 0) + 1
        ans = 0
        for count in products.values():
            ans += count * (count - 1) // 2 * 8
        return ans
```
- **Time:** O(n²) — **Space:** O(n²)

## Approach 3 — Optimal
**Idea:** Stream unordered pairs; each previous pair with the same product forms 8 new ordered tuples.
```python
class Solution:
    def tupleSameProduct(self, nums):
        products = {}
        ans = 0
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                p = nums[i] * nums[j]
                ans += products.get(p, 0) * 8
                products[p] = products.get(p, 0) + 1
        return ans
```
- **Time:** O(n²) — **Space:** O(n²)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n⁴) | O(1) |
| Better | O(n²) | O(n²) |
| Optimal | O(n²) | O(n²) |

## Edge Cases & Pitfalls
- Arrays shorter than four elements return 0.
- The input values are distinct; without that, pair overlap would need extra handling.
- Multiply pair combinations by 8 for ordered tuples.

## Related
- Four Sum II
- Count Number of Pairs With Absolute Difference K
