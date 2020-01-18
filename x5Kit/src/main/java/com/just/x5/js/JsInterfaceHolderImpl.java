package com.just.x5.js;

import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.collection.ArrayMap;

import com.just.x5.helpClass.AgentWebX5Config;
import com.tencent.smtt.sdk.WebView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * source CODE  https://github.com/Justson/AgentWebX5
 */

public class JsInterfaceHolderImpl implements IJsInterfaceHolder {
    private WebView webView;

    public JsInterfaceHolderImpl(WebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean checkObject(Object v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        }
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE) {
            return true;
        }
        boolean tag = false;
        Class clazz = v.getClass();
        Method[] mMethods = clazz.getMethods();
        for (Method mMethod : mMethods) {
            Annotation[] mAnnotations = mMethod.getAnnotations();
            for (Annotation mAnnotation : mAnnotations) {
                if (mAnnotation instanceof JavascriptInterface) {
                    tag = true;
                    break;
                }
            }
            if (tag) {
                break;
            }
        }
        return tag;
    }

    @Override
    public IJsInterfaceHolder addJavaObjects(ArrayMap<String, Object> maps) {
        Set<Map.Entry<String, Object>> sets = maps.entrySet();
        for (Map.Entry<String, Object> mEntry : sets) {
            Object v = mEntry.getValue();
            boolean t = checkObject(v);
            if (t) {
                addJavaObjectDirect(mEntry.getKey(), v);
            }
        }
        return this;
    }

    @Override
    public IJsInterfaceHolder addJavaObject(String k, Object v) {
        boolean t = checkObject(v);
        if (t) {
            addJavaObjectDirect(k, v);
        }
        return this;
    }

    private void addJavaObjectDirect(String k, Object v) {
        this.webView.addJavascriptInterface(v, k);
    }

}
