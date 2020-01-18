package com.just.x5.downFile;

/**
 * 下载监听器
 */
public interface DownLoadResultListener {
    /**
     * 下载成功
     *
     * @param path 文件路径
     */
    void success(String path);

    /**
     * 下载出错
     *
     * @param path   文件路径
     * @param resUrl 资源路径
     * @param cause  错误原因
     * @param e      异常
     */
    void error(String path, String resUrl, String cause, Throwable e);

}
