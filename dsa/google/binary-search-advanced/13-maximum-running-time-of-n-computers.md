# 13. Maximum Running Time of N Computers

- **Difficulty:** Hard
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You have `n` computers and several batteries. A battery can power one computer at a time, and batteries may be swapped between computers at integer times.

Return the maximum number of minutes for which all `n` computers can run simultaneously.

**Input**
- `n`: the number of computers that must run at the same time.
- `batteries`: a list of battery capacities in minutes.

**Output**
- The maximum whole minutes that all computers can run together.

## Constraints
- `1 <= n <= batteries.length <= 10^5`
- `1 <= batteries[i] <= 10^9`

## Examples
```text
Input: n = 2, batteries = [3,3,3]
Output: 4
Explanation: Two computers can run for `4` minutes using `8` total charge. Running for `5` minutes would require `10` total charge, but only `9` is available.
```

## Understanding & Intuition
For a candidate runtime `t`, a battery contributes at most `t` useful minutes because one battery cannot power multiple computers at the same instant. The feasibility check `sum(min(b, t)) >= n*t` is monotonic.

## Approach 1 — Naive / Brute Force
**Idea:** Try runtimes upward until the next minute is infeasible.
```python
class Solution:
    def maxRunTime(self, n: int, batteries: list[int]) -> int:
        limit = sum(batteries) // n
        ans = 0
        for t in range(limit + 1):
            if sum(min(b, t) for b in batteries) >= n * t:
                ans = t
            else:
                break
        return ans
```
- **Time:** O(mT) — **Space:** O(1), where `m = len(batteries)` and `T` is the answer range

## Approach 2 — Better
**Idea:** Binary search the runtime with the direct energy feasibility formula.
```python
class Solution:
    def maxRunTime(self, n: int, batteries: list[int]) -> int:
        lo, hi = 0, sum(batteries) // n
        while lo <= hi:
            mid = (lo + hi) // 2
            if sum(min(b, mid) for b in batteries) >= n * mid:
                lo = mid + 1
            else:
                hi = mid - 1
        return hi
```
- **Time:** O(m log T) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Sort batteries and remove oversized batteries that can dedicate themselves to one computer, shrinking the shared pool.
```python
class Solution:
    def maxRunTime(self, n: int, batteries: list[int]) -> int:
        batteries.sort()
        total = sum(batteries)
        while batteries and batteries[-1] > total // n:
            total -= batteries.pop()
            n -= 1
        return total // n
```
- **Time:** O(m log m) — **Space:** O(1) excluding sort storage

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mT) | O(1) |
| Better | O(m log T) | O(1) |
| Optimal | O(m log m) | O(1) |

## Edge Cases & Pitfalls
- The upper bound is total energy divided by `n`.
- Cap each battery contribution at the candidate runtime.
- Very large batteries should not inflate shared runtime beyond what other computers can sustain.

## Related
- Split Array Largest Sum
- Capacity to Ship Packages Within D Days
