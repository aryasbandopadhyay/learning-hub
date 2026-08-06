package com.orchestrator.model;

import com.orchestrator.agents.AgentTeam;
import com.orchestrator.agents.LlmAgent;
import com.orchestrator.agents.Prompts;
import com.orchestrator.approval.ApprovalPolicy;
import com.orchestrator.config.OrchestratorConfig;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

/**
 * Builds the two provider-distinct chat models and wires them into the worker team.
 *
 * <p>Bias elimination: implementation-side workers use OpenAI, scrutiny-side workers use Anthropic.
 */
public final class ModelFactory {

    private ModelFactory() {
    }

    public static ChatModel openAi(OrchestratorConfig cfg) {
        return OpenAiChatModel.builder()
                .apiKey(cfg.openAiApiKey())
                .modelName(cfg.openAiModel())
                .temperature(0.2)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    public static ChatModel anthropic(OrchestratorConfig cfg) {
        return AnthropicChatModel.builder()
                .apiKey(cfg.anthropicApiKey())
                .modelName(cfg.anthropicModel())
                .temperature(0.0)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    /** Assembles the live worker team from real provider models. */
    public static AgentTeam liveTeam(OrchestratorConfig cfg, ApprovalPolicy policy) {
        ChatModel impl = openAi(cfg);       // design / code / tests
        ChatModel review = anthropic(cfg);  // scrutiny (different provider)

        return new AgentTeam(
                new LlmAgent("design", "Produces the software design", Prompts.DESIGNER, impl, policy),
                new LlmAgent("scrutinize_design", "Reviews design vs SOLID & patterns",
                        Prompts.DESIGN_REVIEWER, review, policy),
                new LlmAgent("implement", "Implements the approved design", Prompts.IMPLEMENTER, impl, policy),
                new LlmAgent("scrutinize_code", "Reviews code vs SOLID & patterns",
                        Prompts.CODE_REVIEWER, review, policy),
                new LlmAgent("write_tests", "Writes unit tests", Prompts.TESTER, impl, policy));
    }
}
