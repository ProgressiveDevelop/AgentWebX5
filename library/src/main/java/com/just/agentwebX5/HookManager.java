package com.just.agentwebX5;

import com.just.agentwebX5.builder.AgentBuilder;
import com.just.agentwebX5.builder.AgentBuilderFragment;

public class HookManager {

    public static AgentWebX5 hookAgentWeb(AgentWebX5 agentWebX5, AgentBuilder agentBuilder) {
        return agentWebX5;
    }

    public static AgentWebX5 hookAgentWeb(AgentWebX5 agentWebX5, AgentBuilderFragment agentBuilder) {
        return agentWebX5;
    }

}
