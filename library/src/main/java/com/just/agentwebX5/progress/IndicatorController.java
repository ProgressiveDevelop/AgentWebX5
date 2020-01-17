package com.just.agentwebX5.progress;


import com.tencent.smtt.sdk.WebView;

/**
 * 进度控制接口
 */
public interface IndicatorController {
    void progress(WebView v, int newProgress);

    IBaseProgressSpec offerIndicator();
}
