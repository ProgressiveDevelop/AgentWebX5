package com.just.x5;

/**
 * WebView 页面加载
 */

public interface ILoader {
    /**
     * 加载 url
     *
     * @param url 路径
     */
    void loadUrl(String url);


    /**
     * 加载 data 网页
     *
     * @param data     源码
     * @param mimeType 源码类型
     * @param encoding 编码
     */
    void loadData(String data, String mimeType, String encoding);

    /**
     * 加载 data 网页并指定基本路径
     *
     * @param baseUrl    基本路径
     * @param data       源码
     * @param mimeType   源码类型
     * @param encoding   编码
     * @param historyUrl 历史路径
     */
    void loadDataWithBaseURL(String baseUrl, String data,
                             String mimeType, String encoding, String historyUrl);

    /**
     * 重新加载
     */
    void reload();

    /**
     * 停止加载
     */
    void stopLoading();


}
