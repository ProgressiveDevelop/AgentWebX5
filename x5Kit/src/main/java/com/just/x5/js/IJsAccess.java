package com.just.x5.js;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tencent.smtt.sdk.ValueCallback;

/**
 *
 */

public interface IJsAccess {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    void callJs(String js, ValueCallback<String> callback, String... params);

    void callJs(String js, ValueCallback<String> callback);

    void callJs(String method, String... params);

    void callJs(String js);
}
