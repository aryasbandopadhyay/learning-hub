# 13. Grumpy Bookstore Owner

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google, LeetCode

## Problem
A bookstore owner has `customers[i]` customers in minute `i`. If `grumpy[i] == 0`, those customers are satisfied; if `grumpy[i] == 1`, they are not satisfied unless the owner uses a secret technique for one contiguous block of `minutes` minutes. Return the maximum number of satisfied customers possible.

Implement `Solution.maxSatisfied` with the parameters below and return the requested value.

**Input**
- `customers`: a `list[int]`; customers arriving at each minute.
- `grumpy`: a `list[int]`; whether the owner is grumpy at each minute.
- `minutes`: a `int`; the length of the secret-technique window.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(customers) == len(grumpy) <= 20000`
- `0 <= customers[i] <= 1000`
- `grumpy[i]` is `0` or `1`
- `1 <= minutes <= len(customers)`

## Examples
```text
Input: customers = [1, 0, 1, 2, 1, 1, 7, 5], grumpy = [0, 1, 0, 1, 0, 1, 0, 1], minutes = 3
Output: 16
Explanation: The always-satisfied total is 10, and using the technique over minutes 5-7 adds 6 more customers.
```

## Understanding & Intuition
Customers during non-grumpy minutes are always satisfied, independent of the technique. The technique only adds customers from grumpy minutes inside one fixed-length window. Therefore the task is to maximize the extra recoverable customers in any window of length `minutes`.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible technique window and recompute the satisfied total for that choice.
```python
class Solution:
    def maxSatisfied(self, customers: list[int], grumpy: list[int], minutes: int) -> int:
        n = len(customers)
        best = 0
        for start in range(n - minutes + 1):
            total = 0
            end = start + minutes
            for i in range(n):
                if grumpy[i] == 0 or start <= i < end:
                    total += customers[i]
            best = max(best, total)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Compute always-satisfied customers once, then use prefix sums of recoverable grumpy customers for each window.
```python
class Solution:
    def maxSatisfied(self, customers: list[int], grumpy: list[int], minutes: int) -> int:
        n = len(customers)
        base = 0
        gain_prefix = [0]
        for c, g in zip(customers, grumpy):
            if g == 0:
                base += c
                gain_prefix.append(gain_prefix[-1])
            else:
                gain_prefix.append(gain_prefix[-1] + c)
        best_gain = 0
        for start in range(n - minutes + 1):
            best_gain = max(best_gain, gain_prefix[start + minutes] - gain_prefix[start])
        return base + best_gain
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Slide one fixed-size window of recoverable grumpy customers while keeping the always-satisfied base.
```python
class Solution:
    def maxSatisfied(self, customers: list[int], grumpy: list[int], minutes: int) -> int:
        base = 0
        gain = 0
        best_gain = 0
        for i, c in enumerate(customers):
            if grumpy[i] == 0:
                base += c
            else:
                gain += c
            if i >= minutes and grumpy[i - minutes] == 1:
                gain -= customers[i - minutes]
            best_gain = max(best_gain, gain)
        return base + best_gain
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not double-count customers from non-grumpy minutes inside the technique window.
- `minutes` may cover the whole array.
- A window's gain only includes positions where `grumpy[i] == 1`.

## Related
- Maximum Average Subarray I
- Max Consecutive Ones III
