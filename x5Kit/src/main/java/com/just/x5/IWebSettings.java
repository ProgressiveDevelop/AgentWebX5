package com.just.x5;


import com.tencent.smtt.sdk.WebView;

public interface IWebSettings<T extends com.tencent.smtt.sdk.WebSettings> {

    IWebSettings toSetting(WebView webView);

    T getWebSettings();
}
