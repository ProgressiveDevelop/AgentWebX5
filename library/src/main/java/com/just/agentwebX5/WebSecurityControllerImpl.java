package com.just.agentwebX5;

import androidx.collection.ArrayMap;

import com.tencent.smtt.sdk.WebView;


/**
 *
 */

public class WebSecurityControllerImpl implements IWebSecurityController<IWebSecurityCheckLogic> {

    private WebView mWebView;
    private ArrayMap<String, Object> mMap;
    private AgentWebX5.SecurityType mSecurityType;

    public WebSecurityControllerImpl(WebView view, ArrayMap<String, Object> map, AgentWebX5.SecurityType securityType) {
        this.mWebView = view;
        this.mMap = map;
        this.mSecurityType = securityType;
    }

    @Override
    public void check(IWebSecurityCheckLogic webSecurityCheckLogic) {
        webSecurityCheckLogic.dealHoneyComb(mWebView);
        if (mMap != null && mSecurityType == AgentWebX5.SecurityType.strict && !mMap.isEmpty()) {
            webSecurityCheckLogic.dealJsInterface(mMap, mSecurityType);
        }
    }
}
