package com.just.x5.builder;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.just.x5.AgentWebX5;
import com.just.x5.DefaultWebClient;
import com.just.x5.IEventHandler;
import com.just.x5.IReceivedTitleCallback;
import com.just.x5.IWebCreator;
import com.just.x5.IWebLayout;
import com.just.x5.IWebSettings;
import com.just.x5.MiddleWareWebChromeBase;
import com.just.x5.MiddleWareWebClientBase;
import com.just.x5.SecurityType;
import com.just.x5.WebViewClientCallbackManager;
import com.just.x5.downFile.DownLoadResultListener;
import com.just.x5.progress.BaseIndicatorView;
import com.just.x5.progress.IndicatorController;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;
import java.util.Map;

public class AgentBuilderFragment {
    private Activity mActivity;
    private ViewGroup mViewGroup;
    private int index = -1;
    private BaseIndicatorView indicatorView;
    private IndicatorController mIndicatorController = null;
    /*默认进度条是打开的*/
    private boolean enableProgress = true;
    private ViewGroup.LayoutParams mLayoutParams = null;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;
    private int mIndicatorColor = -1;
    private IWebSettings mWebSettings;
    private IWebCreator mWebCreator;
    private Map<String, String> additionalHttpHeaders = null;
    private IEventHandler mIEventHandler;
    private int height_dp = -1;
    private ArrayMap<String, Object> mJavaObject;
    private IReceivedTitleCallback receivedTitleCallback;
    private SecurityType mSecurityType = SecurityType.default_check;
    private WebView mWebView;
    private WebViewClientCallbackManager mWebViewClientCallbackManager = new WebViewClientCallbackManager();
    private boolean webClientHelper = true;
    private List<DownLoadResultListener> mDownLoadResultListeners = null;
    private IWebLayout webLayout;
    private boolean isParallelDownload;
    private int icon = -1;
    private MiddleWareWebClientBase tail;
    private MiddleWareWebClientBase header;
    private MiddleWareWebChromeBase mChromeMiddleWareTail;
    private MiddleWareWebChromeBase mChromeMiddleWareHeader;
    private DefaultWebClient.OpenOtherPageWays openOtherPage;
    private boolean isInterceptUnkownScheme;

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public ViewGroup getmViewGroup() {
        return mViewGroup;
    }

    public void setmViewGroup(ViewGroup mViewGroup) {
        this.mViewGroup = mViewGroup;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BaseIndicatorView getIndicatorView() {
        return indicatorView;
    }

    public void setIndicatorView(BaseIndicatorView indicatorView) {
        this.indicatorView = indicatorView;
    }

    public IndicatorController getmIndicatorController() {
        return mIndicatorController;
    }

    public void setmIndicatorController(IndicatorController mIndicatorController) {
        this.mIndicatorController = mIndicatorController;
    }

    public boolean isEnableProgress() {
        return enableProgress;
    }

    public void setEnableProgress(boolean enableProgress) {
        this.enableProgress = enableProgress;
    }

    public ViewGroup.LayoutParams getmLayoutParams() {
        return mLayoutParams;
    }

    public void setmLayoutParams(ViewGroup.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
    }

    public WebViewClient getmWebViewClient() {
        return mWebViewClient;
    }

    public void setmWebViewClient(WebViewClient mWebViewClient) {
        this.mWebViewClient = mWebViewClient;
    }

    public WebChromeClient getmWebChromeClient() {
        return mWebChromeClient;
    }

    public void setmWebChromeClient(WebChromeClient mWebChromeClient) {
        this.mWebChromeClient = mWebChromeClient;
    }

    public int getmIndicatorColor() {
        return mIndicatorColor;
    }

    public void setmIndicatorColor(int mIndicatorColor) {
        this.mIndicatorColor = mIndicatorColor;
    }

    public IWebSettings getmWebSettings() {
        return mWebSettings;
    }

    public void setmWebSettings(IWebSettings mWebSettings) {
        this.mWebSettings = mWebSettings;
    }

    public IWebCreator getmWebCreator() {
        return mWebCreator;
    }

    public void setmWebCreator(IWebCreator mWebCreator) {
        this.mWebCreator = mWebCreator;
    }

    public Map<String, String> getAdditionalHttpHeaders() {
        return additionalHttpHeaders;
    }

    public void setAdditionalHttpHeaders(Map<String, String> additionalHttpHeaders) {
        this.additionalHttpHeaders = additionalHttpHeaders;
    }

    public IEventHandler getmIEventHandler() {
        return mIEventHandler;
    }

    public void setmIEventHandler(IEventHandler mIEventHandler) {
        this.mIEventHandler = mIEventHandler;
    }

    public int getHeight_dp() {
        return height_dp;
    }

    public void setHeight_dp(int height_dp) {
        this.height_dp = height_dp;
    }

    public ArrayMap<String, Object> getmJavaObject() {
        return mJavaObject;
    }

    public void setmJavaObject(ArrayMap<String, Object> mJavaObject) {
        this.mJavaObject = mJavaObject;
    }

    public IReceivedTitleCallback getReceivedTitleCallback() {
        return receivedTitleCallback;
    }

    public SecurityType getmSecurityType() {
        return mSecurityType;
    }

    public void setmSecurityType(SecurityType mSecurityType) {
        this.mSecurityType = mSecurityType;
    }

    public WebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public WebViewClientCallbackManager getmWebViewClientCallbackManager() {
        return mWebViewClientCallbackManager;
    }

    public void setmWebViewClientCallbackManager(WebViewClientCallbackManager mWebViewClientCallbackManager) {
        this.mWebViewClientCallbackManager = mWebViewClientCallbackManager;
    }

    public boolean isWebClientHelper() {
        return webClientHelper;
    }

    public void setWebClientHelper(boolean webClientHelper) {
        this.webClientHelper = webClientHelper;
    }

    public List<DownLoadResultListener> getmDownLoadResultListeners() {
        return mDownLoadResultListeners;
    }

    public void setmDownLoadResultListeners(List<DownLoadResultListener> mDownLoadResultListeners) {
        this.mDownLoadResultListeners = mDownLoadResultListeners;
    }

    public IWebLayout getWebLayout() {
        return webLayout;
    }

    public void setWebLayout(IWebLayout webLayout) {
        this.webLayout = webLayout;
    }

    public boolean isParallelDownload() {
        return isParallelDownload;
    }

    public void setParallelDownload(boolean parallelDownload) {
        isParallelDownload = parallelDownload;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public MiddleWareWebClientBase getTail() {
        return tail;
    }

    public void setTail(MiddleWareWebClientBase tail) {
        this.tail = tail;
    }

    public MiddleWareWebClientBase getHeader() {
        return header;
    }

    public void setHeader(MiddleWareWebClientBase header) {
        this.header = header;
    }

    public MiddleWareWebChromeBase getmChromeMiddleWareTail() {
        return mChromeMiddleWareTail;
    }

    public void setmChromeMiddleWareTail(MiddleWareWebChromeBase mChromeMiddleWareTail) {
        this.mChromeMiddleWareTail = mChromeMiddleWareTail;
    }

    public MiddleWareWebChromeBase getmChromeMiddleWareHeader() {
        return mChromeMiddleWareHeader;
    }

    public void setmChromeMiddleWareHeader(MiddleWareWebChromeBase mChromeMiddleWareHeader) {
        this.mChromeMiddleWareHeader = mChromeMiddleWareHeader;
    }

    public DefaultWebClient.OpenOtherPageWays getOpenOtherPage() {
        return openOtherPage;
    }

    public void setOpenOtherPage(DefaultWebClient.OpenOtherPageWays openOtherPage) {
        this.openOtherPage = openOtherPage;
    }

    public boolean isInterceptUnkownScheme() {
        return isInterceptUnkownScheme;
    }

    public void setInterceptUnkownScheme(boolean interceptUnkownScheme) {
        isInterceptUnkownScheme = interceptUnkownScheme;
    }

    /**
     * AgentBuilderFragment 构造器
     *
     * @param activity 当前Activity
     * @param fragment 当前 Fragment
     */
    public AgentBuilderFragment(@NonNull Activity activity, @NonNull Fragment fragment) {
        mActivity = activity;
    }

    /**
     * 设置父布局
     *
     * @param viewGroup 父控件
     * @param lp        布局参数
     * @return IndicatorBuilderForFragment
     */
    public IndicatorBuilderForFragment setAgentWebParent(@NonNull ViewGroup viewGroup, @NonNull ViewGroup.LayoutParams lp) {
        this.mViewGroup = viewGroup;
        this.mLayoutParams = lp;
        return new IndicatorBuilderForFragment(this);
    }

    public PreAgentWeb buildAgentWeb() {
        if (this.mViewGroup == null)
            throw new NullPointerException("ViewGroup is null,please check you params");
        return new PreAgentWeb(new AgentWebX5(this));
    }

    public void addJavaObject(String key, Object o) {
        if (mJavaObject == null) {
            mJavaObject = new ArrayMap<>();
        }
        mJavaObject.put(key, o);
    }

    public void addHeader(String k, String v) {
        if (additionalHttpHeaders == null) {
            additionalHttpHeaders = new ArrayMap<>();
        }
        additionalHttpHeaders.put(k, v);
    }

    public void setReceivedTitleCallback(IReceivedTitleCallback receivedTitleCallback) {
        this.receivedTitleCallback = receivedTitleCallback;
    }
}
