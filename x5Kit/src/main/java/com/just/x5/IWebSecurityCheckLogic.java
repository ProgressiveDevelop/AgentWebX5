package com.just.x5;

import androidx.collection.ArrayMap;

import com.tencent.smtt.sdk.WebView;

/**
 *
 */
public interface IWebSecurityCheckLogic {
    void dealHoneyComb(WebView view);

    void dealJsInterface(ArrayMap<String, Object> objects, SecurityType securityType);
}
