package com.just.agentwebX5.builder;

import com.just.agentwebX5.BaseIndicatorView;

public class ConfigIndicatorBuilder {
    private AgentBuilder mAgentBuilder;

    public ConfigIndicatorBuilder(AgentBuilder agentBuilder) {
        this.mAgentBuilder = agentBuilder;
    }

    public IndicatorBuilder useDefaultIndicator() {
        this.mAgentBuilder.setNeedProgress(true);
        mAgentBuilder.enableProgress();
        return new IndicatorBuilder(mAgentBuilder);
    }

    public CommonAgentBuilder customProgress(BaseIndicatorView view) {
        this.mAgentBuilder.setIndicatorView(view);
        this.mAgentBuilder.setNeedProgress(false);
        return new CommonAgentBuilder(mAgentBuilder);
    }

    public CommonAgentBuilder closeProgressBar() {
        mAgentBuilder.closeProgress();
        return new CommonAgentBuilder(mAgentBuilder);
    }
}
