# 12. Shopping Offers

- **Difficulty:** Medium
- **Pattern:** multi-dimensional knapsack with memoization
- **Asked at:** Amazon, Google, Meta

## Problem
Given item prices, special bundle offers, and required `needs`, return the minimum cost to satisfy exactly those needs. Each offer may be used multiple times but cannot exceed any remaining need. Constraints: `1 <= len(price) <= 6`, `0 <= needs[i] <= 10`.

## Examples
```text
Input: price = [2,5], special = [[3,0,5],[1,2,10]], needs = [3,2]
Output: 14
Explanation: Use the first offer once and buy two units of the second item.
```

## Understanding & Intuition
The remaining needs vector is the DP state. A valid offer subtracts from every component without going negative. Buying all remaining items individually is the baseline fallback.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every valid offer from the current needs.
```python
class Solution:
    def shoppingOffers(self, price: list[int], special: list[list[int]], needs: list[int]) -> int:
        def dfs(rem: list[int]) -> int:
            best = sum(p * r for p, r in zip(price, rem))
            for offer in special:
                nxt = []
                ok = True
                for r, take in zip(rem, offer[:-1]):
                    if take > r:
                        ok = False
                        break
                    nxt.append(r - take)
                if ok:
                    best = min(best, offer[-1] + dfs(nxt))
            return best
        return dfs(needs)
```
- **Time:** O(b^sum(needs)) — **Space:** O(sum(needs))

## Approach 2 — Better
**Idea:** Memoize every remaining-needs tuple.
```python
class Solution:
    def shoppingOffers(self, price: list[int], special: list[list[int]], needs: list[int]) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def dp(state: tuple[int, ...]) -> int:
            best = sum(p * r for p, r in zip(price, state))
            for offer in special:
                nxt = []
                for r, take in zip(state, offer[:-1]):
                    if take > r:
                        break
                    nxt.append(r - take)
                else:
                    best = min(best, offer[-1] + dp(tuple(nxt)))
            return best
        return dp(tuple(needs))
```
- **Time:** O(S * offers * items) — **Space:** O(S)

## Approach 3 — Optimal
**Idea:** Filter non-beneficial offers, then memoize useful transitions.
```python
class Solution:
    def shoppingOffers(self, price: list[int], special: list[list[int]], needs: list[int]) -> int:
        from functools import lru_cache
        useful = []
        for offer in special:
            regular = sum(c * p for c, p in zip(offer[:-1], price))
            if offer[-1] < regular and any(offer[:-1]):
                useful.append(offer)
        @lru_cache(None)
        def dp(state: tuple[int, ...]) -> int:
            best = sum(p * r for p, r in zip(price, state))
            for offer in useful:
                nxt = []
                for r, take in zip(state, offer[:-1]):
                    if take > r:
                        break
                    nxt.append(r - take)
                else:
                    best = min(best, offer[-1] + dp(tuple(nxt)))
            return best
        return dp(tuple(needs))
```
- **Time:** O(S * useful offers * items) — **Space:** O(S)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(b^sum(needs)) | O(sum(needs)) |
| Better | O(S * offers * items) | O(S) |
| Optimal | O(S * useful offers * items) | O(S) |

## Edge Cases & Pitfalls
- Offers cannot exceed remaining needs.
- Buying remaining items individually is always valid.
- Ignore bundles that are not cheaper than buying individually.

## Related
- Profitable Schemes
- Coin Change II
