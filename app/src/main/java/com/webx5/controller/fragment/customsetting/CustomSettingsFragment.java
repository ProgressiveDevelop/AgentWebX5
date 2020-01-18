package com.webx5.controller.fragment.customsetting;

import com.just.x5.IWebSettings;
import com.webx5.controller.fragment.AgentWebX5Fragment;

/**
 * 自定义WebView配置演示
 */

public class CustomSettingsFragment extends AgentWebX5Fragment {


    @Override
    public IWebSettings getSettings() {
        return new CustomSettings();
    }
}
