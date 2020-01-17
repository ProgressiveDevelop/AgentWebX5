package com.just.agentwebX5.builder;

import androidx.annotation.Nullable;

import com.just.agentwebX5.AgentWebX5;

public class PreAgentWeb {
    private AgentWebX5 mAgentWebX5;
    private boolean isReady = false;

    PreAgentWeb(AgentWebX5 agentWebX5) {
        this.mAgentWebX5 = agentWebX5;
    }

    public PreAgentWeb ready() {
        if (!isReady) {
            mAgentWebX5.ready();
            isReady = true;
        }
        return this;
    }

    public AgentWebX5 go(@Nullable String url) {
        if (!isReady) {
            ready();
        }
        return mAgentWebX5.go(url);
    }
}
