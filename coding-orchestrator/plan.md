# Coding Orchestrator (LangGraph4j, Java) — Plan

## Goal
A deterministic **master–worker** coding orchestrator built on LangGraph4j. The master is a
compiled `StateGraph` (deterministic edges + capped loops). Each worker is an LLM agent with
tools. The whole orchestrator is exposed as an **MCP server** over stdio, and each agent is also
individually exposed as an MCP tool.

## Bias elimination
- Implementation-side agents (design, code, tests) use **OpenAI**.
- Scrutiny agents (design review, code review) use **Anthropic** — a different provider to reduce
  self-review bias.

## Pipeline (master delegates to workers)
1. **design** — DesignAgent (OpenAI) produces a design.
2. **scrutinize_design** — DesignReviewAgent (Anthropic) checks SOLID + design patterns → verdict.
   Loop 1<->2 until APPROVED or `maxDesignIterations`.
3. **code** — ImplementationAgent (OpenAI) writes the solution.
4. **scrutinize_code** — CodeReviewAgent (Anthropic) checks patterns/principles → verdict.
   Loop 3<->4 until "harmony" (APPROVED) or `maxCodeIterations`.
5. **tests** — TestAgent (OpenAI) writes unit tests, writes files via FileSystemTool + GitTool
   (local commit; push intentionally skipped per current scope).

## Deterministic tools (scripts)
- `FileSystemTool` — write/read files under a sandbox workspace.
- `GitTool` — init/add/commit locally (push gated + skipped for now).
- `TestRunnerTool` — run `mvn test` (or a no-op stub when unavailable).

## Front-loaded approvals (minimize human touch)
- `ApprovalPolicy` loaded once at startup (env/config): autoApproveFileWrites, autoApproveGitCommit,
  autoApproveModelCalls, maxDesignIterations, maxCodeIterations. No mid-run interactive prompts.

## MCP surface
- `OrchestratorMcpServer` (stdio) exposes tools:
  - `orchestrate_task` (full pipeline)
  - `design`, `scrutinize_design`, `implement`, `scrutinize_code`, `write_tests` (each agent as MCP tool)

## Build / stack
- Java 17, Maven.
- langgraph4j-core 1.8.22
- langchain4j (openai + anthropic) 1.x via BOM
- io.modelcontextprotocol.sdk:mcp (stdio)

## Status
- [x] Research versions + APIs
- [x] pom + skeleton compiles
- [x] agents + graph
- [x] tools + approvals
- [x] MCP server (stdio; verified initialize + tools/list -> 6 tools)
- [x] tests (offline, no live LLM calls)
- [x] mvn test green (17/17)
- [x] fat-jar packaged + MCP stdio smoke-tested
