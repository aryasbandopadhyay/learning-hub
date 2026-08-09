# 03. Gas Station

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
There are `n` gas stations in a circle. At station `i`, you can add `gas[i]` units of fuel, and it
costs `cost[i]` units of fuel to drive from station `i` to station `(i + 1) mod n`. Start with an
empty tank.

Return the starting station index if you can travel around the circuit exactly once, or `-1` if it is
impossible. If a solution exists, it is unique.

**Input**
- `gas`: fuel available at each station.
- `cost`: fuel needed to reach the next station.

**Output**
- An integer: the valid starting index, or `-1` if none exists.

## Constraints
- n == gas.length == cost.length
- 1 <= n <= 10^5
- 0 <= gas[i], cost[i] <= 10^4

## Examples
```text
Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
Output: 3
Explanation: Starting at index `3`, the tank never drops below zero while visiting stations `3,4,0,1,2` and returning to `3`.
```

## Understanding & Intuition
If total gas is less than total cost, no start can work. While scanning, when the tank becomes negative at index `i`, no station between the current start and `i` can be valid. The safe greedy reset is to start at `i + 1`.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate the full circle from every possible start.
```python
from typing import List

class Solution:
    def canCompleteCircuit(self, gas: List[int], cost: List[int]) -> int:
        n = len(gas)
        for start in range(n):
            tank = 0
            possible = True
            for step in range(n):
                i = (start + step) % n
                tank += gas[i] - cost[i]
                if tank < 0:
                    possible = False
                    break
            if possible:
                return start
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** First reject impossible totals, then still simulate starts with early breaks.
```python
from typing import List

class Solution:
    def canCompleteCircuit(self, gas: List[int], cost: List[int]) -> int:
        if sum(gas) < sum(cost):
            return -1
        n = len(gas)
        for start in range(n):
            tank = 0
            for step in range(n):
                i = (start + step) % n
                tank += gas[i] - cost[i]
                if tank < 0:
                    break
            else:
                return start
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Reset the candidate start whenever the running tank goes negative.
```python
from typing import List

class Solution:
    def canCompleteCircuit(self, gas: List[int], cost: List[int]) -> int:
        total = 0
        tank = 0
        start = 0

        for i, (g, c) in enumerate(zip(gas, cost)):
            gain = g - c
            total += gain
            tank += gain
            if tank < 0:
                start = i + 1
                tank = 0

        return start if total >= 0 else -1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Always check total feasibility.
- Resetting before a negative tank is wrong; reset only after failure.
- The valid start is unique when a solution exists under the standard problem statement.

## Related
- Jump Game
- Circular Array Problems
