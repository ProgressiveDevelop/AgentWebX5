package com.just.x5.uploadFile;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.just.x5.AgentWebX5;
import com.just.x5.js.IJsChannelCallback;
import com.just.x5.util.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 上传文件接口实现
 */

public class FileUploadPopImpl implements IFileUploadPop<IFileUploadChooser> {
    private WeakReference<AgentWebX5> mReference;
    private WeakReference<Activity> mActivityWeakReference;
    private IFileUploadChooser mIFileUploadChooser;

    public FileUploadPopImpl(AgentWebX5 agentWebX5, Activity activity) {
        this.mReference = new WeakReference<>(agentWebX5);
        mActivityWeakReference = new WeakReference<>(activity);
    }

    /**
     * js 上传文件接口
     */
    @JavascriptInterface
    public void uploadFile() {
        LogUtils.getInstance().e(getClass().getSimpleName(), "upload file");
        if (mActivityWeakReference.get() != null && mReference.get() != null) {
            mIFileUploadChooser = new FileUpLoadChooserImpl.Builder()
                    .setActivity(mActivityWeakReference.get())
                    //js 回调
                    .setJsChannelCallback(new IJsChannelCallback() {
                        @Override
                        public void call(String value) {
                            if (mReference.get() != null)
                                mReference.get().getJsAccess().callJs("uploadFileResult", value);
                        }
                    })
                    .setWebView(mReference.get().getWebCreator().get())
                    .build();
            mIFileUploadChooser.openFileChooser();
        }
    }

    @Override
    public IFileUploadChooser pop() {
        IFileUploadChooser mIFileUploadChooser = this.mIFileUploadChooser;
        this.mIFileUploadChooser = null;
        return mIFileUploadChooser;
    }
}
