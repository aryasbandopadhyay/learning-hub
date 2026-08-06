# System Design — High-Level Design (HLD) Series

A portfolio of **HLD interview write-ups**, authored as an SDE2 would present them in a
45–60 minute system-design round. Each document is self-contained and follows the same
structure so they are easy to compare and revise.

> Companion to the LLD machine-coding series in the sibling directories. Where the LLD
> series focuses on classes, patterns, and concurrency **inside one process**, this series
> focuses on **distributed architecture**: data partitioning, replication, caching, queues,
> consistency trade-offs, and back-of-the-envelope scale.

## How to read these

Every doc follows the same 12-section flow — the way a strong candidate drives an interview:

1. **Problem statement & scope** — one-paragraph framing.
2. **Functional requirements** — what the system must do (bulleted, prioritized).
3. **Non-functional requirements** — scale, latency, availability, consistency, durability.
4. **Back-of-the-envelope estimation** — DAU, QPS (avg & peak), read:write ratio, storage
   growth/yr, bandwidth, cache sizing, number of servers. Show the arithmetic.
5. **API design** — the public contract (REST/gRPC/WebSocket), request/response shapes,
   idempotency, pagination.
6. **Data model & schema** — tables/collections with keys, indexes, and the chosen
   **storage engine(s)** and why (SQL vs NoSQL vs KV vs blob vs search vs time-series).
7. **High-level architecture** — a Mermaid diagram of the components and data flow.
8. **Deep dives** — 3–5 of the hardest components, each with a diagram or sequence where
   useful (e.g. ID generation, fan-out, partitioning, consensus, dedup, exactly-once).
9. **Scaling, caching & bottlenecks** — sharding strategy, cache layers + invalidation,
   hot-key/celebrity problem, read/write path scaling, CDN, connection limits.
10. **Reliability** — replication, failover, backpressure, idempotency, retries, DLQ,
    multi-region/DR, consistency model (and where we pick AP vs CP).
11. **Trade-offs & alternatives** — an explicit table of decisions considered and rejected.
12. **Future improvements** — what you'd add with more time.

### Conventions used in the estimates
- 1 day ≈ 86,400 s ≈ **10^5 s** (rounding used throughout for quick math).
- "Peak" ≈ **2–3× average** QPS unless a burstier pattern is justified.
- Storage headers: assume replication factor **3** for durability unless noted.
- Latency budget stated as p50 / p99 where it matters.

## Index

| # | System | Core themes |
|---|--------|-------------|
| 1 | [URL Shortener](./url-shortener.md) | ID generation, KV store, read-heavy, cache, CDN |
| 2 | [Rate Limiter](./rate-limiter.md) | Token bucket, Redis, distributed counters, sliding window |
| 3 | [Notification Service](./notification-service.md) | Fan-out, queues, provider abstraction, retries/DLQ |
| 4 | [API Gateway](./api-gateway.md) | Routing, auth, rate limiting, aggregation, resiliency |
| 5 | [Distributed Cache](./distributed-cache.md) | Consistent hashing, replication, eviction, cache coherence |
| 6 | [File Storage (Dropbox/Drive)](./file-storage.md) | Chunking, dedup, metadata vs blob, sync, blob store |
| 7 | [Chat / Messaging System](./chat-messaging-system.md) | WebSockets, presence, fan-out, ordering, delivery guarantees |
| 8 | [News Feed](./news-feed.md) | Fan-out on write vs read, ranking, celebrity problem |
| 9 | [Logging & Monitoring Platform](./logging-monitoring-platform.md) | Ingestion pipeline, time-series, aggregation, alerting |
| 10 | [Search Autocomplete](./search-autocomplete.md) | Trie, top-k, prefix sharding, latency, freshness |
| 11 | [Payment Gateway](./payment-gateway.md) | Idempotency, ledger, 2-phase, PCI, reconciliation |
| 12 | [Digital Wallet](./digital-wallet.md) | Double-entry ledger, ACID, consistency, idempotent txns |
| 13 | [Order Management](./order-management.md) | Sagas, inventory, state machine, event-driven |
| 14 | [Distributed Job Scheduler](./distributed-job-scheduler.md) | Time-wheel/queues, leader election, exactly-once, at-least-once |
| 15 | [Workflow Engine](./workflow-engine.md) | DAG orchestration, durable state, retries, event sourcing |
| 16 | [Distributed Lock Service](./distributed-lock-service.md) | Consensus (Raft/Paxos), leases, fencing tokens, ZooKeeper/etcd |
| 17 | [Kafka-like Messaging System](./kafka-like-messaging-system.md) | Partitioned log, replication (ISR), consumer groups, offsets |
| 18 | [CI/CD Deployment Platform](./cicd-deployment-platform.md) | Pipeline DAG, runners, artifacts, blue-green/canary |
| 19 | [Kubernetes Scheduler](./kubernetes-scheduler.md) | Bin packing, predicates/priorities, control loop, preemption |
| 20 | [LLM Inference / RAG Platform](./llm-inference-rag-platform.md) | Vector DB, embeddings, GPU batching, KV cache, retrieval |

---

*Diagrams are authored in [Mermaid](https://mermaid.js.org/) so they render inline on GitHub.*
