# 14. Lemonade Change

- **Difficulty:** Easy
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Apple

## Problem
Each lemonade costs `$5`. Customers pay in order with bills of `$5`, `$10`, or `$20`. Return `True` if you can provide correct change to every customer, starting with no cash. Constraints: `1 <= len(bills) <= 10^5`, `bills[i]` is `5`, `10`, or `20`.

## Examples
```text
Input: bills = [5,5,5,10,20]
Output: True
Explanation: Use one $10 and one $5 as change for the $20 bill.
```

## Understanding & Intuition
Five-dollar bills are the most flexible because every change combination needs them. For a `$20`, prefer giving `$10 + $5` over three `$5` bills to preserve smaller change. This local choice is safe because `$10` bills only help with `$20` customers.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try all possible change combinations at each customer.
```python
from typing import List

class Solution:
    def lemonadeChange(self, bills: List[int]) -> bool:
        def dfs(i: int, five: int, ten: int) -> bool:
            if i == len(bills):
                return True
            bill = bills[i]
            if bill == 5:
                return dfs(i + 1, five + 1, ten)
            if bill == 10:
                return five > 0 and dfs(i + 1, five - 1, ten + 1)
            option1 = ten > 0 and five > 0 and dfs(i + 1, five - 1, ten - 1)
            option2 = five >= 3 and dfs(i + 1, five - 3, ten)
            return option1 or option2

        return dfs(0, 0, 0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Track bill counts and explicitly check the valid change combinations.
```python
from typing import List

class Solution:
    def lemonadeChange(self, bills: List[int]) -> bool:
        five = 0
        ten = 0

        for bill in bills:
            if bill == 5:
                five += 1
            elif bill == 10:
                if five == 0:
                    return False
                five -= 1
                ten += 1
            else:
                if ten >= 1 and five >= 1:
                    ten -= 1
                    five -= 1
                elif five >= 3:
                    five -= 3
                else:
                    return False
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** For `$20`, greedily use `$10 + $5` first to preserve `$5` bills when possible.
```python
from typing import List

class Solution:
    def lemonadeChange(self, bills: List[int]) -> bool:
        five = 0
        ten = 0

        for bill in bills:
            if bill == 5:
                five += 1
            elif bill == 10:
                if five == 0:
                    return False
                five -= 1
                ten += 1
            else:
                if ten > 0 and five > 0:
                    ten -= 1
                    five -= 1
                elif five >= 3:
                    five -= 3
                else:
                    return False
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A first bill of `$10` or `$20` immediately fails.
- For `$20`, using `$10 + $5` is better than using three `$5` bills.
- There is no need to track `$20` bills because they are never used as change.

## Related
- Simulation
- Coin Change Greedy
