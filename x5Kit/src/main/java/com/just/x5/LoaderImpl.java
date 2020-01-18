package com.just.x5;

import android.os.Handler;
import android.os.Looper;

import com.just.x5.util.AgentWebX5Utils;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;

/**
 * 页面加载具体实现
 */

public class LoaderImpl implements ILoader {
    private Handler mHandler;
    private WebView mWebView;
    private Map<String, String> headers;

    LoaderImpl(WebView webView, Map<String, String> map) {
        this.mWebView = webView;
        this.headers = map;
        mHandler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void loadUrl(final String url) {
        if (!AgentWebX5Utils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl(url);
                }
            });
        } else {
            if (!AgentWebX5Utils.isEmptyMap(this.headers)) {
                this.mWebView.loadUrl(url, headers);
            } else {
                this.mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void reload() {
        if (!AgentWebX5Utils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            });
        } else {
            this.mWebView.reload();
        }
    }

    @Override
    public void loadData(final String data, final String mimeType, final String encoding) {
        if (!AgentWebX5Utils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadData(data, mimeType, encoding);
                }
            });
        } else {
            this.mWebView.loadData(data, mimeType, encoding);
        }
    }

    @Override
    public void stopLoading() {
        if (!AgentWebX5Utils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopLoading();
                }
            });
        } else {
            this.mWebView.stopLoading();
        }
    }

    @Override
    public void loadDataWithBaseURL(final String baseUrl, final String data, final String mimeType, final String encoding, final String historyUrl) {
        if (!AgentWebX5Utils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
                }
            });
        } else {
            this.mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        }
    }
}
