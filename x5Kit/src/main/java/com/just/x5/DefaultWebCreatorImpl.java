package com.just.x5;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.progress.BaseIndicatorView;
import com.just.x5.progress.IBaseProgressSpec;
import com.just.x5.progress.WebProgress;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.WebView;

/**
 * 默认网页加载器
 */
public class DefaultWebCreatorImpl implements IWebCreator {
    private Activity mActivity;
    private ViewGroup mViewGroup;
    private boolean isNeedDefaultProgress;
    private int index;
    private BaseIndicatorView progressView;
    private ViewGroup.LayoutParams mLayoutParams;
    private int color = -1;
    private int height_dp;
    private boolean isCreated = false;
    private IWebLayout mIWebLayout;
    private IBaseProgressSpec mBaseProgressSpec;
    private WebView mWebView;
    private FrameLayout mFrameLayout = null;

    protected DefaultWebCreatorImpl(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, int color, int height_dp, WebView webView, IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = true;
        this.index = index;
        this.color = color;
        this.mLayoutParams = lp;
        this.height_dp = height_dp;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

    protected DefaultWebCreatorImpl(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, @Nullable WebView webView, IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = false;
        this.index = index;
        this.mLayoutParams = lp;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

    protected DefaultWebCreatorImpl(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp, int index, BaseIndicatorView progressView, WebView webView, IWebLayout webLayout) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.isNeedDefaultProgress = false;
        this.index = index;
        this.mLayoutParams = lp;
        this.progressView = progressView;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }


    @Override
    public DefaultWebCreatorImpl create() {
        if (isCreated) {
            return this;
        }
        isCreated = true;
        if (mViewGroup == null) {
            mViewGroup = createGroupWithWeb();
            mActivity.setContentView(mViewGroup);
        } else {
            if (index == -1) {
                mViewGroup.addView(createGroupWithWeb(), mLayoutParams);
            } else {
                mViewGroup.addView(createGroupWithWeb(), index, mLayoutParams);
            }
        }
        return this;
    }

    /**
     * 创建布局容器
     */
    private ViewGroup createGroupWithWeb() {
        FrameLayout mFrameLayout = new FrameLayout(mActivity);
        mFrameLayout.setBackgroundColor(Color.WHITE);
        View target = mIWebLayout == null ? (this.mWebView = web()) : webLayout();
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        //添加WebView
        mFrameLayout.addView(target, mLayoutParams);
        if (isNeedDefaultProgress) {
            WebProgress mWebProgress = new WebProgress(mActivity);
            if (color != -1) {
                mWebProgress.setColor(color);
            }
            FrameLayout.LayoutParams lp;
            if (height_dp > 0) {
                lp = new FrameLayout.LayoutParams(-2, AgentWebX5Utils.dp2px(mActivity, height_dp));
            } else {
                lp = mWebProgress.offerLayoutParams();
            }
            lp.gravity = Gravity.TOP;
            //添加进度条
            mFrameLayout.addView((View) (this.mBaseProgressSpec = mWebProgress), lp);
            mWebProgress.setVisibility(View.GONE);
        } else if (progressView != null) {
            mFrameLayout.addView((View) (this.mBaseProgressSpec = progressView), progressView.offerLayoutParams());
        }
        return this.mFrameLayout = mFrameLayout;
    }

    @Override
    public WebView get() {
        return mWebView;
    }

    @Override
    public ViewGroup getGroup() {
        return mFrameLayout;
    }

    /**
     * 获取布局容器
     *
     * @return view
     */
    private View webLayout() {
        mWebView = mIWebLayout.getWeb();
        if (mWebView == null) {
            mWebView = web();
            //添加WebView
            mIWebLayout.getLayout().addView(mWebView, -1, -1);
            LogUtils.getInstance().e(getClass().getSimpleName(), "add web view");
        }
        return mIWebLayout.getLayout();
    }

    /**
     * 获取WebView
     *
     * @return WebView
     */
    private WebView web() {
        if (mWebView != null) {
            AgentWebX5Config.WEBVIEW_TYPE = AgentWebX5Config.WEBVIEW_CUSTOM_TYPE;
        } else {
            mWebView = new WebView(mActivity);
            AgentWebX5Config.WEBVIEW_TYPE = AgentWebX5Config.WEBVIEW_DEFAULT_TYPE;
        }
        return mWebView;
    }

    @Override
    public IBaseProgressSpec offer() {
        return mBaseProgressSpec;
    }
}
