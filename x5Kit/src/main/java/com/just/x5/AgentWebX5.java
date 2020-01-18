package com.just.x5;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.just.x5.builder.AgentBuilder;
import com.just.x5.builder.AgentBuilderFragment;
import com.just.x5.downFile.DefaultDownLoaderImpl;
import com.just.x5.downFile.DownLoadResultListener;
import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.downFile.DownLoadMsgConfig;
import com.just.x5.helpClass.WebViewClientMsgCfg;
import com.just.x5.js.IJsAccess;
import com.just.x5.js.IJsInterfaceHolder;
import com.just.x5.js.JsAccessImpl;
import com.just.x5.js.JsInterfaceHolderImpl;
import com.just.x5.permission.IPermissionInterceptor;
import com.just.x5.progress.BaseIndicatorView;
import com.just.x5.progress.IndicatorController;
import com.just.x5.progress.IndicatorHandlerImpl;
import com.just.x5.uploadFile.FileUploadPopImpl;
import com.just.x5.uploadFile.IFileUploadChooser;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.just.x5.video.IVideo;
import com.just.x5.video.VideoImpl;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;
import java.util.Map;

/**
 * https://github.com/Justson/AgentWebX5
 */
public class AgentWebX5 {
    private static final String TAG = AgentWebX5.class.getSimpleName();
    private Activity mActivity;
    private ViewGroup mViewGroup;
    private AgentWebX5 mAgentWebX5;
    private WebChromeClient mWebChromeClient;
    private WebChromeClient mTargetChromeClient;
    private WebViewClient mWebViewClient;
    private SecurityType mSecurityType;
    private FileUploadPopImpl uploadPop = null;
    private boolean enableProgress;
    private ArrayMap<String, Object> mJavaObjects = new ArrayMap<>();
    private IEventHandler mIEventHandler;
    private IWebListenerManager mWebListenerManager;
    private IWebSecurityController<IWebSecurityCheckLogic> mWebSecurityController;
    private IWebSecurityCheckLogic mWebSecurityCheckLogic = null;
    private ILoader mILoader;
    private IAgentWebInterface mAgentWebCompatInterface;
    private IReceivedTitleCallback receivedTitleCallback;
    private boolean webClientHelper;
    private WebViewClientCallbackManager mWebViewClientCallbackManager;
    private boolean isInterceptUnkownScheme;
    private int openOtherAppWays = -1;
    private MiddleWareWebClientBase mMiddleWrareWebClientBaseHeader;
    private MiddleWareWebChromeBase mMiddleWareWebChromeBaseHeader;
    /**
     * 网页构造器
     */
    private IWebCreator webCreator;

    public IWebCreator getWebCreator() {
        return this.webCreator;
    }

    /**
     * 网页配置类
     */
    private IWebSettings mWebSettings;

    public IWebSettings getWebSettings() {
        return this.mWebSettings;
    }

    /**
     * 进度控制器
     */
    private IndicatorController mIndicatorController;

    private IndicatorController getIndicatorController() {
        return this.mIndicatorController;
    }

    /**
     * 下载监听器
     */
    private DownloadListener downloadListener = null;

    private DownloadListener getDownloadListener() {
        return downloadListener;
    }

    /**
     * 添加JS调用
     */
    private IJsInterfaceHolder mJsInterfaceHolder = null;

    public IJsInterfaceHolder getJsInterfaceHolder() {
        return this.mJsInterfaceHolder;
    }

    /**
     * 调用JS对象
     */
    private IJsAccess jsAccess;

    public IJsAccess getJsAccess() {
        if (this.jsAccess == null) {
            this.jsAccess = new JsAccessImpl(webCreator.get());
        }
        return this.jsAccess;
    }

    /**
     * 生命周期
     */
    private IWebLifeCycle mWebLifeCycle;

    public IWebLifeCycle getWebLifeCycle() {
        return this.mWebLifeCycle;
    }

    /**
     * 视频接口
     */
    private IVideo mIVideo = null;

    private IVideo getIVideo() {
        return mIVideo == null ? new VideoImpl(mActivity, webCreator.get()) : mIVideo;
    }

    /**
     * 权限拦截接口
     */
    private IPermissionInterceptor mPermissionInterceptor;

    public IPermissionInterceptor getPermissionInterceptor() {
        return mPermissionInterceptor;
    }

    private IEventInterceptor eventInterceptor;

    private IEventInterceptor getEventInterceptor() {
        if (this.eventInterceptor != null) {
            return this.eventInterceptor;
        }
        if (mIVideo instanceof VideoImpl) {
            return this.eventInterceptor = (IEventInterceptor) this.mIVideo;
        }
        return null;
    }

    //X5 Activity

    /**
     * 构建AgentBuilder Activity
     *
     * @param activity 当前Activity
     */
    public static AgentBuilder with(@NonNull Activity activity) {
        return new AgentBuilder(activity);
    }

    public AgentWebX5(AgentBuilder builder) {
        this.mActivity = builder.getmActivity();
        this.mViewGroup = builder.getmViewGroup();
        this.mIEventHandler = builder.getmIEventHandler();
        this.enableProgress = builder.isEnableProgress();
        webCreator = builder.getmWebCreator() == null
                ? configWebCreator(builder.getIndicatorView(),
                builder.getIndex(), builder.getmLayoutParams(), builder.getmIndicatorColor(),
                builder.getmIndicatorColorWithHeight(), builder.getmWebView(), builder.getmWebLayout())
                : builder.getmWebCreator();
        mIndicatorController = builder.getmIndicatorController();
        this.mWebChromeClient = builder.getmWebChromeClient();
        this.mWebViewClient = builder.getmWebViewClient();
        mAgentWebX5 = this;
        this.mWebSettings = builder.getmWebSettings();
        if (builder.getmJavaObject() != null && builder.getmJavaObject().isEmpty()) {
            this.mJavaObjects.putAll((Map<? extends String, ?>) builder.getmJavaObject());
        }
        this.receivedTitleCallback = builder.getReceivedTitleCallback();
        this.mWebViewClientCallbackManager = builder.getmWebViewClientCallbackManager();
        this.mSecurityType = builder.getmSecurityType();
        this.mILoader = new LoaderImpl(webCreator.create().get(), builder.getHeaders());
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(webCreator.get());
        mWebSecurityController = new WebSecurityControllerImpl(webCreator.get(), this.mAgentWebX5.mJavaObjects, mSecurityType);
        this.webClientHelper = builder.isWebclientHelper();
        this.isInterceptUnkownScheme = builder.isInterceptUnkownScheme();
        if (builder.getOpenOtherPage() != null) {
            this.openOtherAppWays = builder.getOpenOtherPage().code;
        }
        this.mMiddleWrareWebClientBaseHeader = builder.getHeader();
        this.mMiddleWareWebChromeBaseHeader = builder.getmChromeMiddleWareHeader();
        init();
        setDownloadListener(builder.getmDownLoadResultListeners(), builder.isParallelDownload(), builder.getIcon());
    }

    //X5 Fragment

    /**
     * 创建 AgentBuilder Fragment
     *
     * @param fragment 当前 Fragment
     * @return AgentBuilderFragment实例对象
     */
    public static AgentBuilderFragment with(@NonNull Fragment fragment) {
        Activity mActivity = fragment.getActivity();
        if (mActivity == null) {
            throw new NullPointerException();
        } else {
            return new AgentBuilderFragment(mActivity, fragment);
        }
    }

    public AgentWebX5(AgentBuilderFragment builder) {
        this.mActivity = builder.getmActivity();
        this.mViewGroup = builder.getmViewGroup();
        this.mIEventHandler = builder.getmIEventHandler();
        this.enableProgress = builder.isEnableProgress();
        webCreator = builder.getmWebCreator() == null
                ? configWebCreator(builder.getIndicatorView(),
                builder.getIndex(), builder.getmLayoutParams(), builder.getmIndicatorColor(),
                builder.getHeight_dp(), builder.getmWebView(), builder.getWebLayout())
                : builder.getmWebCreator();
        mIndicatorController = builder.getmIndicatorController();
        this.mWebChromeClient = builder.getmWebChromeClient();
        this.mWebViewClient = builder.getmWebViewClient();
        mAgentWebX5 = this;
        this.mWebSettings = builder.getmWebSettings();
        if (builder.getmJavaObject() != null && builder.getmJavaObject().isEmpty()) {
            this.mJavaObjects.putAll((Map<? extends String, ?>) builder.getmJavaObject());
        }
        this.receivedTitleCallback = builder.getReceivedTitleCallback();
        this.mWebViewClientCallbackManager = builder.getmWebViewClientCallbackManager();
        this.mSecurityType = builder.getmSecurityType();
        this.mILoader = new LoaderImpl(webCreator.create().get(), builder.getAdditionalHttpHeaders());
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(webCreator.get());
        mWebSecurityController = new WebSecurityControllerImpl(webCreator.get(), this.mAgentWebX5.mJavaObjects, mSecurityType);
        this.webClientHelper = builder.isWebClientHelper();
        this.isInterceptUnkownScheme = builder.isInterceptUnkownScheme();
        if (builder.getOpenOtherPage() != null) {
            this.openOtherAppWays = builder.getOpenOtherPage().code;
        }
        this.mMiddleWrareWebClientBaseHeader = builder.getHeader();
        this.mMiddleWareWebChromeBaseHeader = builder.getmChromeMiddleWareHeader();
        init();
        setDownloadListener(builder.getmDownLoadResultListeners(), builder.isParallelDownload(), builder.getIcon());
    }


    //设置下载监听器
    private void setDownloadListener(List<DownLoadResultListener> downLoadResultListeners, boolean isParallelDl, int icon) {
        if (downloadListener == null) {
            downloadListener = new DefaultDownLoaderImpl.Builder()
                    .setActivity(mActivity)
                    .setEnableIndicator(true)//
                    .setForce(false)//
                    .setDownLoadResultListeners(downLoadResultListeners)//
                    .setDownLoadMsgConfig(new DownLoadMsgConfig())//
                    .setParallelDownload(isParallelDl)//
                    .setPermissionInterceptor(this.mPermissionInterceptor)
                    .setIcon(icon)
                    .create();
        }
    }

    /**
     * 配置网页容器
     */
    private IWebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int mIndicatorColor, int height_dp, WebView webView, IWebLayout webLayout) {
        if (progressView != null && enableProgress) {
            return new DefaultWebCreatorImpl(mActivity, mViewGroup, lp, index, progressView, webView, webLayout);
        } else {
            return enableProgress ?
                    new DefaultWebCreatorImpl(mActivity, mViewGroup, lp, index, mIndicatorColor, height_dp, webView, webLayout)
                    : new DefaultWebCreatorImpl(mActivity, mViewGroup, lp, index, webView, webLayout);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        uploadPop = new FileUploadPopImpl(this, mActivity);
        mJavaObjects.put("agentWebX5", uploadPop);
        LogUtils.getInstance().e(TAG, "webView type:" + AgentWebX5Config.WEBVIEW_TYPE);
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE) {
            this.mAgentWebCompatInterface = (IAgentWebInterface) webCreator.get();
            this.mWebViewClientCallbackManager.setPageLifeCycleCallback((IPageLoad) webCreator.get());
        }
        if (mWebSecurityCheckLogic == null) {
            this.mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
        }
        mWebSecurityController.check(mWebSecurityCheckLogic);
    }

    public AgentWebX5 ready() {
        AgentWebX5Config.initCookiesManager(mActivity.getApplicationContext());
        IWebSettings mWebSettings = this.mWebSettings;
        if (mWebSettings == null) {
            this.mWebSettings = mWebSettings = WebDefaultSettingsImpl.getInstance();
        }
        if (mWebListenerManager == null && mWebSettings instanceof WebDefaultSettingsImpl) {
            mWebListenerManager = (IWebListenerManager) mWebSettings;
        }
        mWebSettings.toSetting(webCreator.get());
        if (mJsInterfaceHolder == null) {
            mJsInterfaceHolder = new JsInterfaceHolderImpl(webCreator.get());
        }
        if (mJavaObjects != null && !mJavaObjects.isEmpty()) {
            mJsInterfaceHolder.addJavaObjects(mJavaObjects);
        }
        mWebListenerManager.setDownLoader(webCreator.get(), getDownloadListener());
        //设置默认WebChromeClient
        mWebListenerManager.setWebChromeClient(webCreator.get(), getChromeClient());
        //设置默认WebViewClient
        mWebListenerManager.setWebViewClient(webCreator.get(), getClient());
        return this;
    }

    public AgentWebX5 go(String url) {
        IndicatorController mIndicatorController = getIndicatorController();
        if (!TextUtils.isEmpty(url) && mIndicatorController != null && mIndicatorController.offerIndicator() != null) {
            mIndicatorController.offerIndicator().show();
        }
        mILoader.loadUrl(url);
        return this;
    }

    private WebChromeClient getChromeClient() {
        IndicatorController mIndicatorController = (this.mIndicatorController == null) ? IndicatorHandlerImpl.getInstance().inJectProgressView(webCreator.offer()) : this.mIndicatorController;
        DefaultChromeClient mDefaultChromeClient = new DefaultChromeClient(this.mActivity, this.mIndicatorController = mIndicatorController, this.mWebChromeClient, this.mIVideo = getIVideo(), this.mPermissionInterceptor, webCreator.get(), this.receivedTitleCallback, this.mAgentWebCompatInterface);
        LogUtils.getInstance().e(TAG, "WebChromeClient:" + this.mWebChromeClient);
        MiddleWareWebChromeBase header = this.mMiddleWareWebChromeBaseHeader;
        if (header != null) {
            header.setWebChromeClient(mDefaultChromeClient);
            return this.mTargetChromeClient = header;
        } else {
            return this.mTargetChromeClient = mDefaultChromeClient;
        }
    }

    private WebViewClient getClient() {
        LogUtils.getInstance().e(TAG, "WebViewClient:" + this.mMiddleWrareWebClientBaseHeader);
        DefaultWebClient mDefaultWebClient = DefaultWebClient
                .createBuilder()
                .setActivity(this.mActivity)
                .setClient(this.mWebViewClient)
                .setManager(this.mWebViewClientCallbackManager)
                .setWebClientHelper(this.webClientHelper)
                .setPermissionInterceptor(this.mPermissionInterceptor)
                .setWebView(this.webCreator.get())
                .setInterceptUnkownScheme(this.isInterceptUnkownScheme)
                .setSchemeHandleType(this.openOtherAppWays)
                .setCfg(new WebViewClientMsgCfg())
                .build();
        MiddleWareWebClientBase header = this.mMiddleWrareWebClientBaseHeader;
        if (header != null) {
            header.setWebViewClient(mDefaultWebClient);
            return header;
        } else {
            return mDefaultWebClient;
        }
    }

    private void loadData(String data, String mimeType, String encoding) {
        webCreator.get().loadData(data, mimeType, encoding);
    }

    private void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String history) {
        webCreator.get().loadDataWithBaseURL(baseUrl, data, mimeType, encoding, history);
    }

    /**
     * 选择文件返回结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        intent
     */
    public void uploadFileResult(int requestCode, int resultCode, Intent data) {
        IFileUploadChooser mIFileUploadChooser = null;
        if (mTargetChromeClient instanceof DefaultChromeClient) {
            DefaultChromeClient mDefaultChromeClient = (DefaultChromeClient) mTargetChromeClient;
            mIFileUploadChooser = mDefaultChromeClient.pop();
        }
        if (mIFileUploadChooser == null) {
            mIFileUploadChooser = uploadPop.pop();
        }
        if (mIFileUploadChooser != null) {
            mIFileUploadChooser.fetchFilePathFromIntent(requestCode, resultCode, data);
        }
    }


    /**
     * 清除缓存
     */
    public AgentWebX5 clearWebCache() {
        AgentWebX5Utils.clearWebViewAllCache(mActivity);
        return this;
    }

    /**
     * 按键事件拦截
     */
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (mIEventHandler == null) {
            if (webCreator.get() != null && webCreator.get().canGoBack()) {
                webCreator.get().goBack();
                return true;
            } else {
                return false;
            }
        } else {
            return mIEventHandler.onKeyDown(keyCode, keyEvent);
        }
    }

    //返回
    public boolean back() {
        if (mIEventHandler == null) {
            if (webCreator.get() != null && webCreator.get().canGoBack()) {
                webCreator.get().goBack();
                return true;
            } else {
                return false;
            }
        } else {
            return mIEventHandler.back();
        }
    }
}
