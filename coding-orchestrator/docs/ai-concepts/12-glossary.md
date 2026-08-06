# 12 — Glossary

Quick definitions of the jargon used across this section. See the linked docs for depth.

| Term | Definition |
|---|---|
| **Agent** | An LLM-driven system that pursues a goal over multiple steps, using tools and reacting to results. (`01`) |
| **Agentic RAG** | An agent that iteratively retrieves and reasons until it can answer. (`06`) |
| **Alignment** | Training that makes a model prefer helpful, honest, safe responses (RLHF/DPO). (`03`) |
| **Attention (self-attention)** | Transformer mechanism weighing how much each token relates to others. (`02`) |
| **AWQ / GPTQ / GGUF** | Popular LLM quantization formats. (`04`) |
| **Base model** | A raw next-token predictor before instruction tuning. (`02`) |
| **Chain-of-thought (CoT)** | Prompting the model to reason step by step. (`05`) |
| **Chunking** | Splitting documents into passages for embedding/retrieval. (`06`) |
| **Compile (graph)** | Turning a `StateGraph` definition into an executable `CompiledGraph`. (`07`) |
| **Context window** | Max tokens (prompt + output) a model can consider at once. (`02`) |
| **Conditional edge** | A graph edge whose target is chosen by a function reading state. (`07`) |
| **DPO** | Direct Preference Optimization — RL-free alignment on preference pairs. (`03`) |
| **Embedding** | A vector capturing the meaning of text; nearby vectors ≈ similar meaning. (`06`) |
| **Few-shot** | Including examples in the prompt so the model imitates the pattern. (`05`) |
| **Fine-tuning** | Further training to change a model's behavior/skills. (`03`) |
| **Function calling / tool call** | The model emitting a structured request to invoke a tool. (`09`) |
| **Guardrails** | Constraints on inputs, outputs, and actions to keep an agent safe. (`11`) |
| **Hallucination** | Plausible but incorrect model output. (`02`) |
| **Human-in-the-loop** | Pausing an agent for human review/approval. (`10`,`11`) |
| **Instruct/chat model** | A model tuned to follow instructions and hold a dialogue. (`02`) |
| **Knowledge cutoff** | The date after which a model has no training knowledge. (`02`) |
| **LangGraph / LangGraph4j** | Framework for building agents as state graphs (JVM port). (`07`) |
| **LLM-as-judge** | Using a model to score outputs against a rubric. (`11`) |
| **LoRA / QLoRA** | Parameter-efficient fine-tuning via small adapters (on a quantized base). (`03`) |
| **Master–worker** | A coordinator delegating steps to specialized workers. (`10`) |
| **MCP** | Model Context Protocol — a standard interface between AI apps and capability servers. (`08`) |
| **Node (graph)** | A function `state -> state update` in a LangGraph graph. (`07`) |
| **PEFT** | Parameter-Efficient Fine-Tuning (e.g. LoRA). (`03`) |
| **Pre-training** | Large-scale self-supervised next-token training producing a base model. (`03`) |
| **Prompt injection** | Malicious input that tries to override an agent's instructions. (`11`) |
| **Quantization** | Storing weights in fewer bits (int8/int4) to save memory/compute. (`04`) |
| **RAG** | Retrieval-Augmented Generation — fetch relevant docs into the prompt. (`06`) |
| **ReAct** | Agent pattern alternating reasoning ("thought") and tool use ("action"). (`01`) |
| **Reducer (state)** | Rule for merging a node's update into shared graph state. (`07`) |
| **Resource (MCP)** | Readable, app/user-selected context (file/record) with no side effects. (`08`,`09`) |
| **RLHF** | Reinforcement Learning from Human Feedback for alignment. (`03`) |
| **Sampling (temperature/top-p/top-k)** | Parameters controlling generation randomness. (`02`) |
| **Sandbox** | Isolated environment for running untrusted/generated code. (`11`) |
| **Skill** | A packaged procedure/playbook teaching an agent how to do a task. (`09`) |
| **SFT** | Supervised Fine-Tuning on instruction/response pairs. (`03`) |
| **State (graph)** | The shared object passed between graph nodes. (`07`) |
| **System prompt** | Durable instructions/persona set for the whole conversation. (`05`) |
| **Token** | A sub-word unit; models read/write tokens, and cost is per token. (`02`) |
| **Tool** | A callable function (with a schema) the model can invoke to act. (`09`) |
| **Transformer** | The neural architecture underlying modern LLMs. (`02`) |
| **Vector store** | A database for embeddings supporting similarity search. (`06`) |

Parenthetical numbers point to the doc in this `ai-concepts/` folder with the full explanation.
