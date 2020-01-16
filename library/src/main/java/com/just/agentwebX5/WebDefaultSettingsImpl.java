package com.just.agentwebX5;


import android.annotation.SuppressLint;
import android.os.Build;

import com.just.agentwebX5.helpClass.AgentWebX5Config;
import com.just.agentwebX5.util.AgentWebX5Utils;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 默认WebView设置配置
 */
public class WebDefaultSettingsImpl implements IWebSettings, IWebListenerManager {
    private com.tencent.smtt.sdk.WebSettings mWebSettings;

    public static WebDefaultSettingsImpl getInstance() {
        return new WebDefaultSettingsImpl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public IWebSettings toSetting(WebView webView) {
        this.mWebSettings = webView.getSettings();
        mWebSettings.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_NO_CACHE);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebX5Utils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebSettings.setRenderPriority(com.tencent.smtt.sdk.WebSettings.RenderPriority.HIGH);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        mWebSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);
        //
        String dir = AgentWebX5Config.getCachePath(webView.getContext());
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);
        //适配5.0不允许http和https混合使用情况
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        return this;
    }

    @Override
    public com.tencent.smtt.sdk.WebSettings getWebSettings() {
        return mWebSettings;
    }

    @Override
    public IWebListenerManager setWebChromeClient(WebView webview, WebChromeClient webChromeClient) {
        webview.setWebChromeClient(webChromeClient);
        return this;
    }

    @Override
    public IWebListenerManager setWebViewClient(WebView webView, WebViewClient webViewClient) {
        webView.setWebViewClient(webViewClient);
        return this;
    }

    @Override
    public IWebListenerManager setDownLoader(WebView webView, DownloadListener downloadListener) {
        webView.setDownloadListener(downloadListener);
        return this;
    }
}
