package com.just.agentwebX5;

import android.view.ViewGroup;

import com.just.agentwebX5.progress.IProgressManager;

/**
 * 网页加载构造器
 */

public interface IWebCreator extends IProgressManager {
    IWebCreator create();

    com.tencent.smtt.sdk.WebView get();

    ViewGroup getGroup();
}
