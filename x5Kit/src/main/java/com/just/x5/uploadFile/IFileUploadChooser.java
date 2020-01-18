package com.just.x5.uploadFile;

import android.content.Intent;

/**
 * 选择文件监听接口
 */

public interface IFileUploadChooser {
    /**
     * 选择文件
     */
    void openFileChooser();

    /**
     * 返回结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        intent
     */
    void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data);
}
