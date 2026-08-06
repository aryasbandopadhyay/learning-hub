package com.orchestrator.agents;

import com.orchestrator.approval.ApprovalPolicy;
import dev.langchain4j.model.chat.ChatModel;

/**
 * Base class for LLM-backed agents. Each agent carries a fixed <em>role/system</em> prompt that
 * encodes its specialization; {@link #act} prepends that role to the caller-supplied context and
 * issues a single deterministic model call (guarded by the front-loaded {@link ApprovalPolicy}).
 */
public class LlmAgent implements Agent {

    private final String name;
    private final String description;
    private final String rolePrompt;
    private final ChatModel model;
    private final ApprovalPolicy policy;

    public LlmAgent(String name, String description, String rolePrompt,
                    ChatModel model, ApprovalPolicy policy) {
        this.name = name;
        this.description = description;
        this.rolePrompt = rolePrompt;
        this.model = model;
        this.policy = policy;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String act(String input) {
        if (!policy.autoApproveModelCalls()) {
            throw new IllegalStateException("model calls not approved by ApprovalPolicy");
        }
        String prompt = rolePrompt + "\n\n=== TASK CONTEXT ===\n" + input;
        return model.chat(prompt);
    }
}
