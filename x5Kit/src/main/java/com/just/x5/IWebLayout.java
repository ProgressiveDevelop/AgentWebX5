package com.just.x5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.ViewGroup;

import com.tencent.smtt.sdk.WebView;

/**
 * 网页布局容器
 */

public interface IWebLayout<T extends WebView, V extends ViewGroup> {

    /**
     * @return WebView 的父控件
     */
    @NonNull
    V getLayout();

    /**
     * @return 返回 WebView  或 WebView 的子View ，返回null AgentWebX5 内部会自动创建适当 WebView
     */
    @Nullable
    T getWeb();
}
