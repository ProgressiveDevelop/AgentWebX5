package com.just.x5.builder;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.x5.DefaultWebClient;
import com.just.x5.IEventHandler;
import com.just.x5.IReceivedTitleCallback;
import com.just.x5.IWebCreator;
import com.just.x5.IWebLayout;
import com.just.x5.IWebSettings;
import com.just.x5.MiddleWareWebChromeBase;
import com.just.x5.MiddleWareWebClientBase;
import com.just.x5.SecurityType;
import com.just.x5.downFile.DownLoadResultListener;
import com.just.x5.permission.IPermissionInterceptor;
import com.just.x5.progress.IndicatorController;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;

public class CommonAgentBuilder {
    private AgentBuilder mAgentBuilder;

    public CommonAgentBuilder(AgentBuilder agentBuilder) {
        this.mAgentBuilder = agentBuilder;
    }

    public CommonAgentBuilder openParallelDownload() {
        this.mAgentBuilder.setParallelDownload(true);
        return this;
    }

    public CommonAgentBuilder setNotifyIcon(@DrawableRes int icon) {
        this.mAgentBuilder.setIcon(icon);
        return this;
    }

    public CommonAgentBuilder setWebViewClient(@Nullable WebViewClient webViewClient) {
        this.mAgentBuilder.setmWebViewClient(webViewClient);
        return this;
    }

    public CommonAgentBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
        this.mAgentBuilder.setmWebChromeClient(webChromeClient);
        return this;
    }

    public CommonAgentBuilder useMiddleWareWebClient(@NonNull MiddleWareWebClientBase middleWrareWebClientBase) {
        if (this.mAgentBuilder.getHeader() == null) {
            this.mAgentBuilder.setHeader(middleWrareWebClientBase);
        } else {
            this.mAgentBuilder.getTail().enq(middleWrareWebClientBase);
        }
        this.mAgentBuilder.setTail(middleWrareWebClientBase);
        return this;
    }

    public CommonAgentBuilder setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
        this.mAgentBuilder.setOpenOtherPage(openOtherPageWays);
        return this;
    }

    public CommonAgentBuilder interceptUnkownScheme() {
        this.mAgentBuilder.setInterceptUnkownScheme(true);
        return this;
    }

    public CommonAgentBuilder useMiddleWareWebChrome(@NonNull MiddleWareWebChromeBase middleWareWebChromeBase) {
        if (this.mAgentBuilder.getmChromeMiddleWareHeader() == null) {
            this.mAgentBuilder.setmChromeMiddleWareHeader(middleWareWebChromeBase);
        } else {
            this.mAgentBuilder.getmChromeMiddleWareTail().enq(middleWareWebChromeBase);
        }
        this.mAgentBuilder.setmChromeMiddleWareTail(middleWareWebChromeBase);
        return this;
    }

    public CommonAgentBuilder setEventHandler(@Nullable IEventHandler iEventHandler) {
        this.mAgentBuilder.setmIEventHandler(iEventHandler);
        return this;
    }

    public CommonAgentBuilder setWebSettings(IWebSettings webSettings) {
        this.mAgentBuilder.setmWebSettings(webSettings);
        return this;
    }


    public CommonAgentBuilder(@Nullable IndicatorController indicatorController) {
        this.mAgentBuilder.setmIndicatorController(indicatorController);
    }


    public CommonAgentBuilder addJavascriptInterface(String name, Object o) {
        mAgentBuilder.addJavaObject(name, o);
        return this;
    }

    public CommonAgentBuilder setWebCreator(@Nullable IWebCreator webCreator) {
        this.mAgentBuilder.setmWebCreator(webCreator);
        return this;
    }

    public CommonAgentBuilder setReceivedTitleCallback(@Nullable IReceivedTitleCallback receivedTitleCallback) {
        this.mAgentBuilder.setReceivedTitleCallback(receivedTitleCallback);
        return this;
    }

    public CommonAgentBuilder setSecutityType(@Nullable SecurityType secutityType) {
        this.mAgentBuilder.setmSecurityType(secutityType);
        return this;
    }

    public CommonAgentBuilder setWebView(@Nullable WebView webView) {
        this.mAgentBuilder.setmWebView(webView);
        return this;
    }

    public CommonAgentBuilder setWebLayout(@NonNull IWebLayout webLayout) {
        this.mAgentBuilder.setmWebLayout(webLayout);
        return this;
    }

    public CommonAgentBuilder additionalHttpHeader(String k, String v) {
        this.mAgentBuilder.addHeader(k, v);
        return this;
    }

    public CommonAgentBuilder closeWebViewClientHelper() {
        mAgentBuilder.setWebclientHelper(false);
        return this;
    }

    public CommonAgentBuilder setPermissionInterceptor(IPermissionInterceptor permissionInterceptor) {
        this.mAgentBuilder.setmPermissionInterceptor(permissionInterceptor);
        return this;
    }

    public CommonAgentBuilder addDownLoadResultListener(DownLoadResultListener downLoadResultListener) {
        if (this.mAgentBuilder.getmDownLoadResultListeners() == null) {
            this.mAgentBuilder.setmDownLoadResultListeners(new ArrayList<DownLoadResultListener>());
        }
        this.mAgentBuilder.getmDownLoadResultListeners().add(downLoadResultListener);
        return this;
    }

    public PreAgentWeb createAgentWeb() {
        return mAgentBuilder.buildAgentWeb();
    }
}
