# 07. Final Prices With a Special Discount

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
For each item price `prices[i]`, subtract the price of the first item to its right with price less than or equal to `prices[i]`. If no such item exists, keep the original price. Return the final prices. Constraints: `1 <= len(prices) <= 500`, `1 <= prices[i] <= 1000`.

## Examples
```text
Input: prices = [8,4,6,2,3]
Output: [4,2,4,2,3]
Explanation: Prices 8, 4, and 6 receive discounts 4, 2, and 2 respectively.
```

## Understanding & Intuition
This is a next-smaller-or-equal query for every index. A direct scan finds the first eligible discount. A monotonic increasing stack resolves pending items as soon as their first discount appears.

## Approach 1 — Naive / Brute Force
**Idea:** For each possible discount, verify no earlier eligible discount exists.
```python
from typing import List

class Solution:
    def finalPrices(self, prices: List[int]) -> List[int]:
        ans = prices[:]
        for i in range(len(prices)):
            for j in range(i + 1, len(prices)):
                if prices[j] <= prices[i] and all(prices[k] > prices[i] for k in range(i + 1, j)):
                    ans[i] = prices[i] - prices[j]
                    break
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Scan right from each item and stop at the first price not greater than it.
```python
from typing import List

class Solution:
    def finalPrices(self, prices: List[int]) -> List[int]:
        ans = prices[:]
        for i in range(len(prices)):
            for j in range(i + 1, len(prices)):
                if prices[j] <= prices[i]:
                    ans[i] -= prices[j]
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain item indexes waiting for their first smaller-or-equal price.
```python
from typing import List

class Solution:
    def finalPrices(self, prices: List[int]) -> List[int]:
        ans = prices[:]
        stack = []
        for i, price in enumerate(prices):
            while stack and prices[stack[-1]] >= price:
                ans[stack.pop()] -= price
            stack.append(i)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The discount must be the first eligible item to the right.
- Equal prices qualify as discounts.
- Return a new list rather than relying on in-place mutation.

## Related
- Next Smaller Element
- Daily Temperatures
