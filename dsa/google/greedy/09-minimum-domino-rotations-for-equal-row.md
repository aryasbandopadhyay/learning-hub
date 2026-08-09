# 09. Minimum Domino Rotations For Equal Row

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
You are given two arrays `tops` and `bottoms` representing a row of dominoes. Domino `i` has value `tops[i]` on top and `bottoms[i]` on bottom.

In one rotation, swap the top and bottom values of one domino. Return the minimum rotations needed so that all top values are equal or all bottom values are equal. If impossible, return `-1`.

**Input**
- `tops`: top values of the dominoes.
- `bottoms`: bottom values of the same dominoes.

**Output**
- The minimum rotations to make one row uniform, or `-1` if no value can fill a row.

## Constraints
- `2 <= tops.length == bottoms.length <= 2 * 10^4`
- `1 <= tops[i], bottoms[i] <= 6`

## Examples
```text
Input: tops = [2,1,2,4,2,2], bottoms = [5,2,6,2,3,2]
Output: 2
Explanation: Making every top value equal to `2` takes two rotations. No solution needs fewer rotations.
```

## Understanding & Intuition
If a value can fill a whole row, it must appear on every domino in at least one of its two halves. Such a value must be either `tops[0]` or `bottoms[0]`. Testing only these candidates is enough.

## Approach 1 — Naive / Brute Force
**Idea:** Try each face value from 1 to 6 and count rotations for making either row equal to it.
```python
class Solution:
    def minDominoRotations(self, tops: list[int], bottoms: list[int]) -> int:
        best = 10 ** 9
        for target in range(1, 7):
            top_moves = bottom_moves = 0
            ok = True
            for a, b in zip(tops, bottoms):
                if a != target and b != target:
                    ok = False
                    break
                if a != target:
                    top_moves += 1
                if b != target:
                    bottom_moves += 1
            if ok:
                best = min(best, top_moves, bottom_moves)
        return -1 if best == 10 ** 9 else best
```
- **Time:** O(6n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count face occurrences in tops, bottoms, and same-position doubles to identify possible values.
```python
class Solution:
    def minDominoRotations(self, tops, bottoms):
        n = len(tops)
        top_count = [0] * 7
        bottom_count = [0] * 7
        same = [0] * 7
        for a, b in zip(tops, bottoms):
            top_count[a] += 1
            bottom_count[b] += 1
            if a == b:
                same[a] += 1
        best = 10 ** 9
        for v in range(1, 7):
            if top_count[v] + bottom_count[v] - same[v] == n:
                best = min(best, n - top_count[v], n - bottom_count[v])
        return -1 if best == 10 ** 9 else best
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Only test the two values from the first domino; any global target must be one of them.
```python
class Solution:
    def minDominoRotations(self, tops, bottoms):
        def check(target):
            top_moves = bottom_moves = 0
            for a, b in zip(tops, bottoms):
                if a != target and b != target:
                    return 10 ** 9
                if a != target:
                    top_moves += 1
                if b != target:
                    bottom_moves += 1
            return min(top_moves, bottom_moves)
        ans = min(check(tops[0]), check(bottoms[0]))
        return -1 if ans == 10 ** 9 else ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(6n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A candidate must appear in at least one half of every domino.
- Rotations can target either the top row or the bottom row.
- Equal halves need no rotation for either row.

## Related
- Greedy Candidate Reduction
- Minimum Swaps
