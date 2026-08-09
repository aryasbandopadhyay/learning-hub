# 07. Camelcase Matching

- **Difficulty:** Medium
- **Pattern:** advanced strings
- **Asked at:** Google, Amazon

## Problem
For each string in `queries`, return whether it matches `pattern`. Lowercase letters may be inserted anywhere into `pattern`; uppercase letters must match exactly and in order.

**Input**
- `queries`: a `list[str]`; the query strings.
- `pattern`: a `str`; the pattern to match.

**Output**
- A `list[bool]`. Return whether it matches `pattern`. This judge compares the sequence exactly: `answer[i]` must describe whether `queries[i]` matches, preserving query order.

## Constraints
- `1 <= len(queries) <= 100`.
- `1 <= len(pattern), len(query) <= 100`.

## Examples
```text
Input: queries = ["FooBar", "FooBarTest", "FootBall", "FrameBuffer", "ForceFeedBack"], pattern = "FB"
Output: [True, False, True, True, False]
Explanation: Extra lowercase letters are allowed, but extra uppercase letters are not. The output is written in the required deterministic order.
```

## Understanding & Intuition
The pattern must be a subsequence of each query. The only characters that may be skipped are lowercase query characters. Any unmatched uppercase character immediately makes the query fail.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose to match a character or skip it when lowercase.
```python
class Solution:
    def camelMatch(self, queries: list[str], pattern: str) -> list[bool]:
        from functools import lru_cache
        def check(query):
            @lru_cache(None)
            def dfs(i, j):
                if i == len(query):
                    return j == len(pattern)
                if j < len(pattern) and query[i] == pattern[j] and dfs(i + 1, j + 1):
                    return True
                return query[i].islower() and dfs(i + 1, j)
            return dfs(0, 0)
        return [check(query) for query in queries]
```
- **Time:** O(QLP) — **Space:** O(LP)

## Approach 2 — Better
**Idea:** Use a pointer into the pattern and reject unmatchable uppercase letters.
```python
class Solution:
    def camelMatch(self, queries, pattern):
        ans = []
        for query in queries:
            j = 0
            ok = True
            for ch in query:
                if j < len(pattern) and ch == pattern[j]:
                    j += 1
                elif ch.isupper():
                    ok = False
                    break
            ans.append(ok and j == len(pattern))
        return ans
```
- **Time:** O(total length) — **Space:** O(1) extra

## Approach 3 — Optimal
**Idea:** First compare uppercase skeletons, then verify the full constrained subsequence.
```python
class Solution:
    def camelMatch(self, queries, pattern):
        result = []
        target_caps = ''.join(ch for ch in pattern if ch.isupper())
        for query in queries:
            if ''.join(ch for ch in query if ch.isupper()) != target_caps:
                result.append(False)
                continue
            j = 0
            for ch in query:
                if j < len(pattern) and ch == pattern[j]:
                    j += 1
            result.append(j == len(pattern))
        return result
```
- **Time:** O(total length) — **Space:** O(P) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(QLP) | O(LP) |
| Better | O(total length) | O(1) |
| Optimal | O(total length) | O(P) |

## Edge Cases & Pitfalls
- Extra lowercase characters are allowed anywhere.
- Extra uppercase characters are never allowed.
- The entire pattern must be consumed.

## Related
- Subsequence matching
- Pattern matching
