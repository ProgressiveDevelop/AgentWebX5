package com.just.x5;


import com.tencent.smtt.sdk.WebChromeClient;

/**
 *
 */
public class MiddleWareWebChromeBase extends WebChromeClientWrapper {

    private MiddleWareWebChromeBase mMiddleWareWebChromeBase;

    public MiddleWareWebChromeBase(WebChromeClient webChromeClient) {
        super(webChromeClient);
    }

    @Override
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    public MiddleWareWebChromeBase enq(MiddleWareWebChromeBase middleWareWebChromeBase) {
        setWebChromeClient(middleWareWebChromeBase);
        this.mMiddleWareWebChromeBase = middleWareWebChromeBase;
        return this.mMiddleWareWebChromeBase;
    }


    public MiddleWareWebChromeBase next() {
        return this.mMiddleWareWebChromeBase;
    }

}
