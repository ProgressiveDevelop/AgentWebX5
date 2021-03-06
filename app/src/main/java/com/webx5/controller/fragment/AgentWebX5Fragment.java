package com.webx5.controller.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.just.x5.AgentWebX5;
import com.just.x5.DefaultWebClient;
import com.just.x5.IReceivedTitleCallback;
import com.just.x5.IWebSettings;
import com.just.x5.SecurityType;
import com.just.x5.WebDefaultSettingsImpl;
import com.just.x5.downFile.DownLoadResultListener;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.webx5.FragmentKeyDown;
import com.webx5.KeyCanstans;
import com.webx5.R;

import java.util.Objects;

import static com.webx5.R.id.iv_back;


/**
 * 基础AgentWebX5Fragment
 */

public class AgentWebX5Fragment extends Fragment implements FragmentKeyDown {
    private ImageView mBackImageView;
    private View mLineView;
    private TextView mTitleTextView;
    protected AgentWebX5 mAgentWebX5;
    private static final String TAG = AgentWebX5Fragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //填充布局
        return inflater.inflate(R.layout.fragment_agentweb, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化 AgentWebX5
        mAgentWebX5 = AgentWebX5.with(this)//
                //设置父布局
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                //设置指示器颜色和高度
                .setIndicatorColorWithHeight(-1, 2)//
                //设置WebSetting
                .setWebSettings(getSettings())
                //设置WebViewClient
                //.setWebViewClient(mWebViewClient)
                //设置WebChromeClient
                //.setWebChromeClient(mWebChromeClient)
                //设置接收标题回调
                .setReceivedTitleCallback(mCallback)
                //设置通知栏图标
                .setNotifyIcon(R.mipmap.download)
                //打开其他页面方式
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                //.setWebLayout(new WebLayout(getActivity()))
                .interceptUnkownScheme()
                .openParallelDownload()
                .setSecurityType(SecurityType.strict)
                .addDownLoadResultListener(mDownLoadResultListener)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());
        initView(view);
    }

    /**
     * 获取网页
     *
     * @return 网页路径
     */
    protected String getUrl() {
        String target;
        if (TextUtils.isEmpty(target = this.getArguments() != null ? this.getArguments().getString(KeyCanstans.KEY_URL) : null)) {
            target = "https://parse.xymov.net/";
        }
        return target;
    }

    protected IReceivedTitleCallback mCallback = new IReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mTitleTextView != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 10)
                    title = title.substring(0, 10) + "...";
                mTitleTextView.setText(title);
            }
        }
    };
    /**
     * 下载结果监听
     */
    protected DownLoadResultListener mDownLoadResultListener = new DownLoadResultListener() {
        @Override
        public void success(String path) {
            LogUtils.getInstance().e(TAG, "path:" + path);
        }

        @Override
        public void error(String path, String resUrl, String cause, Throwable e) {
            LogUtils.getInstance().e(TAG, "path:" + path + "  url:" + resUrl + "  couse:" + cause + "  Throwable:" + e);
        }
    };

    protected IWebSettings getSettings() {
        return WebDefaultSettingsImpl.getInstance();
    }

    //    private WebChromeClient mWebChromeClient = new WebChromeClient() {
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//        }
//    };
    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            LogUtils.getInstance().e(TAG, "mWebViewClient shouldOverrideUrlLoading:" + url);
            //intent:// scheme的处理 如果返回false ， 则交给 DefaultWebClient 处理 ， 默认会打开该Activity  ， 如果Activity不存在则跳到应用市场上去.  true 表示拦截
            //例如优酷视频播放 ，intent://play?vid=XODEzMjU1MTI4&refer=&tuid=&ua=Mozilla%2F5.0%20(Linux%3B%20Android%207.0%3B%20SM-G9300%20Build%2FNRD90M%3B%20wv)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Version%2F4.0%20Chrome%2F58.0.3029.83%20Mobile%20Safari%2F537.36&source=exclusive-pageload&cookieid=14971464739049EJXvh|Z6i1re#Intent;scheme=youku;package=com.youku.phone;end;
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            return url.startsWith("intent://") || url.startsWith("youku");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtils.getInstance().e(TAG, "url:" + url + " onPageStarted  url:" + getUrl());
            if (url.equals(getUrl())) {
                pageNavigator(View.GONE);
            } else {
                pageNavigator(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtils.getInstance().e(TAG, "onPageFinished  url:" + url);
        }
    };

    protected void initView(View view) {
        mBackImageView = view.findViewById(iv_back);
        mBackImageView.setOnClickListener(mOnClickListener);

        mLineView = view.findViewById(R.id.view_line);
        mTitleTextView = view.findViewById(R.id.toolbar_title);

        view.findViewById(R.id.iv_finish).setOnClickListener(mOnClickListener);

        pageNavigator(View.GONE);
    }

    /**
     * 设置返回按钮和分割线
     *
     * @param tag 显示|隐藏
     */
    private void pageNavigator(int tag) {
        mBackImageView.setVisibility(tag);
        mLineView.setVisibility(tag);
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case iv_back:
                    if (!mAgentWebX5.back()) {
                        Objects.requireNonNull(getActivity()).finish();
                    }
                    break;
                case R.id.iv_finish:
                    Objects.requireNonNull(getActivity()).finish();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.getInstance().e(TAG, "onActivityResult -- >callback:" + requestCode);
        //选择文件返回结果
        mAgentWebX5.uploadFileResult(requestCode, resultCode, data);
    }

    /**
     * 按键拦截事件
     *
     * @param keyCode 按键
     * @param event   触发事件
     * @return 是否处理
     */
    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        return mAgentWebX5.onKeyDown(keyCode, event);
    }

    /**
     * 生命周期方法
     */
    @Override
    public void onResume() {
        mAgentWebX5.getWebLifeCycle().onResume();
        super.onResume();
    }

    /**
     * 生命周期方法
     */
    @Override
    public void onPause() {
        mAgentWebX5.getWebLifeCycle().onPause();
        super.onPause();
    }

    /**
     * 生命周期方法
     */
    @Override
    public void onDestroyView() {
        mAgentWebX5.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}
