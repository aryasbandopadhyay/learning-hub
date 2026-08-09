# 15. Grumpy Bookstore Owner

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Microsoft, DoorDash

## Problem
A bookstore owner has customer counts per minute and a grumpiness flag per minute. Calm minutes satisfy customers automatically. The owner can choose one contiguous block of `minutes` minutes to suppress grumpiness and maximize total satisfied customers.

**Input**
- `customers`: customers arriving each minute.
- `grumpy`: `0` if the owner is not grumpy at that minute, `1` otherwise.
- `minutes`: the length of the one technique window.

**Output**
- The maximum number of satisfied customers achievable.

## Constraints
- `customers.length == grumpy.length`
- `1 <= minutes <= customers.length <= 2 * 10^4`
- `0 <= customers[i] <= 1000`
- `grumpy[i]` is `0` or `1`.

## Examples
```text
Input: customers = [1,0,1,2,1,1,7,5], grumpy = [0,1,0,1,0,1,0,1], minutes = 3
Output: 16
Explanation: Naturally calm minutes contribute automatically; using the technique on the best length-3 grumpy window adds enough customers to reach `16` total.
```

## Understanding & Intuition
Customers during non-grumpy minutes are always satisfied and form a fixed base. The chosen window only adds customers who would otherwise be lost during grumpy minutes. Therefore the problem becomes finding the maximum fixed-length gain window.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible secret-technique window and recompute satisfied customers.
```python
from typing import List

class Solution:
    def maxSatisfied(self, customers: List[int], grumpy: List[int], minutes: int) -> int:
        best = 0
        n = len(customers)
        for start in range(n - minutes + 1):
            total = 0
            for i in range(n):
                if grumpy[i] == 0 or start <= i < start + minutes:
                    total += customers[i]
            best = max(best, total)
        return best
```
- **Time:** O(n * (n-minutes+1)) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Compute always-satisfied base and prefix sums of recoverable grumpy customers.
```python
from typing import List

class Solution:
    def maxSatisfied(self, customers: List[int], grumpy: List[int], minutes: int) -> int:
        base = sum(c for c, g in zip(customers, grumpy) if g == 0)
        gain_prefix = [0]
        for c, g in zip(customers, grumpy):
            gain_prefix.append(gain_prefix[-1] + (c if g else 0))
        best_gain = 0
        for right in range(minutes, len(customers) + 1):
            best_gain = max(best_gain, gain_prefix[right] - gain_prefix[right - minutes])
        return base + best_gain
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Roll the recoverable gain over a fixed-size window.
```python
from typing import List

class Solution:
    def maxSatisfied(self, customers: List[int], grumpy: List[int], minutes: int) -> int:
        base = 0
        gain = 0
        for i, (c, g) in enumerate(zip(customers, grumpy)):
            if g == 0:
                base += c
            elif i < minutes:
                gain += c
        best_gain = gain
        for right in range(minutes, len(customers)):
            if grumpy[right]:
                gain += customers[right]
            if grumpy[right - minutes]:
                gain -= customers[right - minutes]
            best_gain = max(best_gain, gain)
        return base + best_gain
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * (n-minutes+1)) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not double-count customers in non-grumpy minutes inside the chosen window.
- If `minutes == len(customers)`, all customers can be satisfied.
- The sliding gain includes only positions where `grumpy[i] == 1`.

## Related
- Maximum Average Subarray I
- Minimum Size Subarray Sum

