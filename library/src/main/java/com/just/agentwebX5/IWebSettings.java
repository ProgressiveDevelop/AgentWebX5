package com.just.agentwebX5;


import com.tencent.smtt.sdk.WebView;

public interface IWebSettings<T extends com.tencent.smtt.sdk.WebSettings> {

    IWebSettings toSetting(WebView webView);

    T getWebSettings();
}
