package com.just.agentwebX5;

import android.view.KeyEvent;

import com.tencent.smtt.sdk.WebView;

/**
 * 按键事件拦截实现类
 */
public class EventHandlerImpl implements IEventHandler {
    private WebView mWebView;
    private IEventInterceptor mEventInterceptor;

    public static EventHandlerImpl getInstance(WebView view, IEventInterceptor eventInterceptor) {
        return new EventHandlerImpl(view, eventInterceptor);
    }

    private EventHandlerImpl(WebView webView, IEventInterceptor eventInterceptor) {
        this.mWebView = webView;
        this.mEventInterceptor = eventInterceptor;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN) {
            return back();
        }
        return false;
    }

    @Override
    public boolean back() {
        if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
            return true;
        }
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }
}
