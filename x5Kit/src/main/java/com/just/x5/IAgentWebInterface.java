package com.just.x5;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.WebView;

public interface IAgentWebInterface {
    boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result);

    void onReceivedTitle(WebView view, String title);

    void onProgressChanged(WebView view, int newProgress);
}
