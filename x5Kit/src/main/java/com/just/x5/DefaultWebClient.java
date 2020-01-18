package com.just.x5;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.helpClass.WebViewClientMsgCfg;
import com.just.x5.permission.IPermissionInterceptor;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 默认WebClient
 */
public class DefaultWebClient extends WrapperWebViewClient {
    public static final String TAG = "DefaultWebClient";
    private WebViewClientCallbackManager mWebViewClientCallbackManager;
    private WeakReference<Activity> mWeakReference;
    private static final int CONSTANTS_ABNORMAL_BIG = 7;
    private WebViewClient mWebViewClient;
    private boolean webClientHelper;
    private static final String WEBVIEWCLIENTPATH = "com.tencent.smtt.sdk.WebViewClient";
    private static final String INTENT_SCHEME = "intent://";
    private static final String WEBCHAT_PAY_SCHEME = "weixin://wap/pay?";
    private static final String SCHEME_SMS = "sms:";
    private static final String ALIPAYS_SCHEME = "alipays://";
    private static final String HTTP_SCHEME = "http://";
    private static final String HTTPS_SCHEME = "https://";

    private static final int DERECT_OPEN_OTHER_APP = 1001;
    private static final int ASK_USER_OPEN_OTHER_APP = DERECT_OPEN_OTHER_APP >> 2;

    private boolean isInterceptUnkownScheme = true;


    private static final boolean hasAlipayLib;
    private int schemeHandleType = ASK_USER_OPEN_OTHER_APP;
    private WebViewClientMsgCfg mMsgCfg;
    private WebView mWebView;

    //检查是否有支付宝sdk
    static {
        boolean tag = true;
        try {
            Class.forName("com.alipay.sdk.app.PayTask");
        } catch (Throwable ignore) {
            tag = false;
        }
        hasAlipayLib = tag;
        LogUtils.getInstance().e(TAG, "has AlipayLib：" + hasAlipayLib);
    }

    private DefaultWebClient(Builder builder) {
        super(builder.client);
        this.mWebView = builder.webView;
        this.mWebViewClient = builder.client;
        mWeakReference = new WeakReference<>(builder.activity);
        this.mWebViewClientCallbackManager = builder.manager;
        this.webClientHelper = builder.webClientHelper;
        this.mMsgCfg = builder.mCfg;
        this.isInterceptUnkownScheme = builder.isInterceptUnkownScheme;
        if (builder.schemeHandleType <= 0) {
            schemeHandleType = ASK_USER_OPEN_OTHER_APP;
        } else {
            schemeHandleType = builder.schemeHandleType;
        }
        LogUtils.getInstance().e(TAG, "schemeHandleType:" + schemeHandleType);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.getInstance().e(TAG, "shouldOverrideUrlLoading --->  url:" + url);
        if (AgentWebX5Utils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, String.class) && (super.shouldOverrideUrlLoading(view, url))) {
            return true;
        }
        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return (webClientHelper && hasAlipayLib && isAlipay(view, url));
        }
        if (!webClientHelper) {
            return false;
        }
        //电话 ， 邮箱 ， 短信
        if (handleLinked(url)) {
            return true;
        }
        //Intent scheme
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url);
            return true;
        }
        //微信支付
        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {
            startWeiXinPayActivity(url);
            return true;
        }
        //跳转支付宝
        if (url.startsWith(ALIPAYS_SCHEME) && handleIntentUrl(url)) {
            return true;
        }
        if (queryActivity(url) > 0 && handleOtherScheme(url)) { //打开Scheme 相对应的页面
            LogUtils.getInstance().e(TAG, "intercept OtherAppScheme");
            return true;
        }
        if (isInterceptUnkownScheme) { // 手机里面没有页面能匹配到该链接 ， 也就是无法处理的scheme返回True，拦截下来。
            LogUtils.getInstance().e(TAG, "intercept Scheme : " + url);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    /**
     * 查询能打开该链接的应用
     *
     * @param url 路径
     * @return int
     */
    private int queryActivity(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager mPackageManager = mWeakReference.get().getPackageManager();
            List<ResolveInfo> mResolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return mResolveInfos.size();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private AlertDialog askOpenOtherAppDialog = null;

    private boolean handleOtherScheme(final String url) {
        switch (schemeHandleType) {
            case DERECT_OPEN_OTHER_APP: //直接打开其他App
                handleIntentUrl(url);
                return true;
            case ASK_USER_OPEN_OTHER_APP:  //咨询用户是否打开其他App
                if (mWeakReference.get() != null) {
                    askOpenOtherAppDialog = new AlertDialog
                            .Builder(mWeakReference.get())//
                            .setMessage(String.format(mMsgCfg.getLeaveApp(), AgentWebX5Utils.getApplicationName(mWebView.getContext())))//
                            .setTitle(mMsgCfg.getTitle())
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .setPositiveButton(mMsgCfg.getConfirm(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    handleIntentUrl(url);
                                }
                            })
                            .create();
                }
                askOpenOtherAppDialog.show();
                return true;
            default://默认不打开
                return false;
        }
    }

    /**
     * 处理sms tel email
     *
     * @param url link url
     * @return boolean
     */
    private boolean handleLinked(String url) {
        if (url.startsWith(android.webkit.WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(android.webkit.WebView.SCHEME_MAILTO)
                || url.startsWith(android.webkit.WebView.SCHEME_GEO)) {
            Activity mActivity;
            if ((mActivity = mWeakReference.get()) == null) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mActivity.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 跳转其他应用
     */
    private boolean handleIntentUrl(String intentUrl) {
        try {
            Activity mActivity;
            if ((mActivity = mWeakReference.get()) == null) {
                return false;
            }
            PackageManager packageManager = mActivity.getPackageManager();
            Intent intent = Intent.parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.getInstance().e(TAG, "intentUrl： " + intentUrl + "   package:" + intent.getPackage());
            if (info != null) {  //跳到该应用
                mActivity.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转微信支付
     *
     * @param url weixin://
     */
    private void startWeiXinPayActivity(String url) {
        try {
            if (mWeakReference.get() == null) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mWeakReference.get().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是支付宝
     *
     * @param view WebView
     * @param url  url
     * @return
     */
    private boolean isAlipay(final WebView view, String url) {
        Activity mActivity;
        if ((mActivity = mWeakReference.get()) == null)
            return false;
        final PayTask task = new PayTask(mActivity);
        final String ex = task.fetchOrderInfoFromH5PayUrl(url);
        LogUtils.getInstance().e(TAG, "alipay:" + ex);
        if (!TextUtils.isEmpty(ex)) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                public void run() {
                    final H5PayResultModel result = task.h5Pay(ex, true);
                    if (!TextUtils.isEmpty(result.getReturnUrl())) {
                        AgentWebX5Utils.runInUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.loadUrl(result.getReturnUrl());
                            }
                        });
                    }
                }
            });
            return true;
        }
        return false;
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtils.getInstance().e(TAG, "onPageStarted");
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageStarted(view, url, favicon);
        }
    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtils.getInstance().e(TAG, "onReceivedError：" + description + "  CODE:" + errorCode);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtils.getInstance().e(TAG, "onReceivedError:" + error.toString());

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageFinished(view, url);
        }
        LogUtils.getInstance().e(TAG, "onPageFinished");
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtils.getInstance().e(TAG, "shouldOverrideKeyEvent");
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (AgentWebX5Utils.isOverriedMethod(mWebViewClient, "onScaleChanged", WEBVIEWCLIENTPATH + ".onScaleChanged", WebView.class, float.class, float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }
        LogUtils.getInstance().e(TAG, "onScaleChanged:" + oldScale + "   n:" + newScale);
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public enum OpenOtherPageWays {
        ASK(DefaultWebClient.ASK_USER_OPEN_OTHER_APP);
        int code;

        OpenOtherPageWays(int code) {
            this.code = code;
        }
    }


    public static class Builder {
        private Activity activity;
        private WebViewClient client;
        private WebViewClientCallbackManager manager;
        private boolean webClientHelper;
        private WebView webView;
        private boolean isInterceptUnkownScheme;
        private int schemeHandleType;
        private WebViewClientMsgCfg mCfg;

        public Builder setCfg(WebViewClientMsgCfg cfg) {
            mCfg = cfg;
            return this;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setClient(WebViewClient client) {
            this.client = client;
            return this;
        }

        public Builder setManager(WebViewClientCallbackManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder setWebClientHelper(boolean webClientHelper) {
            this.webClientHelper = webClientHelper;
            return this;
        }

        public Builder setPermissionInterceptor(IPermissionInterceptor permissionInterceptor) {
            return this;
        }

        public Builder setWebView(WebView webView) {
            this.webView = webView;
            return this;
        }

        public Builder setInterceptUnkownScheme(boolean interceptUnkownScheme) {
            this.isInterceptUnkownScheme = interceptUnkownScheme;
            return this;
        }

        public Builder setSchemeHandleType(int schemeHandleType) {
            this.schemeHandleType = schemeHandleType;
            return this;
        }

        public DefaultWebClient build() {
            return new DefaultWebClient(this);
        }
    }
}
