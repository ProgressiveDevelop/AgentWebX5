package com.just.x5;


import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 *
 */

public interface IWebListenerManager {
    IWebListenerManager setWebChromeClient(WebView webview, WebChromeClient webChromeClient);

    IWebListenerManager setWebViewClient(WebView webView, WebViewClient webViewClient);

    IWebListenerManager setDownLoader(WebView webView, DownloadListener downloadListener);
}
