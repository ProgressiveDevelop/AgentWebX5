package com.just.x5.builder;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.just.x5.progress.BaseIndicatorView;

public class IndicatorBuilderForFragment {
    private AgentBuilderFragment agentBuilderFragment;

    public IndicatorBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
        this.agentBuilderFragment = agentBuilderFragment;
    }

    public CommonBuilderForFragment useDefaultIndicator(int color) {
        this.agentBuilderFragment.setEnableProgress(true);
        this.agentBuilderFragment.setmIndicatorColor(color);
        return new CommonBuilderForFragment(agentBuilderFragment);
    }

    public CommonBuilderForFragment useDefaultIndicator() {
        this.agentBuilderFragment.setEnableProgress(true);
        return new CommonBuilderForFragment(agentBuilderFragment);
    }

    public CommonBuilderForFragment closeDefaultIndicator() {
        this.agentBuilderFragment.setEnableProgress(false);
        this.agentBuilderFragment.setmIndicatorColor(-1);
        this.agentBuilderFragment.setHeight_dp(-1);
        return new CommonBuilderForFragment(agentBuilderFragment);
    }

    public CommonBuilderForFragment setCustomIndicator(@NonNull BaseIndicatorView indicatorView) {
        this.agentBuilderFragment.setEnableProgress(true);
        this.agentBuilderFragment.setIndicatorView(indicatorView);
        return new CommonBuilderForFragment(agentBuilderFragment);
    }

    public CommonBuilderForFragment setIndicatorColorWithHeight(@ColorInt int color, int height_dp) {
        this.agentBuilderFragment.setmIndicatorColor(color);
        this.agentBuilderFragment.setHeight_dp(height_dp);
        return new CommonBuilderForFragment(this.agentBuilderFragment);
    }
}
