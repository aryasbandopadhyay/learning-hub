# 07. Minimum Index Sum of Two Lists

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Yelp, Amazon, Google

## Problem
Given two lists of favorite restaurants, return all common restaurant names with the minimum possible sum of their indices in the two lists. Return the names sorted lexicographically for deterministic output.

Constraints: `1 <= len(list1), len(list2) <= 1000`; names are non-empty strings; names are unique within each list.

## Examples
```text
Input: list1 = ["Shogun", "Tapioca Express", "Burger King", "KFC"], list2 = ["KFC", "Shogun", "Burger King"]
Output: ["Shogun"]
Explanation: Shogun has index sum 0 + 1 = 1, which is minimal.
```

## Understanding & Intuition
The task is an intersection problem with a score attached to each common key. A hashmap from restaurant name to index lets us evaluate each restaurant in the other list quickly. Ties are collected and sorted before returning.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair of restaurants and track the smallest index sum.
```python
class Solution:
    def findRestaurant(self, list1: list[str], list2: list[str]) -> list[str]:
        best = 10**18
        ans = []
        for i in range(len(list1)):
            for j in range(len(list2)):
                if list1[i] == list2[j]:
                    s = i + j
                    if s < best:
                        best = s
                        ans = [list1[i]]
                    elif s == best:
                        ans.append(list1[i])
        return sorted(ans)
```
- **Time:** O(nm + r log r) — **Space:** O(r)

## Approach 2 — Better
**Idea:** Sort `(name, index)` pairs from both lists and walk the common names with two pointers.
```python
class Solution:
    def findRestaurant(self, list1, list2):
        a = sorted((name, i) for i, name in enumerate(list1))
        b = sorted((name, i) for i, name in enumerate(list2))
        i = j = 0
        best = 10**18
        ans = []
        while i < len(a) and j < len(b):
            if a[i][0] == b[j][0]:
                s = a[i][1] + b[j][1]
                if s < best:
                    best = s
                    ans = [a[i][0]]
                elif s == best:
                    ans.append(a[i][0])
                i += 1
                j += 1
            elif a[i][0] < b[j][0]:
                i += 1
            else:
                j += 1
        return sorted(ans)
```
- **Time:** O(n log n + m log m) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Store indices from the first list in a hashmap and scan the second list once.
```python
class Solution:
    def findRestaurant(self, list1, list2):
        pos = {name: i for i, name in enumerate(list1)}
        best = 10**18
        ans = []
        for j, name in enumerate(list2):
            if name in pos:
                s = pos[name] + j
                if s < best:
                    best = s
                    ans = [name]
                elif s == best:
                    ans.append(name)
        return sorted(ans)
```
- **Time:** O(n + m + r log r) — **Space:** O(n + r)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nm + r log r) | O(r) |
| Better | O(n log n + m log m) | O(n + m) |
| Optimal | O(n + m + r log r) | O(n + r) |

## Edge Cases & Pitfalls
- There may be multiple restaurants tied for the same minimum sum.
- Return an empty list if there is no common restaurant.
- Sort the final tied names to avoid dependence on input order.

## Related
- Intersection of Two Arrays II
- Two Sum
