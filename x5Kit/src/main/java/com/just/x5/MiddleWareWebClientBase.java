package com.just.x5;


import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.WebViewClient;

/**
 *
 */

public class MiddleWareWebClientBase extends WrapperWebViewClient {
    private MiddleWareWebClientBase mMiddleWrareWebClientBase;
    private String TAG = this.getClass().getSimpleName();

    public MiddleWareWebClientBase(WebViewClient client) {
        super(client);
    }

    MiddleWareWebClientBase next() {
        LogUtils.getInstance().e(TAG, "next");
        return this.mMiddleWrareWebClientBase;
    }


    @Override
    public void setWebViewClient(WebViewClient webViewClient) {
        super.setWebViewClient(webViewClient);

    }

    public MiddleWareWebClientBase enq(MiddleWareWebClientBase middleWrareWebClientBase) {
        setWebViewClient(middleWrareWebClientBase);
        this.mMiddleWrareWebClientBase = middleWrareWebClientBase;
        return middleWrareWebClientBase;
    }


}
