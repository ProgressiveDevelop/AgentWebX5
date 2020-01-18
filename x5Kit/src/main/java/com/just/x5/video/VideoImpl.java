package com.just.x5.video;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.just.x5.IEventInterceptor;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebView;


/**
 * 自定义Video实现
 */

public class VideoImpl implements IVideo, IEventInterceptor {
    public static final String TAG = "VideoImpl";
    private Activity mActivity;
    private WebView mWebView;
    private View movieView = null;
    private ViewGroup movieParentView = null;
    private IX5WebChromeClient.CustomViewCallback mCallback;

    public VideoImpl(Activity mActivity, WebView webView) {
        this.mActivity = mActivity;
        this.mWebView = webView;
    }

    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        LogUtils.getInstance().e(TAG, "onShowCustomView:" + view);
        Activity mActivity;
        if ((mActivity = this.mActivity) == null) {
            return;
        }
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (movieView != null) {
            callback.onCustomViewHidden();
            return;
        }
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
        }
        if (movieParentView == null) {
            FrameLayout mDecorView = (FrameLayout) mActivity.getWindow().getDecorView();
            movieParentView = new FrameLayout(mActivity);
            movieParentView.setBackgroundColor(Color.BLACK);
            mDecorView.addView(movieParentView);
        }
        this.mCallback = callback;
        movieParentView.addView(this.movieView = view);
        movieParentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideCustomView() {
        LogUtils.getInstance().e(TAG, "onHideCustomView:" + movieView);
        if (movieView == null) {
            return;
        }
        if (mActivity != null && mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        movieView.setVisibility(View.GONE);
        if (movieParentView != null && movieView != null) {
            movieParentView.removeView(movieView);
        }
        if (movieParentView != null) {
            movieParentView.setVisibility(View.GONE);
        }
        if (this.mCallback != null) {
            mCallback.onCustomViewHidden();
        }
        this.movieView = null;
        if (mWebView != null) {
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isVideoState() {
        return movieView != null;
    }

    @Override
    public boolean event() {
        if (isVideoState()) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }
}
