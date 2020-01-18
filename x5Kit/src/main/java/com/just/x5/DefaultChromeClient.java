package com.just.x5;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.permission.IPermissionInterceptor;
import com.just.x5.permission.IPermissionRequestListener;
import com.just.x5.progress.IndicatorController;
import com.just.x5.uploadFile.FileUpLoadChooserImpl;
import com.just.x5.uploadFile.IFileUploadChooser;
import com.just.x5.uploadFile.IFileUploadPop;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.just.x5.video.IVideo;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 默认WebChromeClient处理类
 */
public class DefaultChromeClient extends MiddleWareWebChromeBase implements IFileUploadPop<IFileUploadChooser> {
    private WeakReference<Activity> mActivityWeakReference;
    private AlertDialog promptDialog = null;
    private AlertDialog confirmDialog = null;
    private JsPromptResult pJsResult = null;
    private JsResult cJsResult = null;
    private String TAG = DefaultChromeClient.class.getSimpleName();
    private static final String ChromePath = "com.tencent.smtt.sdk.WebChromeClient";
    private WebChromeClient mWebChromeClient;
    private boolean isWrapper;
    private IFileUploadChooser mIFileUploadChooser;
    private IVideo mIVideo;
    private IPermissionInterceptor mPermissionInterceptor;
    private WebView mWebView;
    private String origin = null;
    private GeolocationPermissionsCallback mCallback = null;
    private static final int FROM_CODE_INTENTION = 0x18;
    private static final int FROM_CODE_INTENTION_LOCATION = FROM_CODE_INTENTION << 2;
    private IndicatorController mIndicatorController;
    private IReceivedTitleCallback receivedTitleCallback;
    private IAgentWebInterface mAgentWebCompatInterface;

    DefaultChromeClient(Activity activity, IndicatorController indicatorController,
                        WebChromeClient chromeClient,
                        @Nullable IVideo iVideo,
                        IPermissionInterceptor permissionInterceptor, WebView webView, IReceivedTitleCallback receivedTitleCallback,
                        IAgentWebInterface mAgentWebCompatInterface) {
        super(chromeClient);
        this.mIndicatorController = indicatorController;
        isWrapper = chromeClient != null;
        this.mWebChromeClient = chromeClient;
        mActivityWeakReference = new WeakReference<>(activity);
        this.mIVideo = iVideo;
        this.mPermissionInterceptor = permissionInterceptor;
        this.mWebView = webView;
        this.receivedTitleCallback = receivedTitleCallback;
        this.mAgentWebCompatInterface = mAgentWebCompatInterface;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (this.mIndicatorController != null) {
            this.mIndicatorController.progress(view, newProgress);
        }
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && mAgentWebCompatInterface != null) {
            mAgentWebCompatInterface.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (receivedTitleCallback != null) {
            receivedTitleCallback.onReceivedTitle(view, title);
        }
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && mAgentWebCompatInterface != null) {
            mAgentWebCompatInterface.onReceivedTitle(view, title);
        }
        if (isWrapper) {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onJsAlert", "public boolean " + ChromePath + ".onJsAlert", WebView.class, String.class, String.class, JsResult.class)) {
            return super.onJsAlert(view, url, message, result);
        }
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            result.cancel();
            return true;
        }
        result.confirm();
        return true;
    }


    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);

    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
        LogUtils.getInstance().e(TAG, "onGeolocationPermissionsHidePrompt");
    }

    //location
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
        LogUtils.getInstance().e(TAG, "onGeolocationPermissionsShowPrompt:" + origin + "   callback:" + callback);
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onGeolocationPermissionsShowPrompt", "public void " + ChromePath + ".onGeolocationPermissionsShowPrompt", String.class, GeolocationPermissionsCallback.class)) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            return;
        }
        onGeolocationPermissionsShowPromptInternal(origin, callback);
    }

    private void onGeolocationPermissionsShowPromptInternal(String origin, GeolocationPermissionsCallback callback) {
        if (mPermissionInterceptor != null) {
            if (mPermissionInterceptor.intercept(this.mWebView.getUrl(), PermissionConstant.LOCATION, "location")) {
                callback.invoke(origin, false, false);
                return;
            }
        }
        Activity mActivity = mActivityWeakReference.get();
        if (mActivity == null) {
            callback.invoke(origin, false, false);
            return;
        }
        List<String> deniedPermissions;
        if ((deniedPermissions = AgentWebX5Utils.getDeniedPermissions(mActivity, PermissionConstant.LOCATION)).isEmpty()) {
            callback.invoke(origin, true, false);
        } else {
            ActionActivity.setPermissionListener(mPermissionListener);
            this.mCallback = callback;
            this.origin = origin;
            ActionActivity.start(mActivity, AgentWebX5Config.ACTION_PERMISSION, FROM_CODE_INTENTION_LOCATION, deniedPermissions.toArray(new String[]{}));
        }
    }

    private IPermissionRequestListener mPermissionListener = new IPermissionRequestListener() {
        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
            if (extras.getInt(AgentWebX5Config.KEY_FROM_INTENTION) == FROM_CODE_INTENTION_LOCATION) {
                boolean t = true;
                for (int p : grantResults) {
                    if (p != PackageManager.PERMISSION_GRANTED) {
                        t = false;
                        break;
                    }
                }
                if (mCallback != null) {
                    if (t) {
                        mCallback.invoke(origin, true, false);
                    } else {
                        mCallback.invoke(origin, false, false);
                    }
                    mCallback = null;
                    origin = null;
                }
            }
        }
    };

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onJsPrompt", "public boolean " + ChromePath + ".onJsPrompt", WebView.class, String.class, String.class, String.class, JsPromptResult.class)) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
        if (AgentWebX5Config.WEBVIEW_TYPE == AgentWebX5Config.WEBVIEW_AGENTWEB_SAFE_TYPE && mAgentWebCompatInterface != null) {
            LogUtils.getInstance().e(TAG, "IAgentWebCompatInterface():" + mAgentWebCompatInterface);
            if (mAgentWebCompatInterface.onJsPrompt(view, url, message, defaultValue, result)) {
                return true;
            }
        }
        showJsPrompt(message, result, defaultValue);
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        LogUtils.getInstance().e(TAG, message);
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onJsConfirm", "public boolean " + ChromePath + ".onJsConfirm", WebView.class, String.class, String.class, JsResult.class)) {
            return super.onJsConfirm(view, url, message, result);
        }
        showJsConfirm(message, result);
        return true;
    }


    private void toDismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    private void showJsConfirm(String message, final JsResult result) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            result.cancel();
            return;
        }
        if (confirmDialog == null)
            confirmDialog = new AlertDialog.Builder(mActivity)//
                    .setMessage(message)//
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            toCancelJsresult(cJsResult);
                        }
                    })//
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(confirmDialog);
                            if (DefaultChromeClient.this.cJsResult != null)
                                DefaultChromeClient.this.cJsResult.confirm();

                        }
                    }).create();
        this.cJsResult = result;
        confirmDialog.show();
    }

    private void toCancelJsresult(JsResult result) {
        if (result != null) {
            result.cancel();
        }
    }

    private void showJsPrompt(String message, final JsPromptResult js, String defaultstr) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            js.cancel();
            return;
        }
        if (promptDialog == null) {
            final EditText et = new EditText(mActivity);
            et.setText(defaultstr);
            promptDialog = new AlertDialog.Builder(mActivity)//
                    .setView(et)//
                    .setTitle(message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);
                            toCancelJsresult(pJsResult);
                        }
                    })//
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDismissDialog(promptDialog);

                            if (DefaultChromeClient.this.pJsResult != null)
                                DefaultChromeClient.this.pJsResult.confirm(et.getText().toString());

                        }
                    }).create();
        }
        this.pJsResult = js;
        promptDialog.show();
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onExceededDatabaseQuota", ChromePath + ".onExceededDatabaseQuota", String.class, String.class, long.class, long.class, long.class, WebStorage.QuotaUpdater.class)) {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            return;
        }
        quotaUpdater.updateQuota(totalQuota * 2);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onReachedMaxAppCacheSize", ChromePath + ".onReachedMaxAppCacheSize", long.class, long.class, WebStorage.QuotaUpdater.class)) {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            return;
        }
        quotaUpdater.updateQuota(requiredStorage * 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        LogUtils.getInstance().e(TAG, "openFileChooser>=5.0");
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onShowFileChooser", ChromePath + ".onShowFileChooser", WebView.class, ValueCallback.class, WebChromeClient.FileChooserParams.class)) {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
        openFileChooserAboveLOLLIPOP(webView, filePathCallback, fileChooserParams);
        return true;
    }

    /**
     * 打开选择文件
     */
    private void openFileChooserAboveLOLLIPOP(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            filePathCallback.onReceiveValue(new Uri[]{});
            return;
        }
        //创建文件选择器
        mIFileUploadChooser = new FileUpLoadChooserImpl.Builder()
                .setWebView(webView)
                .setActivity(mActivity)
                .setUriValueCallbacks(filePathCallback)
                .setFileChooserParams(fileChooserParams)
                .build();
        mIFileUploadChooser.openFileChooser();
    }

    @Override
    public IFileUploadChooser pop() {
        LogUtils.getInstance().e(TAG, "pop:" + mIFileUploadChooser);
        return mIFileUploadChooser;
    }

    // Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        /*believe me , i never want to do this */
        LogUtils.getInstance().e(TAG, "openFileChooser>=4.1");
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "openFileChooser", ChromePath + ".openFileChooser", ValueCallback.class, String.class, String.class)) {
            super.openFileChooser(uploadFile, acceptType, capture);
            return;
        }
        createAndOpenCommonFileLoader(uploadFile);
    }

    //  Android  >= 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType) {
        LogUtils.getInstance().e(TAG, "openFileChooser>3.0");
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "openFileChooser", ChromePath + ".openFileChooser", ValueCallback.class, String.class)) {
            super.openFileChooser(valueCallback, acceptType);
            return;
        }
        createAndOpenCommonFileLoader(valueCallback);
    }

    //  Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "openFileChooser", ChromePath + ".openFileChooser", ValueCallback.class)) {
            super.openFileChooser(valueCallback);
            return;
        }
        LogUtils.getInstance().e(TAG, "openFileChooser<3.0");
        createAndOpenCommonFileLoader(valueCallback);
    }


    private void createAndOpenCommonFileLoader(ValueCallback<Uri> valueCallback) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()) {
            this.mIFileUploadChooser = new FileUpLoadChooserImpl.Builder()
                    .setWebView(this.mWebView)
                    .setActivity(mActivity)
                    .setUriValueCallback(valueCallback)
                    .build();
            this.mIFileUploadChooser.openFileChooser();
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        super.onConsoleMessage(consoleMessage);
        LogUtils.getInstance().e(TAG, "consoleMessage:" + consoleMessage.message() + "  lineNumber:" + consoleMessage.lineNumber());
        return true;
    }


    @Override
    public void onHideCustomView() {
        if (AgentWebX5Utils.isOverriedMethod(mWebChromeClient, "onHideCustomView", ChromePath + ".onHideCustomView")) {
            LogUtils.getInstance().e(TAG, "onHide:" + true);
            super.onHideCustomView();
            return;
        }
        LogUtils.getInstance().e(TAG, "Video:" + mIVideo);
        if (mIVideo != null) {
            mIVideo.onHideCustomView();
        }
    }

}
