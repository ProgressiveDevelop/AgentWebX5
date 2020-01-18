package com.just.x5;

import android.view.KeyEvent;

import com.just.x5.util.LogUtils;
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
        } else {
            return false;
        }
    }

    @Override
    public boolean back() {
        if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
            LogUtils.getInstance().e(getClass().getSimpleName(), "webView go back");
            return true;
        } else {
            return false;
        }
    }
}
