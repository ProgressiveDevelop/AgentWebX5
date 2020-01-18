package com.just.x5.uploadFile;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.just.x5.ActionActivity;
import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.js.IJsChannelCallback;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.permission.kit.OnPermissionListener;
import com.permission.kit.PermissionKit;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.util.Queue;

/**
 * 选择文件接口实现类
 */
public class FileUpLoadChooserImpl implements IFileUploadChooser {
    private Activity mActivity;
    private ValueCallback<Uri> mUriValueCallback;
    private ValueCallback<Uri[]> mUriValueCallbacks;
    private boolean isLOLLIPOP;
    private boolean jsChannel;
    private IJsChannelCallback mJsChannelCallback;
    private AlertDialog mAlertDialog;
    private static final String TAG = FileUpLoadChooserImpl.class.getSimpleName();
    private boolean cameraState = false;

    public FileUpLoadChooserImpl(Builder builder) {
        this.mActivity = builder.mActivity;
        this.mUriValueCallback = builder.mUriValueCallback;
        this.mUriValueCallbacks = builder.mUriValueCallbacks;
        this.isLOLLIPOP = builder.isLOLLIPOP;
        this.jsChannel = builder.jsChannel;
        this.mJsChannelCallback = builder.mJsChannelCallback;
    }

    @Override
    public void openFileChooser() {
        if (!AgentWebX5Utils.isUIThread()) {
            AgentWebX5Utils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    openFileChooser();
                }
            });
        } else {
            checkPermission();
        }
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        //请求权限
        PermissionKit.getInstance().requestPermission(mActivity, 1000, new OnPermissionListener() {
            @Override
            public void onSuccess(int requestCode, String... permissions) {
                openFileChooserInternal();
            }

            @Override
            public void onFail(int requestCode, String... permissions) {
                //授权失败后再次操作
                PermissionKit.getInstance().guideSetting(mActivity, true, requestCode, null, permissions);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    /**
     * 打开上传选择对话框
     */
    private void openFileChooserInternal() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mActivity)//
                    .setSingleChoiceItems(AgentWebX5Config.MEDIAS, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                            openChooserAction(which);
                        }
                    })
                    .create();
        }
        mAlertDialog.show();
    }

    /**
     * 选择文件文件
     */
    private void openChooserAction(int which) {
        ActionActivity.setFileDataListener(new IFileDataListener() {
            @Override
            public void onFileDataResult(int requestCode, int resultCode, Intent data) {
                LogUtils.getInstance().e(TAG, "openFileChooserAction request:" + requestCode + "  resultCode:" + resultCode);
                fetchFilePathFromIntent(requestCode, resultCode, data);
            }
        });
        cameraState = which != 1;
        ActionActivity.start(mActivity, which == 1 ? AgentWebX5Config.ACTION_FILE : AgentWebX5Config.ACTION_CAMERA, 0);
    }

    @Override
    public void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data) {
        LogUtils.getInstance().e(TAG, "request:" + requestCode + "  result:" + resultCode + "  data:" + data);
        if (resultCode == Activity.RESULT_OK && AgentWebX5Config.REQUEST_CODE == requestCode) {
            if (isLOLLIPOP) {
                Uri[] uris = cameraState ? new Uri[]{data.getParcelableExtra(AgentWebX5Config.KEY_URI)} : processData(data);
                if (mUriValueCallbacks != null) {
                    mUriValueCallbacks.onReceiveValue(uris == null ? new Uri[]{} : uris);
                }
            } else if (jsChannel) {
                //js
                LogUtils.getInstance().e(TAG, "jsChannel:" + jsChannel);
                final Uri[] uris = cameraState ? new Uri[]{data.getParcelableExtra(AgentWebX5Config.KEY_URI)} : processData(data);
                final String[] paths;
                if (uris == null || uris.length == 0 || (paths = AgentWebX5Utils.uriToPath(mActivity, uris)) == null || paths.length == 0) {
                    mJsChannelCallback.call(null);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Queue<FilePO> mQueue = AgentWebX5Utils.convertFile(paths);
                            String result = AgentWebX5Utils.convertFileParcelObjectsToJson(mQueue);
                            LogUtils.getInstance().e(TAG, "result:" + result);
                            if (mJsChannelCallback != null) {
                                mJsChannelCallback.call(result);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                LogUtils.getInstance().e(TAG, "fetchFilePathFromIntent:" + cameraState);
                if (cameraState && mUriValueCallback != null) {
                    mUriValueCallback.onReceiveValue((Uri) data.getParcelableExtra(AgentWebX5Config.KEY_URI));
                } else {
                    Uri mUri = data == null ? null : data.getData();
                    LogUtils.getInstance().e(TAG, "handleBelowLData  -- >uri:" + mUri + "  mUriValueCallback:" + mUriValueCallback);
                    if (mUriValueCallback != null) {
                        mUriValueCallback.onReceiveValue(mUri);
                    }
                }
            }
        }
    }

    private Uri[] processData(Intent data) {
        String target = data.getDataString();
        if (!TextUtils.isEmpty(target)) {
            LogUtils.getInstance().e(TAG, "processData：" + target);
            return new Uri[]{Uri.parse(target)};
        } else {
            return null;
        }
    }

    /**
     * 构造器
     */
    public static final class Builder {
        private Activity mActivity;
        private ValueCallback<Uri> mUriValueCallback;
        private ValueCallback<Uri[]> mUriValueCallbacks;
        private boolean isLOLLIPOP = false;
        private IJsChannelCallback mJsChannelCallback;
        private boolean jsChannel = false;

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setUriValueCallback(ValueCallback<Uri> uriValueCallback) {
            mUriValueCallback = uriValueCallback;
            isLOLLIPOP = false;
            jsChannel = false;
            mUriValueCallbacks = null;
            mJsChannelCallback = null;
            return this;
        }

        /**
         * >=5.0 调用
         *
         * @param uriValueCallbacks 回调
         */
        public Builder setUriValueCallbacks(ValueCallback<Uri[]> uriValueCallbacks) {
            mUriValueCallbacks = uriValueCallbacks;
            isLOLLIPOP = true;
            jsChannel = false;
            mUriValueCallback = null;
            mJsChannelCallback = null;
            return this;
        }

        public Builder setJsChannelCallback(IJsChannelCallback jsChannelCallback) {
            mJsChannelCallback = jsChannelCallback;
            jsChannel = true;
            mUriValueCallback = null;
            mUriValueCallbacks = null;
            return this;
        }

        public Builder setFileChooserParams(WebChromeClient.FileChooserParams fileChooserParams) {
            return this;
        }

        public Builder setWebView(WebView webView) {
            return this;
        }

        public FileUpLoadChooserImpl build() {
            return new FileUpLoadChooserImpl(this);
        }
    }

}
