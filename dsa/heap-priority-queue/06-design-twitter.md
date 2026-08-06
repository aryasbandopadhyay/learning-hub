# 06. Design Twitter

- **Difficulty:** Medium
- **Pattern:** Heap / Priority Queue
- **Asked at:** Amazon, Google, Meta, Twitter

## Problem
Design a simplified Twitter with `postTweet(userId, tweetId)`, `getNewsFeed(userId)`, `follow(followerId, followeeId)`, and `unfollow(followerId, followeeId)`. `getNewsFeed` returns up to 10 most recent tweet IDs posted by the user or users they follow. Constraints: at most `3 * 10^4` operations.

## Examples
```text
Input: ["Twitter","postTweet","getNewsFeed","follow","postTweet","getNewsFeed","unfollow","getNewsFeed"], [[],[1,5],[1],[1,2],[2,6],[1],[1,2],[1]]
Output: [null,null,[5],null,null,[6,5],null,[5]]
Explanation: User 1 sees their own tweets and followed user 2's tweets until unfollowing.
```

## Understanding & Intuition
Each tweet needs a timestamp so recency is comparable. The news feed is a top-10 merge across the user's own tweet list and followees' tweet lists. A heap can merge recent tweets without scanning every historical tweet.

## Approach 1 — Naive / Brute Force
**Idea:** Store all tweets globally and scan them from newest to oldest for visible authors.
```python
from collections import defaultdict
from typing import List

class Twitter:
    def __init__(self):
        self.time = 0
        self.tweets = []  # (time, userId, tweetId)
        self.follows = defaultdict(set)

    def postTweet(self, userId: int, tweetId: int) -> None:
        self.time += 1
        self.tweets.append((self.time, userId, tweetId))

    def getNewsFeed(self, userId: int) -> List[int]:
        visible = self.follows[userId] | {userId}
        feed = []
        for _, author, tweet_id in reversed(self.tweets):
            if author in visible:
                feed.append(tweet_id)
                if len(feed) == 10:
                    break
        return feed

    def follow(self, followerId: int, followeeId: int) -> None:
        if followerId != followeeId:
            self.follows[followerId].add(followeeId)

    def unfollow(self, followerId: int, followeeId: int) -> None:
        self.follows[followerId].discard(followeeId)
```
- **Time:** O(T) per feed — **Space:** O(T + F)

## Approach 2 — Better
**Idea:** Collect all visible users' tweets, sort them by timestamp, and return the newest 10.
```python
from collections import defaultdict
from typing import List

class Twitter:
    def __init__(self):
        self.time = 0
        self.user_tweets = defaultdict(list)  # userId -> [(time, tweetId)]
        self.follows = defaultdict(set)

    def postTweet(self, userId: int, tweetId: int) -> None:
        self.time += 1
        self.user_tweets[userId].append((self.time, tweetId))

    def getNewsFeed(self, userId: int) -> List[int]:
        candidates = []
        for user in self.follows[userId] | {userId}:
            candidates.extend(self.user_tweets[user])
        candidates.sort(reverse=True)
        return [tweet_id for _, tweet_id in candidates[:10]]

    def follow(self, followerId: int, followeeId: int) -> None:
        if followerId != followeeId:
            self.follows[followerId].add(followeeId)

    def unfollow(self, followerId: int, followeeId: int) -> None:
        self.follows[followerId].discard(followeeId)
```
- **Time:** O(m log m) per feed — **Space:** O(T + F)

## Approach 3 — Optimal
**Idea:** Store tweets per user and use a max-heap to k-way merge only the newest candidates.
```python
from collections import defaultdict
from typing import List
import heapq

class Twitter:
    def __init__(self):
        self.time = 0
        self.user_tweets = defaultdict(list)  # userId -> [(time, tweetId)]
        self.follows = defaultdict(set)

    def postTweet(self, userId: int, tweetId: int) -> None:
        self.time += 1
        self.user_tweets[userId].append((self.time, tweetId))

    def getNewsFeed(self, userId: int) -> List[int]:
        heap = []
        for user in self.follows[userId] | {userId}:
            tweets = self.user_tweets[user]
            if tweets:
                index = len(tweets) - 1
                time, tweet_id = tweets[index]
                # Negative time makes heapq behave like a max-heap.
                heapq.heappush(heap, (-time, tweet_id, user, index - 1))

        feed = []
        while heap and len(feed) < 10:
            neg_time, tweet_id, user, next_index = heapq.heappop(heap)
            feed.append(tweet_id)
            if next_index >= 0:
                time, next_tweet = self.user_tweets[user][next_index]
                heapq.heappush(heap, (-time, next_tweet, user, next_index - 1))
        return feed

    def follow(self, followerId: int, followeeId: int) -> None:
        if followerId != followeeId:
            self.follows[followerId].add(followeeId)

    def unfollow(self, followerId: int, followeeId: int) -> None:
        self.follows[followerId].discard(followeeId)
```
- **Time:** O((f + 10) log f) per feed — **Space:** O(T + F + f)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(T) per feed | O(T + F) |
| Better | O(m log m) per feed | O(T + F) |
| Optimal | O((f + 10) log f) per feed | O(T + F + f) |

## Edge Cases & Pitfalls
- A user should always see their own tweets.
- Ignore self-follow to avoid duplicate feeds.
- `unfollow` should be safe even if the relationship does not exist.

## Related
- Merge k Sorted Lists
- Design In-Memory File System
- Find Median from Data Stream
