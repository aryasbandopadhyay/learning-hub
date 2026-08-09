# 18. Dota2 Senate

- **Difficulty:** Medium
- **Pattern:** greedy scheduling
- **Asked at:** Amazon, Google, Bloomberg

## Problem
Implement `predictPartyVictory` for **Dota2 Senate**. In the Dota2 senate, each character in `senate` is either `"R"` for Radiant or `"D"` for Dire. Senators act in order repeatedly. On a turn, an active senator bans one opposing senator's future rights. Return the party that will eventually win, either `"Radiant"` or `"Dire"`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `senate`: string; senate party string.

**Output**
- A string.

## Constraints
- `1 <= len(senate) <= 10^4` and `senate[i]` is either `"R"` or `"D"`

## Examples
```text
Input: senate = "RDD"
Output: "Dire"
Explanation: The first Radiant senator bans one Dire senator, then the remaining Dire senator bans Radiant. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A senator should ban the earliest opposing senator who would otherwise act soonest. This preserves as much future voting power as possible for the senator's party. Queueing future turns models the circular order cleanly.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate the circular senate directly, removing senators when a pending ban applies.
```python
class Solution:
    def predictPartyVictory(self, senate: str) -> str:
        senators = list(senate)
        counts = {"R": senate.count("R"), "D": senate.count("D")}
        bans = {"R": 0, "D": 0}
        i = 0
        while counts["R"] and counts["D"]:
            party = senators[i]
            if bans[party] > 0:
                bans[party] -= 1
                counts[party] -= 1
                senators.pop(i)
                if senators:
                    i %= len(senators)
            else:
                other = "D" if party == "R" else "R"
                bans[other] += 1
                i = (i + 1) % len(senators)
        return "Radiant" if counts["R"] else "Dire"
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sweep rounds while carrying a signed ban balance; surviving senators form the next round.
```python
class Solution:
    def predictPartyVictory(self, senate: str) -> str:
        current = senate
        balance = 0
        while "R" in current and "D" in current:
            nxt = []
            for party in current:
                if party == "R":
                    if balance >= 0:
                        nxt.append("R")
                    balance += 1
                else:
                    if balance <= 0:
                        nxt.append("D")
                    balance -= 1
            current = "".join(nxt)
        return "Radiant" if current and current[0] == "R" else "Dire"
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store the active indices for each party in queues; the earlier index acts and returns in the next round.
```python
class Solution:
    def predictPartyVictory(self, senate: str) -> str:
        from collections import deque

        n = len(senate)
        radiant = deque(i for i, c in enumerate(senate) if c == "R")
        dire = deque(i for i, c in enumerate(senate) if c == "D")
        while radiant and dire:
            r = radiant.popleft()
            d = dire.popleft()
            if r < d:
                radiant.append(r + n)
            else:
                dire.append(d + n)
        return "Radiant" if radiant else "Dire"
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Bans apply to future turns, not necessarily to the nearest character in the string.
- The circular order is important after each full pass.
- Return the full party name, not `"R"` or `"D"`.

## Related
- Reveal Cards in Increasing Order
- Task Scheduler
