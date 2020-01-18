package com.just.x5;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.collection.ArrayMap;

import com.just.x5.helpClass.AgentWebX5Config;
import com.tencent.smtt.sdk.WebView;


/**
 *
 */

public class WebSecurityLogicImpl implements IWebSecurityCheckLogic {
    public static WebSecurityLogicImpl getInstance() {
        return new WebSecurityLogicImpl();
    }

    public WebSecurityLogicImpl() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void dealHoneyComb(WebView view) {
        if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
            return;
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        view.removeJavascriptInterface("accessibility");
        view.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void dealJsInterface(ArrayMap<String, Object> objects,SecurityType securityType) {
        if (securityType ==SecurityType.strict && AgentWebX5Config.WEBVIEW_TYPE != AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            objects.clear();
            System.gc();
        }
    }
}
