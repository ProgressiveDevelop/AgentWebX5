package com.just.x5.js;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

public class JsAccessImpl implements IJsAccess {
    public static final String TAG = "JsAccessImpl";
    private WebView mWebView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public JsAccessImpl(WebView webView) {
        this.mWebView = webView;
    }


    @Override
    public void callJs(String js) {
        this.callJs(js, (String) null);
    }

    @Override
    public void callJs(String method, String... params) {
        this.callJs(method, null, params);
    }

    @Override
    public void callJs(String method, ValueCallback<String> callback, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:").append(method);
        if (params == null || params.length == 0) {
            sb.append("()");
        } else {
            sb.append("(").append(concat(params)).append(")");
        }
        callJs(sb.toString(), callback);
    }

    @Override
    public void callJs(final String js, final ValueCallback<String> callback) {
        LogUtils.getInstance().e(TAG, "js:" + js);
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            executeFunction(js, callback);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    executeFunction(js, callback);
                }
            });
        }

    }

    private void executeFunction(String js, final ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogUtils.getInstance().e(TAG, "evaluateJs：" + js);
            mWebView.evaluateJavascript(js, new com.tencent.smtt.sdk.ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    LogUtils.getInstance().e(TAG, "onReceiveValue：" + value);
                    if (callback != null) {
                        callback.onReceiveValue(value);
                    }
                }
            });
        } else {
            LogUtils.getInstance().e(TAG, "loadJs：" + js);
            mWebView.loadUrl(js);
        }
    }

    /**
     * 组合 js 方法参数
     *
     * @param params 可变数组
     */
    private String concat(String... params) {
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (!AgentWebX5Utils.isJson(param)) {
                mStringBuilder.append("\"").append(param).append("\"");
            } else {
                mStringBuilder.append(param);
            }
            if (i != params.length - 1) {
                mStringBuilder.append(" , ");
            }
        }
        return mStringBuilder.toString();
    }
}
