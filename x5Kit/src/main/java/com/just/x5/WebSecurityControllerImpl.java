package com.just.x5;

import androidx.collection.ArrayMap;

import com.tencent.smtt.sdk.WebView;


/**
 *
 */

public class WebSecurityControllerImpl implements IWebSecurityController<IWebSecurityCheckLogic> {

    private WebView mWebView;
    private ArrayMap<String, Object> mMap;
    private SecurityType mSecurityType;

    public WebSecurityControllerImpl(WebView view, ArrayMap<String, Object> map,SecurityType securityType) {
        this.mWebView = view;
        this.mMap = map;
        this.mSecurityType = securityType;
    }

    @Override
    public void check(IWebSecurityCheckLogic webSecurityCheckLogic) {
        webSecurityCheckLogic.dealHoneyComb(mWebView);
        if (mMap != null && mSecurityType ==SecurityType.strict && !mMap.isEmpty()) {
            webSecurityCheckLogic.dealJsInterface(mMap, mSecurityType);
        }
    }
}
