# 10. Subdomain Visit Count

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Amazon, Google, Yelp

## Problem
A count-paired domain like `"9001 discuss.leetcode.com"` means the domain was visited 9001 times. Every visit to a domain also counts as a visit to each parent subdomain. Given `cpdomains`, return all subdomain visit counts as strings of the form `"count domain"`.

Return the result sorted lexicographically by domain for deterministic output.

**Input**
- `cpdomains`: a `list[str]`; the count-paired domains.

**Output**
- A `list[str]`. Return all subdomain visit counts as strings of the form `"count domain"`. Return the result sorted lexicographically by domain for deterministic output. This judge compares the sequence exactly: return entries sorted lexicographically by domain string.

## Constraints
- `1 <= len(cpdomains) <= 10^4`.
- counts are positive integers.
- domains contain lowercase letters and dots.

## Examples
```text
Input: cpdomains = ["9001 discuss.leetcode.com"]
Output: ["9001 com", "9001 discuss.leetcode.com", "9001 leetcode.com"]
Explanation: A visit to discuss.leetcode.com also visits leetcode.com and com. The output is written in the required deterministic order.
```

## Understanding & Intuition
Each domain contributes its count to every suffix starting after a dot, plus the whole domain. Hashmap aggregation handles repeated subdomains across different input entries. Sorting by domain gives a stable return order.

## Approach 1 — Naive / Brute Force
**Idea:** Generate all suffixes into a list, then repeatedly scan the list to sum each unseen domain.
```python
class Solution:
    def subdomainVisits(self, cpdomains: list[str]) -> list[str]:
        expanded = []
        for item in cpdomains:
            count_text, domain = item.split()
            count = int(count_text)
            parts = domain.split('.')
            for i in range(len(parts)):
                expanded.append((count, '.'.join(parts[i:])))
        domains = sorted({domain for count, domain in expanded})
        ans = []
        for domain in domains:
            total = 0
            for count, cur in expanded:
                if cur == domain:
                    total += count
            ans.append(str(total) + " " + domain)
        return ans
```
- **Time:** O(m² + m log m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Sort the expanded `(domain, count)` pairs and sum consecutive equal domains.
```python
class Solution:
    def subdomainVisits(self, cpdomains):
        pairs = []
        for item in cpdomains:
            count_text, domain = item.split()
            count = int(count_text)
            pairs.append((domain, count))
            for i, ch in enumerate(domain):
                if ch == '.':
                    pairs.append((domain[i + 1:], count))
        pairs.sort()
        ans = []
        i = 0
        while i < len(pairs):
            domain = pairs[i][0]
            total = 0
            while i < len(pairs) and pairs[i][0] == domain:
                total += pairs[i][1]
                i += 1
            ans.append(str(total) + " " + domain)
        return ans
```
- **Time:** O(m log m) — **Space:** O(m)

## Approach 3 — Optimal
**Idea:** Add each suffix count directly into a hashmap, then emit sorted domains.
```python
class Solution:
    def subdomainVisits(self, cpdomains):
        counts = {}
        for item in cpdomains:
            count_text, domain = item.split()
            count = int(count_text)
            counts[domain] = counts.get(domain, 0) + count
            for i, ch in enumerate(domain):
                if ch == '.':
                    sub = domain[i + 1:]
                    counts[sub] = counts.get(sub, 0) + count
        return [str(counts[domain]) + " " + domain for domain in sorted(counts)]
```
- **Time:** O(total characters + m log m) — **Space:** O(m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m² + m log m) | O(m) |
| Better | O(m log m) | O(m) |
| Optimal | O(total characters + m log m) | O(m) |

## Edge Cases & Pitfalls
- Counts for the same subdomain can arrive from many different full domains.
- Preserve the exact `"count domain"` output format.
- Sort by domain, not by count, for the canonical result.

## Related
- Group Anagrams
- Top K Frequent Words

