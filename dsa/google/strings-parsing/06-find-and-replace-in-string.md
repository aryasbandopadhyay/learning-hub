# 06. Find and Replace in String

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given `s` and parallel arrays `indices`, `sources`, and `targets`, perform all matching replacements simultaneously against the original string. Return the final string.

Implement `Solution.findReplaceString` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.
- `indices`: a `list[int]`; replacement starting indices.
- `sources`: a `list[str]`; source strings to match.
- `targets`: a `list[str]`; replacement strings to insert.

**Output**
- The final string after applying every matching replacement simultaneously.

## Constraints
- 1 <= s.length <= 1000
- 0 <= indices.length == sources.length == targets.length <= 100
- 0 <= indices[i] < s.length
- `sources[i]` and `targets[i]` are non-empty lowercase strings
- All replacement checks are made against the original `s` simultaneously

## Examples
```text
Input: s = "abcd", indices = [0,2], sources = ["a","cd"], targets = ["eee","ffff"]
Output: "eeebffff"
Explanation: Both sources match the original string at their indices.
```

## Understanding & Intuition
Simultaneous replacements mean indices refer to the original string. Mapping matching operations by start index lets one scan decide whether to append a target or the current character.

## Approach 1 — Naive / Brute Force
**Idea:** At each position, scan all operations for a matching start and source.
```python
class Solution:
    def findReplaceString(self, s: str, indices: list[int], sources: list[str], targets: list[str]) -> str:
        out=[]; i=0
        while i<len(s):
            done=False
            for idx,src,tgt in zip(indices,sources,targets):
                if idx==i and s.startswith(src,i): out.append(tgt); i+=len(src); done=True; break
            if not done: out.append(s[i]); i+=1
        return ''.join(out)
```
- **Time:** O(nmL) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort operations by index and copy original gaps between successful replacements.
```python
class Solution:
    def findReplaceString(self, s: str, indices: list[int], sources: list[str], targets: list[str]) -> str:
        out=[]; pos=0
        for idx,src,tgt in sorted(zip(indices,sources,targets)):
            if pos<idx: out.append(s[pos:idx]); pos=idx
            if s.startswith(src,idx): out.append(tgt); pos=idx+len(src)
        out.append(s[pos:])
        return ''.join(out)
```
- **Time:** O(n + m log m + mL) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Precompute only successful matches in a dictionary keyed by original index.
```python
class Solution:
    def findReplaceString(self, s: str, indices: list[int], sources: list[str], targets: list[str]) -> str:
        mp={idx:(src,tgt) for idx,src,tgt in zip(indices,sources,targets) if s.startswith(src,idx)}
        out=[]; i=0
        while i<len(s):
            if i in mp:
                src,tgt=mp[i]; out.append(tgt); i+=len(src)
            else:
                out.append(s[i]); i+=1
        return ''.join(out)
```
- **Time:** O(n + mL) — **Space:** O(n + m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nmL) | O(n) |
| Better | O(n + m log m + mL) | O(n + m) |
| Optimal | O(n + mL) | O(n + m) |

## Edge Cases & Pitfalls
- Check sources against the original `s`.
- Ignore non-matching operations.
- Replacements can change length.

## Related
- Replace Words
- String Compression
