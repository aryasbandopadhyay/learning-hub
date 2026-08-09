# 11. Sum of Subarray Minimums

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Microsoft

## Problem
You are given an integer array `arr`.

For every non-empty contiguous subarray, take the minimum element of that subarray. Return the sum of all those minimums modulo `10^9 + 7`.

**Input**
- `arr`: a list of integers.

**Output**
- The sum of all subarray minimums, modulo `10^9 + 7`.

## Constraints
- `1 <= arr.length <= 3 * 10^4`
- `1 <= arr[i] <= 3 * 10^4`

## Examples
```text
Input: arr = [3,1,2,4]
Output: 17
Explanation: The subarray minimums sum to `3 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 4 = 17`.
```

## Understanding & Intuition
Each element contributes as the minimum for some number of subarrays. To avoid double-counting equal values, use a strict comparison on one side and a non-strict comparison on the other. Monotonic stacks find the previous and next smaller boundaries efficiently.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subarrays and maintain the current minimum.
```python
class Solution:
    def sumSubarrayMins(self, arr: list[int]) -> int:
        mod = 10 ** 9 + 7
        ans = 0
        n = len(arr)
        for i in range(n):
            mn = arr[i]
            for j in range(i, n):
                mn = min(mn, arr[j])
                ans = (ans + mn) % mod
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each index, expand left and right until a smaller tie-breaking boundary is found.
```python
class Solution:
    def sumSubarrayMins(self, arr: list[int]) -> int:
        mod = 10 ** 9 + 7
        n = len(arr)
        ans = 0
        for i in range(n):
            left = i
            while left - 1 >= 0 and arr[left - 1] > arr[i]:
                left -= 1
            right = i
            while right + 1 < n and arr[right + 1] >= arr[i]:
                right += 1
            ans = (ans + arr[i] * (i - left + 1) * (right - i + 1)) % mod
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use monotonic stacks to compute each element's span to the previous strictly smaller and next smaller-or-equal value.
```python
class Solution:
    def sumSubarrayMins(self, arr: list[int]) -> int:
        mod = 10 ** 9 + 7
        n = len(arr)
        prev = [-1] * n
        stack = []
        for i, x in enumerate(arr):
            while stack and arr[stack[-1]] > x:
                stack.pop()
            prev[i] = stack[-1] if stack else -1
            stack.append(i)
        nxt = [n] * n
        stack = []
        for i in range(n - 1, -1, -1):
            while stack and arr[stack[-1]] >= arr[i]:
                stack.pop()
            nxt[i] = stack[-1] if stack else n
            stack.append(i)
        ans = 0
        for i, x in enumerate(arr):
            ans = (ans + x * (i - prev[i]) * (nxt[i] - i)) % mod
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal elements need consistent tie-breaking to avoid duplicate contribution.
- Apply the modulo to the accumulated sum.
- A single element contributes to exactly one subarray.

## Related
- Largest Rectangle in Histogram
- Maximum Subarray Min-Product
