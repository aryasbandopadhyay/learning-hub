# 09. Decode Ways II

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
A message string `s` contains digits and `*`, where `*` can be any digit from 1 to 9. Count decodings under `1 -> A` through `26 -> Z`, modulo `10^9 + 7`.

Implement `Solution.numDecodings` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- 1 <= s.length <= 10^5
- `s` contains digits and the character `*`
- Return the count modulo `10^9 + 7`

## Examples
```text
Input: s = "1*"
Output: 18
Explanation: There are 9 single-character choices and 9 valid two-character choices.
```

## Understanding & Intuition
Every position may stand alone or combine with the previous character. Wildcards multiply the number of valid one- and two-character choices.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize recursive suffix counts using helper functions for one- and two-character choice counts.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        from functools import lru_cache
        MOD=10**9+7
        def one(c): return 9 if c=='*' else 0 if c=='0' else 1
        def two(a,b):
            if a=='*' and b=='*': return 15
            if a=='*': return 2 if b<='6' else 1
            if b=='*': return 9 if a=='1' else 6 if a=='2' else 0
            return 1 if 10<=int(a+b)<=26 else 0
        @lru_cache(None)
        def dp(i):
            if i==len(s): return 1
            ans=one(s[i])*dp(i+1)
            if i+1<len(s): ans+=two(s[i],s[i+1])*dp(i+2)
            return ans%MOD
        return dp(0)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Fill a prefix DP table where `dp[i]` is the number of decodings for the first `i` characters.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        MOD=10**9+7; n=len(s); dp=[0]*(n+1); dp[0]=1
        def one(c): return 9 if c=='*' else 0 if c=='0' else 1
        def two(a,b):
            if a=='*' and b=='*': return 15
            if a=='*': return 2 if b<='6' else 1
            if b=='*': return 9 if a=='1' else 6 if a=='2' else 0
            return 1 if 10<=int(a+b)<=26 else 0
        for i,c in enumerate(s,1):
            dp[i]=one(c)*dp[i-1]
            if i>=2: dp[i]+=two(s[i-2],c)*dp[i-2]
            dp[i]%=MOD
        return dp[n]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep only the previous two DP values.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        MOD=10**9+7
        def one(c): return 9 if c=='*' else 0 if c=='0' else 1
        def two(a,b):
            if a=='*' and b=='*': return 15
            if a=='*': return 2 if b<='6' else 1
            if b=='*': return 9 if a=='1' else 6 if a=='2' else 0
            return 1 if 10<=int(a+b)<=26 else 0
        prev2,prev1=1,one(s[0])
        for i in range(1,len(s)):
            prev2,prev1=prev1,(one(s[i])*prev1+two(s[i-1],s[i])*prev2)%MOD
        return prev1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `0` cannot decode by itself.
- `**` contributes 15 two-digit choices.
- Always apply the modulo.

## Related
- Decode Ways
- Distinct Subsequences
