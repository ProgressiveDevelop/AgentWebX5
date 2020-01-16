package com.just.agentwebX5;

/**
 *
 */

public class WebViewClientCallbackManager {
    private IPageLoad mPageLifeCycleCallback;

    public IPageLoad getPageLifeCycleCallback() {
        return mPageLifeCycleCallback;
    }

    public void setPageLifeCycleCallback(IPageLoad pageLifeCycleCallback) {
        mPageLifeCycleCallback = pageLifeCycleCallback;
    }
}
