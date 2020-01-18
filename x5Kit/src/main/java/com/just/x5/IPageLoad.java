package com.just.x5;

import android.graphics.Bitmap;

import com.tencent.smtt.sdk.WebView;

/**
 * 网页加载接口
 */
public interface IPageLoad {
    /**
     * 开始加载
     *
     * @param webView WebView
     * @param url     链接路径
     * @param favicon 图标
     */
    void onPageStarted(WebView webView, String url, Bitmap favicon);

    /**
     * 页面加载完成
     *
     * @param webView WebView
     * @param url     链接路径
     */
    void onPageFinished(WebView webView, String url);
}
