# 05. Exclusive Time of Functions

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Meta, Amazon, Bloomberg

## Problem
You are given `n` functions labeled `0` to `n - 1` and a chronologically sorted list of logs. Each log is formatted as `"id:start:timestamp"` or `"id:end:timestamp"`. A function's exclusive time is the total time it runs excluding time spent in child calls. Return a list where index `i` is function `i`'s exclusive time.

Constraints: `1 <= n <= 100`, `1 <= len(logs) <= 500`, timestamps are nondecreasing, and every start has a matching end.

## Examples
```text
Input: n = 2, logs = ["0:start:0","1:start:2","1:end:5","0:end:6"]
Output: [3, 4]
Explanation: Function 0 runs at times 0,1,6; function 1 runs at times 2,3,4,5.
```

## Understanding & Intuition
Only the currently running function should receive elapsed time between two log boundaries. A call stack identifies that active function. End timestamps are inclusive, so after an end event the next interval starts at `timestamp + 1`.

## Approach 1 — Naive / Brute Force
**Idea:** Replay every integer timestamp from the previous event to the current event, assigning it to the stack top.
```python
class Solution:
    def exclusiveTime(self, n: int, logs: list[str]) -> list[int]:
        ans = [0] * n
        stack = []
        prev = None
        for log in logs:
            fid_s, typ, t_s = log.split(':')
            fid, t = int(fid_s), int(t_s)
            if prev is not None and stack:
                end = t + 1 if typ == 'end' else t
                for _ in range(prev, end):
                    ans[stack[-1]] += 1
            if typ == 'start':
                stack.append(fid)
                prev = t
            else:
                stack.pop()
                prev = t + 1
        return ans
```
- **Time:** O(T + m) — **Space:** O(n + m)

## Approach 2 — Better
**Idea:** Instead of replaying unit time, add whole elapsed intervals to the function on top of the stack.
```python
class Solution:
    def exclusiveTime(self, n: int, logs: list[str]) -> list[int]:
        ans = [0] * n
        stack = []
        prev = 0
        for log in logs:
            fid_s, typ, t_s = log.split(':')
            fid, t = int(fid_s), int(t_s)
            if typ == 'start':
                if stack:
                    ans[stack[-1]] += t - prev
                stack.append(fid)
                prev = t
            else:
                ans[stack.pop()] += t - prev + 1
                prev = t + 1
        return ans
```
- **Time:** O(m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Use the same stack interval accounting while parsing log fields manually to avoid extra temporary lists.
```python
class Solution:
    def exclusiveTime(self, n: int, logs: list[str]) -> list[int]:
        ans = [0] * n
        stack = []
        prev = 0
        for log in logs:
            a = log.find(':')
            b = log.find(':', a + 1)
            fid = int(log[:a])
            typ = log[a + 1:b]
            t = int(log[b + 1:])
            if typ == 'start':
                if stack:
                    ans[stack[-1]] += t - prev
                stack.append(fid)
                prev = t
            else:
                ans[stack.pop()] += t - prev + 1
                prev = t + 1
        return ans
```
- **Time:** O(m) — **Space:** O(m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(T + m) | O(n + m) |
| Better | O(m) | O(m) |
| Optimal | O(m) | O(m) |

## Edge Cases & Pitfalls
- End times are inclusive.
- A parent resumes at `end + 1`, not at `end`.
- Recursive calls of the same function still occupy separate stack frames.

## Related
- Basic Calculator II
- Validate Stack Sequences
- Single-Threaded CPU
