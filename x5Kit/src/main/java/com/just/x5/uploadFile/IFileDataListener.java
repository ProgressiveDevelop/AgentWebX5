package com.just.x5.uploadFile;

import android.content.Intent;

/**
 * 文件选择结果回调接口
 */
public interface IFileDataListener {
    /**
     * 文件选择结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据Intent
     */
    void onFileDataResult(int requestCode, int resultCode, Intent data);
}
