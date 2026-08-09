# 17. Maximum White Tiles Covered by a Carpet

- **Difficulty:** Medium
- **Pattern:** intervals & sliding window
- **Asked at:** Google, Amazon, Meta

## Problem
You are given non-overlapping white tile intervals `tiles`, where `[start, end]` means every integer tile from `start` through `end` is white. A carpet of length `carpetLen` covers a consecutive range of exactly `carpetLen` integer positions. Return the maximum number of white tiles the carpet can cover.

**Input**
- `tiles`: a `list[list[int]]`; non-overlapping white tile intervals.
- `carpetLen`: a `int`; the carpet length.

**Output**
- A `int`. Return the maximum number of white tiles the carpet can cover.

## Constraints
- `1 <= len(tiles) <= 5 * 10^4`, `1 <= start <= end <= 10^9`, `1 <= carpetLen <= 10^9`, and tile intervals are non-overlapping.

## Examples
```text
Input: tiles = [[1,5],[10,11],[12,18],[20,25],[30,32]], carpetLen = 10
Output: 9
Explanation: Placing the carpet from 10 through 19 covers intervals [10,11] and [12,18], for 9 white tiles.
```

## Understanding & Intuition
An optimal carpet can be shifted left until its left edge aligns with the start of some white interval. That reduces the search to candidate starts from `tiles`. Prefix sums or a sliding window then count fully covered intervals and one possible partially covered interval.

## Approach 1 — Naive / Brute Force
**Idea:** Try starting the carpet at every tile interval's start and sum the overlap with all intervals.
```python
class Solution:
    def maximumWhiteTiles(self, tiles: list[list[int]], carpetLen: int) -> int:
        best = 0
        for start, _ in tiles:
            end = start + carpetLen - 1
            covered = 0
            for a, b in tiles:
                overlap = min(b, end) - max(a, start) + 1
                if overlap > 0:
                    covered += overlap
            best = max(best, covered)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort intervals, use prefix sums of lengths, and binary search the last interval touched by each carpet start.
```python
class Solution:
    def maximumWhiteTiles(self, tiles: list[list[int]], carpetLen: int) -> int:
        from bisect import bisect_right
        tiles = sorted(tiles)
        starts = [a for a, _ in tiles]
        prefix = [0]
        for a, b in tiles:
            prefix.append(prefix[-1] + b - a + 1)

        best = 0
        for i, (start, _) in enumerate(tiles):
            carpet_end = start + carpetLen - 1
            j = bisect_right(starts, carpet_end) - 1
            covered = prefix[max(j, i) + 1] - prefix[i]
            if j + 1 < len(tiles) and tiles[j + 1][0] <= carpet_end:
                covered += carpet_end - tiles[j + 1][0] + 1
            if j >= i and tiles[j][1] > carpet_end:
                covered -= tiles[j][1] - carpet_end
            best = max(best, covered)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort intervals and slide a window of fully covered intervals, adding one partial interval at the right edge.
```python
class Solution:
    def maximumWhiteTiles(self, tiles: list[list[int]], carpetLen: int) -> int:
        tiles = sorted(tiles)
        best = 0
        full = 0
        right = 0
        for left in range(len(tiles)):
            carpet_end = tiles[left][0] + carpetLen - 1
            while right < len(tiles) and tiles[right][1] <= carpet_end:
                full += tiles[right][1] - tiles[right][0] + 1
                right += 1
            partial = 0
            if right < len(tiles) and tiles[right][0] <= carpet_end:
                partial = carpet_end - tiles[right][0] + 1
            best = max(best, full + partial)
            if right == left:
                right += 1
            else:
                full -= tiles[left][1] - tiles[left][0] + 1
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Tile intervals are inclusive, so an interval has length `end - start + 1`.
- The rightmost touched interval may be only partially covered.
- If the carpet can cover an entire interval, keep sliding to include following intervals.

## Related
- Maximum Number of Points Covered by a Segment
- Number of Flowers in Full Bloom
