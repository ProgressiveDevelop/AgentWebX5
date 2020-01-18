package com.just.x5.builder;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.collection.ArrayMap;

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
import com.just.x5.permission.IPermissionInterceptor;
import com.just.x5.progress.BaseIndicatorView;
import com.just.x5.progress.IndicatorController;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.Map;

public class AgentBuilder {
    private Activity mActivity;
    private ViewGroup mViewGroup;
    private boolean isNeedProgress;
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
    private IReceivedTitleCallback receivedTitleCallback;
    private WebViewClientCallbackManager mWebViewClientCallbackManager = new WebViewClientCallbackManager();
    private SecurityType mSecurityType = SecurityType.default_check;
    private Map<String, String> headers = null;
    private IWebLayout mWebLayout;
    private ArrayMap<String, Object> mJavaObject = null;
    private int mIndicatorColorWithHeight = -1;
    private WebView mWebView;
    private boolean webclientHelper = true;
    private ArrayList<DownLoadResultListener> mDownLoadResultListeners;
    private boolean isParallelDownload = false;
    private int icon = -1;
    private IPermissionInterceptor mPermissionInterceptor;
    private MiddleWareWebClientBase tail;
    private MiddleWareWebClientBase header;
    private MiddleWareWebChromeBase mChromeMiddleWareTail;
    private MiddleWareWebChromeBase mChromeMiddleWareHeader;
    private DefaultWebClient.OpenOtherPageWays openOtherPage;
    private boolean isInterceptUnkownScheme;

    public AgentBuilder(Activity activity) {
        this.mActivity = activity;
    }

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

    public boolean isNeedProgress() {
        return isNeedProgress;
    }

    public void setNeedProgress(boolean needProgress) {
        isNeedProgress = needProgress;
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

    public IReceivedTitleCallback getReceivedTitleCallback() {
        return receivedTitleCallback;
    }

    public WebViewClientCallbackManager getmWebViewClientCallbackManager() {
        return mWebViewClientCallbackManager;
    }

    public void setmWebViewClientCallbackManager(WebViewClientCallbackManager mWebViewClientCallbackManager) {
        this.mWebViewClientCallbackManager = mWebViewClientCallbackManager;
    }

    public SecurityType getmSecurityType() {
        return mSecurityType;
    }

    public void setmSecurityType(SecurityType mSecurityType) {
        this.mSecurityType = mSecurityType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public IWebLayout getmWebLayout() {
        return mWebLayout;
    }

    public void setmWebLayout(IWebLayout mWebLayout) {
        this.mWebLayout = mWebLayout;
    }

    public ArrayMap<String, Object> getmJavaObject() {
        return mJavaObject;
    }

    public void setmJavaObject(ArrayMap<String, Object> mJavaObject) {
        this.mJavaObject = mJavaObject;
    }

    public int getmIndicatorColorWithHeight() {
        return mIndicatorColorWithHeight;
    }

    public void setmIndicatorColorWithHeight(int mIndicatorColorWithHeight) {
        this.mIndicatorColorWithHeight = mIndicatorColorWithHeight;
    }

    public WebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public boolean isWebclientHelper() {
        return webclientHelper;
    }

    public void setWebclientHelper(boolean webclientHelper) {
        this.webclientHelper = webclientHelper;
    }

    public ArrayList<DownLoadResultListener> getmDownLoadResultListeners() {
        return mDownLoadResultListeners;
    }

    public void setmDownLoadResultListeners(ArrayList<DownLoadResultListener> mDownLoadResultListeners) {
        this.mDownLoadResultListeners = mDownLoadResultListeners;
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

    public IPermissionInterceptor getmPermissionInterceptor() {
        return mPermissionInterceptor;
    }

    public void setmPermissionInterceptor(IPermissionInterceptor mPermissionInterceptor) {
        this.mPermissionInterceptor = mPermissionInterceptor;
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

    public IEventHandler getmIEventHandler() {
        return mIEventHandler;
    }

    public void setmIEventHandler(IEventHandler mIEventHandler) {
        this.mIEventHandler = mIEventHandler;
    }

    public AgentBuilder enableProgress() {
        this.enableProgress = true;
        return this;
    }

    public AgentBuilder closeProgress() {
        this.enableProgress = false;
        return this;
    }

    public void addJavaObject(String key, Object o) {
        if (mJavaObject == null)
            mJavaObject = new ArrayMap<>();
        mJavaObject.put(key, o);
    }


    public ConfigIndicatorBuilder setAgentWebParent(ViewGroup viewGroup, ViewGroup.LayoutParams lp) {
        this.mViewGroup = viewGroup;
        mLayoutParams = lp;
        return new ConfigIndicatorBuilder(this);
    }

    public ConfigIndicatorBuilder setAgentWebParent(ViewGroup viewGroup, ViewGroup.LayoutParams lp, int position) {
        this.mViewGroup = viewGroup;
        mLayoutParams = lp;
        this.index = position;
        return new ConfigIndicatorBuilder(this);
    }

    public ConfigIndicatorBuilder createContentViewTag() {
        this.mViewGroup = null;
        this.mLayoutParams = null;
        return new ConfigIndicatorBuilder(this);
    }

    public void addHeader(String k, String v) {
        if (headers == null) {
            headers = new ArrayMap<>();
        }
        headers.put(k, v);
    }

    public PreAgentWeb buildAgentWeb() {
        return new PreAgentWeb(new AgentWebX5(this));
    }

    private IEventHandler mIEventHandler;

    public void setIndicatorColor(int indicatorColor) {
        mIndicatorColor = indicatorColor;
    }

    public void setIndicatorColorWithHeight(int indicatorColorWithHeight) {
        mIndicatorColorWithHeight = indicatorColorWithHeight;
    }

    public void setReceivedTitleCallback(IReceivedTitleCallback receivedTitleCallback) {
        this.receivedTitleCallback = receivedTitleCallback;
    }
}
