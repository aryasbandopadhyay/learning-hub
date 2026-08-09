# 03. 3Sum With Multiplicity

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Facebook, Amazon, Google

## Problem
Implement `threeSumMulti` for **3Sum With Multiplicity**. Given `arr` and `target`, return the number of index triples whose values sum to `target`, modulo `1_000_000_007`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `arr`: list; input integer list.
- `target`: integer; target value or string.

**Output**
- A single integer.

## Constraints
- `0 <= len(arr) <= 3000`, `0 <= arr[i] <= 100`, `0 <= target <= 300`

## Examples
```text
Input: arr = [1,1,2,2,3,3,4,4,5,5], target = 8
Output: 20
Explanation: There are 20 index triples with values summing to 8. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Bounded values make frequency counting useful. Sorted two-pointers can also count equal blocks in bulk. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def threeSumMulti(self, arr: list[int], target: int) -> int:
        mod = 1000000007
        freq = [0] * 101
        for x in arr:
            freq[x] += 1
        ans = 0
        for a in range(101):
            for b in range(a, 101):
                c = target - a - b
                if c < b or c > 100:
                    continue
                if a == b == c:
                    ans += freq[a] * (freq[a] - 1) * (freq[a] - 2) // 6
                elif a == b:
                    ans += freq[a] * (freq[a] - 1) // 2 * freq[c]
                elif b == c:
                    ans += freq[a] * freq[b] * (freq[b] - 1) // 2
                else:
                    ans += freq[a] * freq[b] * freq[c]
        return ans % mod
```
- **Time:** O(U^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def threeSumMulti(self, arr: list[int], target: int) -> int:
        mod = 1000000007
        arr = sorted(arr)
        ans = 0
        for i in range(len(arr) - 2):
            left, right = i + 1, len(arr) - 1
            while left < right:
                s = arr[i] + arr[left] + arr[right]
                if s < target:
                    left += 1
                elif s > target:
                    right -= 1
                elif arr[left] != arr[right]:
                    lv, rv, lc, rc = arr[left], arr[right], 0, 0
                    while left < right and arr[left] == lv:
                        lc += 1; left += 1
                    while right >= left and arr[right] == rv:
                        rc += 1; right -= 1
                    ans += lc * rc
                else:
                    m = right - left + 1
                    ans += m * (m - 1) // 2
                    break
        return ans % mod
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def threeSumMulti(self, arr: list[int], target: int) -> int:
        mod = 1000000007
        right = [0] * 101
        for x in arr:
            right[x] += 1
        left = [0] * 101
        ans = 0
        for b in arr:
            right[b] -= 1
            for a in range(101):
                c = target - a - b
                if 0 <= c <= 100:
                    ans += left[a] * right[c]
            left[b] += 1
        return ans % mod
```
- **Time:** O(nU) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(U^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(nU) | O(1) |

## Edge Cases & Pitfalls
- Equal values require combinations.
- Return the modulo result.
- The order of indices is fixed as i < j < k.

## Related
- 3Sum Smaller
- 4Sum
