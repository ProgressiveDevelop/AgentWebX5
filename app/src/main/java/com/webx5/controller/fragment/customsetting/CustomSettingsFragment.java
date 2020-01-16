package com.webx5.controller.fragment.customsetting;

import android.os.Bundle;

import com.just.agentwebX5.IWebSettings;
import com.webx5.controller.fragment.AgentWebX5Fragment;

/**
 * 自定义WebView配置演示
 */

public class CustomSettingsFragment extends AgentWebX5Fragment {

    public static AgentWebX5Fragment getInstance(Bundle bundle) {
        CustomSettingsFragment mCustomSettingsFragment = new CustomSettingsFragment();
        if (bundle != null)
            mCustomSettingsFragment.setArguments(bundle);
        return mCustomSettingsFragment;
    }


    @Override
    public IWebSettings getSettings() {
        return new CustomSettings();
    }
}
