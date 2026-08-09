# 23. Accounts Merge

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Each account is `[name, email1, email2, ...]`. Accounts belong to the same person if they share at least one email; shared emails can connect more than two accounts.

Merge connected accounts. Each merged account contains the name followed by all unique emails in that group.

**Input**
- `accounts`: a list of accounts.

**Output**
- A list of merged accounts. Emails inside each account must be sorted lexicographically. **This judge compares exactly**: groups are emitted in the order their representative is first encountered while scanning input accounts and emails left to right.

## Constraints
- `1 <= accounts.length <= 1000`
- `2 <= accounts[i].length <= 10`
- `1 <= name.length, email.length <= 30`
- Accounts sharing an email have the same name.

## Examples
```text
Input: accounts = [["John","a@mail.com","b@mail.com"],["John","b@mail.com","c@mail.com"]]
Output: [["John","a@mail.com","b@mail.com","c@mail.com"]]
Explanation: The two John accounts share `b@mail.com`, so their emails are one connected group. The merged row keeps the name and sorted unique emails.
```

## Understanding & Intuition
Emails are nodes and accounts connect emails. Connected components become merged accounts.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import defaultdict

class Solution:
    def accountsMerge(self, accounts: List[List[str]]) -> List[List[str]]:
        # Union emails in the same account.
        parent = {}
        def find(x):
            parent.setdefault(x, x)
            if parent[x] != x: parent[x] = find(parent[x])
            return parent[x]
        name = {}
        for acc in accounts:
            first = acc[1]
            for email in acc[1:]:
                name[email] = acc[0]
                parent[find(email)] = find(first)
        groups = defaultdict(list)
        for email in name:
            groups[find(email)].append(email)
        return [[name[root]] + sorted(emails) for root, emails in groups.items()]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import defaultdict

class Solution:
    def accountsMerge(self, accounts: List[List[str]]) -> List[List[str]]:
        # Union emails in the same account.
        parent = {}
        def find(x):
            parent.setdefault(x, x)
            if parent[x] != x: parent[x] = find(parent[x])
            return parent[x]
        name = {}
        for acc in accounts:
            first = acc[1]
            for email in acc[1:]:
                name[email] = acc[0]
                parent[find(email)] = find(first)
        groups = defaultdict(list)
        for email in name:
            groups[find(email)].append(email)
        return [[name[root]] + sorted(emails) for root, emails in groups.items()]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import defaultdict

class Solution:
    def accountsMerge(self, accounts: List[List[str]]) -> List[List[str]]:
        # Union emails in the same account.
        parent = {}
        def find(x):
            parent.setdefault(x, x)
            if parent[x] != x: parent[x] = find(parent[x])
            return parent[x]
        name = {}
        for acc in accounts:
            first = acc[1]
            for email in acc[1:]:
                name[email] = acc[0]
                parent[find(email)] = find(first)
        groups = defaultdict(list)
        for email in name:
            groups[find(email)].append(email)
        return [[name[root]] + sorted(emails) for root, emails in groups.items()]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V+E) or O(mn) | O(V) or O(mn) |
| Better | O(V+E) or O(mn) | O(V) or O(mn) |
| Optimal | O(V+E) or O(mn) | O(V) or O(mn) |

## Edge Cases & Pitfalls
- Empty or singleton graphs/grids.
- Mark visited before repeated traversal creates cycles.
- Preserve required in-place behavior when the signature returns None.

## Related
- BFS
- DFS
- Union-Find / Topological Sort
