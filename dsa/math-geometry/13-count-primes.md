# 13. Count Primes

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given an integer `n`, return the number of prime numbers strictly less than `n`. Constraints: `0 <= n <= 5 * 10^6`.

## Examples
```text
Input: n = 10
Output: 4
Explanation: The primes less than 10 are 2, 3, 5, and 7.
```

## Understanding & Intuition
Testing every number by trial division works but repeats a lot of work. Composite numbers have a factor no larger than their square root. The sieve marks multiples of each prime once and is the standard optimal approach.

## Approach 1 — Naive / Brute Force
**Idea:** For every number below `n`, test divisibility by all smaller numbers.
```python
class Solution:
    def countPrimes(self, n: int) -> int:
        count = 0
        for x in range(2, n):
            prime = True
            for d in range(2, x):
                if x % d == 0:
                    prime = False
                    break
            if prime:
                count += 1
        return count
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Trial divide only up to `sqrt(x)` for each candidate.
```python
class Solution:
    def countPrimes(self, n: int) -> int:
        def is_prime(x: int) -> bool:
            if x < 2:
                return False
            d = 2
            while d * d <= x:
                if x % d == 0:
                    return False
                d += 1
            return True

        return sum(1 for x in range(2, n) if is_prime(x))
```
- **Time:** O(n√n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use the Sieve of Eratosthenes and mark multiples starting at `p*p`.
```python
class Solution:
    def countPrimes(self, n: int) -> int:
        if n <= 2:
            return 0
        is_prime = [True] * n
        is_prime[0] = is_prime[1] = False
        p = 2
        while p * p < n:
            if is_prime[p]:
                # Smaller multiples were already marked by smaller primes.
                for multiple in range(p * p, n, p):
                    is_prime[multiple] = False
            p += 1
        return sum(is_prime)
```
- **Time:** O(n log log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n√n) | O(1) |
| Optimal | O(n log log n) | O(n) |

## Edge Cases & Pitfalls
- Count primes strictly less than `n`, not less than or equal.
- `n <= 2` returns `0`.

## Related
- Ugly Number
- Perfect Squares
