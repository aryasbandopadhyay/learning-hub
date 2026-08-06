# 13. Assign Cookies

- **Difficulty:** Easy
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given children greed factors `g` and cookie sizes `s`, assign at most one cookie to each child. A child is content if assigned a cookie with size at least their greed. Return the maximum number of content children. Constraints: `1 <= len(g), len(s) <= 3 * 10^4`, `1 <= g[i], s[j] <= 2^31 - 1`.

## Examples
```text
Input: g = [1,2,3], s = [1,1]
Output: 1
Explanation: Only one child with greed 1 can be satisfied.
```

## Understanding & Intuition
The least greedy child should get the smallest cookie that can satisfy them. Giving a larger cookie when a smaller one works cannot help future children. Sorting both lists makes this greedy pairing direct.

## Approach 1 — Naive / Brute Force
**Idea:** Try all assignments of cookies to children.
```python
from typing import List

class Solution:
    def findContentChildren(self, g: List[int], s: List[int]) -> int:
        used = [False] * len(s)

        def dfs(child: int) -> int:
            if child == len(g):
                return 0
            best = dfs(child + 1)
            for cookie in range(len(s)):
                if not used[cookie] and s[cookie] >= g[child]:
                    used[cookie] = True
                    best = max(best, 1 + dfs(child + 1))
                    used[cookie] = False
            return best

        return dfs(0)
```
- **Time:** O(m^n) — **Space:** O(m + n)

## Approach 2 — Better
**Idea:** Sort cookies and, for each child, scan for the smallest unused fitting cookie.
```python
from typing import List

class Solution:
    def findContentChildren(self, g: List[int], s: List[int]) -> int:
        g.sort()
        s.sort()
        used = [False] * len(s)
        content = 0

        for greed in g:
            for i, size in enumerate(s):
                if not used[i] and size >= greed:
                    used[i] = True
                    content += 1
                    break
        return content
```
- **Time:** O(nm + n log n + m log m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Use two pointers over sorted greed factors and cookie sizes.
```python
from typing import List

class Solution:
    def findContentChildren(self, g: List[int], s: List[int]) -> int:
        g.sort()
        s.sort()
        child = 0
        cookie = 0

        while child < len(g) and cookie < len(s):
            if s[cookie] >= g[child]:
                child += 1
            cookie += 1
        return child
```
- **Time:** O(n log n + m log m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m^n) | O(m + n) |
| Better | O(nm + n log n + m log m) | O(m) |
| Optimal | O(n log n + m log m) | O(1) |

## Edge Cases & Pitfalls
- If there are no cookies, the answer is `0`.
- Sort both arrays before using two pointers.
- Do not waste large cookies on children that smaller cookies can satisfy.

## Related
- Two Pointers
- Boats to Save People
