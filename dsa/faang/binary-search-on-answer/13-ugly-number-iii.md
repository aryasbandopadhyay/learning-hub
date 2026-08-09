# 13. Ugly Number III

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Amazon, Microsoft

## Problem
Implement `nthUglyNumber` for **Ugly Number III**. A positive integer is ugly if divisible by at least one of `a`, `b`, or `c`. Return the `n`-th ugly number.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `n`: integer; problem size or count as defined above.
- `a`: integer; parameter defined by the problem.
- `b`: integer; parameter defined by the problem.
- `c`: integer; parameter defined by the problem.

**Output**
- A single integer.

## Constraints
- `1 <= n,a,b,c <= 10^9`, answer `<= 2 * 10^9`

## Examples
```text
Input: n = 5, a = 2, b = 11, c = 13
Output: 10
Explanation: The ugly numbers are 2, 4, 6, 8, and 10. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Inclusion-exclusion counts ugly numbers up to any `x`. The count grows monotonically, so the answer is the first `x` with count at least `n`.

## Approach 1 — Naive / Brute Force
**Idea:** scan integers one by one.
```python
class Solution:
    def nthUglyNumber(self, n, a, b, c):
        count = 0; x = 0
        while count < n:
            x += 1
            if x % a == 0 or x % b == 0 or x % c == 0:
                count += 1
        return x
```

- **Time:** O(answer) — **Space:** O(1)

## Approach 2 — Better
**Idea:** generate candidates with a heap and deduplicate.
```python
class Solution:
    def nthUglyNumber(self, n, a, b, c):
        import heapq
        heap=[a,b,c]; heapq.heapify(heap); seen=set(heap); val=0
        for _ in range(n):
            val=heapq.heappop(heap)
            for d in (a,b,c):
                nxt=val+d
                if nxt not in seen:
                    seen.add(nxt); heapq.heappush(heap,nxt)
        return val
```

- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** binary-search with inclusion-exclusion counts.
```python
class Solution:
    def nthUglyNumber(self, n, a, b, c):
        def gcd(x,y):
            while y: x,y=y,x%y
            return x
        def lcm(x,y): return x//gcd(x,y)*y
        ab,ac,bc=lcm(a,b),lcm(a,c),lcm(b,c); abc=lcm(ab,c)
        def cnt(x):
            return x//a + x//b + x//c - x//ab - x//ac - x//bc + x//abc
        lo,hi=1,2000000000
        while lo<hi:
            mid=(lo+hi)//2
            if cnt(mid) >= n: hi=mid
            else: lo=mid+1
        return lo
```

- **Time:** O(log answer) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(answer) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(log answer) | O(1) |


## Edge Cases & Pitfalls
- Use inclusion-exclusion to avoid double-counting.
- Compute LCM via GCD.
- Heap generation must deduplicate shared multiples.


## Related
- Kth Smallest Number in Multiplication Table
- Super Ugly Number
