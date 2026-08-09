# 05. Push Dominoes

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Google, Facebook, Amazon

## Problem
Implement `pushDominoes` for **Push Dominoes**. Given a string `dominoes` of `L`, `R`, and `.`, return the final state after falling domino forces finish propagating.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `dominoes`: string; initial domino row.

**Output**
- A string.

## Constraints
- `0 <= len(dominoes) <= 10^5`

## Examples
```text
Input: dominoes = ".L.R...LR..L.."
Output: "LL.RR.LLRRLL.."
Explanation: Each dot run is determined by its nearest non-dot endpoints. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A run of dots only cares about the closest force to its left and right. Sentinels make boundary intervals behave like normal intervals. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def pushDominoes(self, dominoes: str) -> str:
        arr = list(dominoes)
        changed = True
        while changed:
            changed = False
            nxt = arr[:]
            for i, ch in enumerate(arr):
                if ch == '.':
                    left = i > 0 and arr[i - 1] == 'R'
                    right = i + 1 < len(arr) and arr[i + 1] == 'L'
                    if left and not right:
                        nxt[i] = 'R'; changed = True
                    elif right and not left:
                        nxt[i] = 'L'; changed = True
            arr = nxt
        return ''.join(arr)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def pushDominoes(self, dominoes: str) -> str:
        n = len(dominoes)
        force = 0
        forces = [0] * n
        for i, ch in enumerate(dominoes):
            force = n if ch == 'R' else 0 if ch == 'L' else max(force - 1, 0)
            forces[i] += force
        force = 0
        for i in range(n - 1, -1, -1):
            ch = dominoes[i]
            force = n if ch == 'L' else 0 if ch == 'R' else max(force - 1, 0)
            forces[i] -= force
        return ''.join('R' if f > 0 else 'L' if f < 0 else '.' for f in forces)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def pushDominoes(self, dominoes: str) -> str:
        marks = [(-1, 'L')] + [(i, c) for i, c in enumerate(dominoes) if c != '.'] + [(len(dominoes), 'R')]
        ans = list(dominoes)
        for p in range(len(marks) - 1):
            i, a = marks[p]
            j, b = marks[p + 1]
            if a == b:
                for k in range(i + 1, j):
                    ans[k] = a
            elif a == 'R' and b == 'L':
                l, r = i + 1, j - 1
                while l < r:
                    ans[l] = 'R'; ans[r] = 'L'; l += 1; r -= 1
        return ''.join(ans)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Opposite equal forces leave a middle dot unchanged.
- Leading dots before L fall left.
- Trailing dots after R fall right.

## Related
- Shortest Distance to a Character
- Backspace String Compare
