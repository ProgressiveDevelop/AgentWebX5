package com.just.x5.builder;

import androidx.annotation.ColorInt;

public class IndicatorBuilder {
    private AgentBuilder mAgentBuilder;

    public IndicatorBuilder(AgentBuilder builder) {
        this.mAgentBuilder = builder;
    }

    public CommonAgentBuilder setIndicatorColor(int color) {
        mAgentBuilder.setIndicatorColor(color);
        return new CommonAgentBuilder(mAgentBuilder);
    }

    public CommonAgentBuilder defaultProgressBarColor() {
        mAgentBuilder.setIndicatorColor(-1);
        return new CommonAgentBuilder(mAgentBuilder);
    }

    public CommonAgentBuilder setIndicatorColorWithHeight(@ColorInt int color, int height_dp) {
        mAgentBuilder.setIndicatorColor(color);
        mAgentBuilder.setIndicatorColorWithHeight(height_dp);
        return new CommonAgentBuilder(mAgentBuilder);
    }
}
