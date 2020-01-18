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
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;

public class CommonBuilderForFragment {
    private AgentBuilderFragment mAgentBuilderFragment;

    public CommonBuilderForFragment(AgentBuilderFragment agentBuilderFragment) {
        this.mAgentBuilderFragment = agentBuilderFragment;
    }

    public CommonBuilderForFragment setEventHanadler(@Nullable IEventHandler iEventHandler) {
        mAgentBuilderFragment.setmIEventHandler(iEventHandler);
        return this;
    }

    public CommonBuilderForFragment closeWebViewClientHelper() {
        mAgentBuilderFragment.setWebClientHelper(false);
        return this;
    }

    public CommonBuilderForFragment setWebCreator(@Nullable IWebCreator webCreator) {
        this.mAgentBuilderFragment.setmWebCreator(webCreator);
        return this;
    }

    public CommonBuilderForFragment setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
        this.mAgentBuilderFragment.setmWebChromeClient(webChromeClient);
        return this;

    }

    public CommonBuilderForFragment setWebViewClient(@Nullable WebViewClient webChromeClient) {
        this.mAgentBuilderFragment.setmWebViewClient(webChromeClient);
        return this;
    }

    public CommonBuilderForFragment useMiddleWareWebClient(@NonNull MiddleWareWebClientBase middleWrareWebClientBase) {
        if (this.mAgentBuilderFragment.getHeader() == null) {
            this.mAgentBuilderFragment.setHeader(middleWrareWebClientBase);
        } else {
            this.mAgentBuilderFragment.getTail().enq(middleWrareWebClientBase);
        }
        this.mAgentBuilderFragment.setTail(middleWrareWebClientBase);
        return this;
    }

    public CommonBuilderForFragment setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
        this.mAgentBuilderFragment.setOpenOtherPage(openOtherPageWays);
        return this;
    }

    public CommonBuilderForFragment interceptUnkownScheme() {
        this.mAgentBuilderFragment.setInterceptUnkownScheme(true);
        return this;
    }

    public CommonBuilderForFragment useMiddleWareWebChrome(@NonNull MiddleWareWebChromeBase middleWareWebChromeBase) {
        if (this.mAgentBuilderFragment.getmChromeMiddleWareHeader() == null) {
            this.mAgentBuilderFragment.setmChromeMiddleWareHeader(middleWareWebChromeBase);
        } else {
            this.mAgentBuilderFragment.getmChromeMiddleWareTail().enq(middleWareWebChromeBase);
        }
        this.mAgentBuilderFragment.setmChromeMiddleWareTail(middleWareWebChromeBase);
        return this;
    }

    public CommonBuilderForFragment setWebSettings(@Nullable IWebSettings webSettings) {
        this.mAgentBuilderFragment.setmWebSettings(webSettings);
        return this;
    }

    public PreAgentWeb createAgentWeb() {
        return this.mAgentBuilderFragment.buildAgentWeb();
    }

    public CommonBuilderForFragment setReceivedTitleCallback(@Nullable IReceivedTitleCallback receivedTitleCallback) {
        this.mAgentBuilderFragment.setReceivedTitleCallback(receivedTitleCallback);
        return this;
    }

    public CommonBuilderForFragment addJavascriptInterface(@NonNull String name, @NonNull Object o) {
        this.mAgentBuilderFragment.addJavaObject(name, o);
        return this;
    }

    public CommonBuilderForFragment setSecurityType(SecurityType type) {
        this.mAgentBuilderFragment.setmSecurityType(type);
        return this;
    }

    public CommonBuilderForFragment setWebView(@Nullable WebView webView) {
        this.mAgentBuilderFragment.setmWebView(webView);
        return this;
    }

    public CommonBuilderForFragment additionalHttpHeaders(String k, String v) {
        this.mAgentBuilderFragment.addHeader(k, v);

        return this;
    }

    public CommonBuilderForFragment openParallelDownload() {
        this.mAgentBuilderFragment.setParallelDownload(true);
        return this;
    }

    public CommonBuilderForFragment setNotifyIcon(@DrawableRes int icon) {
        this.mAgentBuilderFragment.setIcon(icon);
        return this;
    }

    public CommonBuilderForFragment setWebLayout(@Nullable IWebLayout iWebLayout) {
        this.mAgentBuilderFragment.setWebLayout(iWebLayout);
        return this;
    }

    public CommonBuilderForFragment addDownLoadResultListener(DownLoadResultListener downLoadResultListener) {
        if (this.mAgentBuilderFragment.getmDownLoadResultListeners() == null) {
            this.mAgentBuilderFragment.setmDownLoadResultListeners(new ArrayList<DownLoadResultListener>());
        }
        this.mAgentBuilderFragment.getmDownLoadResultListeners().add(downLoadResultListener);
        return this;
    }
}
