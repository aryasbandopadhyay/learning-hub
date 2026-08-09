# 16. Maximum Value At A Given Index In A Bounded Array

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `maxValue` for **Maximum Value At A Given Index In A Bounded Array**. You need to build an array of length `n` of positive integers such that adjacent values differ by at most `1` and the total sum is at most `maxSum`. Return the maximum possible value at position `index`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `n`: integer; problem size or count as defined above.
- `index`: integer; target index.
- `maxSum`: integer; maximum allowed total sum.

**Output**
- A single integer.

## Constraints
- `1 <= n <= 10^9`, `0 <= index < n`, `n <= maxSum <= 10^9`

## Examples
```text
Input: n = 4, index = 2, maxSum = 6
Output: 2
Explanation: One valid optimal array is [1,2,2,1], so the value at index 2 is 2. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
For a chosen value at `index`, the cheapest surrounding values decrease by `1` per step until reaching `1`. If that minimum possible total fits within `maxSum`, the value is feasible. Feasibility is monotonic over the chosen value.

## Approach 1 — Naive / Brute Force
**Idea:** increase the candidate value one at a time and explicitly build the cheapest array around it.
```python
class Solution:
    def maxValue(self, n, index, maxSum):
        answer = 1
        value = 1
        while True:
            total = 0
            for i in range(n):
                total += max(1, value - abs(i - index))
                if total > maxSum:
                    break
            if total > maxSum:
                return answer
            answer = value
            value += 1
```
- **Time:** O(n * answer) — **Space:** O(1)

## Approach 2 — Better
**Idea:** compute the minimum total for each possible center value with arithmetic sums, but still scan values linearly.
```python
class Solution:
    def maxValue(self, n, index, maxSum):
        def side_sum(peak_minus_one, length):
            if peak_minus_one >= length:
                return (peak_minus_one + peak_minus_one - length + 1) * length // 2
            return peak_minus_one * (peak_minus_one + 1) // 2 + (length - peak_minus_one)

        value = 1
        answer = 1
        while True:
            total = value + side_sum(value - 1, index) + side_sum(value - 1, n - index - 1)
            if total > maxSum:
                return answer
            answer = value
            value += 1
```
- **Time:** O(answer) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** binary-search the value at `index` and use the minimum possible total for each candidate.
```python
class Solution:
    def maxValue(self, n, index, maxSum):
        def side_sum(peak_minus_one, length):
            if peak_minus_one >= length:
                return (peak_minus_one + peak_minus_one - length + 1) * length // 2
            return peak_minus_one * (peak_minus_one + 1) // 2 + (length - peak_minus_one)

        def required(value):
            return value + side_sum(value - 1, index) + side_sum(value - 1, n - index - 1)

        lo, hi = 1, maxSum
        while lo < hi:
            mid = (lo + hi + 1) // 2
            if required(mid) <= maxSum:
                lo = mid
            else:
                hi = mid - 1
        return lo
```
- **Time:** O(log maxSum) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * answer) | O(1) |
| Better | O(answer) | O(1) |
| Optimal | O(log maxSum) | O(1) |

## Edge Cases & Pitfalls
- All values must stay positive, so tails flatten at `1`.
- Count the left and right sides separately because their lengths differ.
- Use the upper-mid binary search form to return the maximum feasible value.

## Related
- Koko Eating Bananas
- Minimized Maximum of Products Distributed to Any Store
