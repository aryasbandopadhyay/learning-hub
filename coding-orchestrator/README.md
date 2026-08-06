# Coding Orchestrator (LangGraph4j · Java · MCP)

A **deterministic master–worker** coding orchestrator built on
[LangGraph4j](https://github.com/langgraph4j/langgraph4j). A master state-machine delegates each
step of a coding task to a specialized worker agent, drives review loops until convergence, and is
exposed — together with every individual agent — as an **MCP server** over stdio.

> 📖 **Deep dive:** see [`docs/ORCHESTRATOR_FLOW.md`](docs/ORCHESTRATOR_FLOW.md) for the full flow,
> tool-call mechanics, and LangGraph4j syntax — with Mermaid diagrams.

## Why this design

| Requirement | How it's met |
|---|---|
| Master–worker, deterministic | The master is a compiled `StateGraph` with fixed edges and capped loops. All routing is on explicit control signals (approval flags + iteration counters) — never on an LLM decision. |
| Separate agent per step | `design`, `scrutinize_design`, `implement`, `scrutinize_code`, `write_tests`. |
| Scrutiny with SOLID + patterns, loop to harmony | Review agents emit `VERDICT: APPROVED / REVISE`; the master loops design↔review and code↔review until approval or a policy cap. |
| Different model for scrutiny (bias elimination) | Implementation agents use **OpenAI**; scrutiny agents use **Anthropic**. |
| Deterministic scripts with tools | `FileSystemTool`, `GitTool`, `TestRunnerTool` — plain, side-effecting Java, no LLM logic. |
| Each agent has an MCP + orchestrator is an MCP | `OrchestratorMcpServer` (stdio) exposes `orchestrate_task` **and** each agent as its own MCP tool. |
| Front-loaded approvals (minimize human touch) | `ApprovalPolicy` is resolved once at startup; no mid-run prompts. |

## Pipeline

```
START -> design -> scrutinize_design --approved--> implement -> scrutinize_code --approved--> write_tests -> END
             ^          |                              ^            |
             +--revise--+                              +--revise----+
```

`write_tests` materializes the design, code and generated unit tests into the workspace via
`FileSystemTool` and makes a **local** git commit (`GitTool`). Pushing to a remote is gated by
`ApprovalPolicy.autoApproveGitPush` and is disabled by default (out of scope for now).

## Architecture

```
com.orchestrator
├── OrchestratorApp            # entrypoint: `mcp` (default) | `run "<task>"`
├── config/OrchestratorConfig  # env-resolved config (keys, models, workspace)
├── approval/ApprovalPolicy    # front-loaded approvals + loop caps
├── model/ModelFactory         # builds OpenAI (impl) + Anthropic (scrutiny) models -> AgentTeam
├── agents/                    # Agent, LlmAgent, AgentTeam, Prompts, ReviewVerdict
├── tools/                     # Tool, FileSystemTool, GitTool (deterministic scripts)
├── graph/                     # OrchestratorState, OrchestratorGraph (MASTER), Orchestrator, ArtifactWriter
└── mcp/OrchestratorMcpServer  # stdio MCP surface (orchestrator + per-agent tools)
```

## Requirements

- Java 17+
- Maven 3.6+
- `OPENAI_API_KEY` and `ANTHROPIC_API_KEY` for live runs (see `.env.example`)

## Build & test

```bash
mvn test          # 17 unit tests, fully offline (fake agents, no live LLM calls)
mvn package       # builds target/coding-orchestrator.jar (runnable fat-jar)
```

## Run the pipeline once

```bash
export OPENAI_API_KEY=sk-...
export ANTHROPIC_API_KEY=sk-ant-...
java -jar target/coding-orchestrator.jar run "Design and implement an LRU cache in Java"
```

Artifacts (design, code, tests) are written under `ORCHESTRATOR_WORKSPACE`
(default: `<tmp>/orchestrator-workspace`) and committed locally.

## Run as an MCP server

```bash
java -jar target/coding-orchestrator.jar mcp
```

The server speaks MCP over stdio and exposes these tools:

- `orchestrate_task` — run the full pipeline for a task.
- `design`, `scrutinize_design`, `implement`, `scrutinize_code`, `write_tests` — invoke a single agent.

Example client registration (e.g. Claude Desktop `mcpServers` config):

```json
{
  "mcpServers": {
    "coding-orchestrator": {
      "command": "java",
      "args": ["-jar", "/absolute/path/target/coding-orchestrator.jar", "mcp"],
      "env": {
        "OPENAI_API_KEY": "sk-...",
        "ANTHROPIC_API_KEY": "sk-ant-..."
      }
    }
  }
}
```

## Configuration (environment variables)

| Variable | Default | Purpose |
|---|---|---|
| `OPENAI_API_KEY` | — | OpenAI key (implementation agents) |
| `OPENAI_MODEL` | `gpt-4o` | OpenAI model name |
| `ANTHROPIC_API_KEY` | — | Anthropic key (scrutiny agents) |
| `ANTHROPIC_MODEL` | `claude-3-5-sonnet-20241022` | Anthropic model name |
| `ORCHESTRATOR_WORKSPACE` | `<tmp>/orchestrator-workspace` | Output sandbox |
| `APPROVE_FILE_WRITES` | `true` | Pre-approve file writes |
| `APPROVE_GIT_COMMIT` | `true` | Pre-approve local commits |
| `APPROVE_GIT_PUSH` | `false` | Pre-approve remote push (kept off) |
| `APPROVE_MODEL_CALLS` | `true` | Pre-approve model calls |
| `MAX_DESIGN_ITERATIONS` | `3` | Design review loop cap |
| `MAX_CODE_ITERATIONS` | `4` | Code review loop cap |

## Notes & scope

- **GitHub push** is intentionally not wired yet; tests are generated and committed locally. Flip
  `APPROVE_GIT_PUSH=true` and add a `push` step to enable it.
- Tests never make network calls — the master graph and MCP surface are exercised with deterministic
  fake agents, so `mvn test` is hermetic.
