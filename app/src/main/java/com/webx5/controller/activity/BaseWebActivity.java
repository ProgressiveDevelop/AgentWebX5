package com.webx5.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.just.x5.AgentWebX5;
import com.just.x5.DefaultWebClient;
import com.just.x5.IReceivedTitleCallback;
import com.just.x5.SecurityType;
import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.WebView;
import com.webx5.R;
import com.webx5.ui.view.WebLayout;

public abstract class BaseWebActivity extends AppCompatActivity {
    protected AgentWebX5 mAgentWebX5;
    private TextView mTitleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar mToolbar = this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LinearLayout mLinearLayout = findViewById(R.id.container);
        mTitleTextView = findViewById(R.id.toolbar_title);

        long p = System.currentTimeMillis();

        mAgentWebX5 = AgentWebX5.with(this)//
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setReceivedTitleCallback(new IReceivedTitleCallback() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        if (mTitleTextView != null) {
                            mTitleTextView.setText(title);
                        }
                    }
                })
//                .setWebChromeClient(mWebChromeClient)
//                .setWebViewClient(mWebViewClient)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownScheme()
                .setSecutityType(SecurityType.strict)
                //反弹布局
                .setWebLayout(new WebLayout(this))
                .createAgentWeb()
                .ready()
                .go(getUrl());
        long n = System.currentTimeMillis();
        LogUtils.getInstance().e("Info", "init used time:" + (n - p));
    }


    public abstract String getUrl();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.getInstance().e("BaseWebActivity", "result:" + requestCode + " result:" + resultCode);
        mAgentWebX5.uploadFileResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mAgentWebX5.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAgentWebX5.getWebLifeCycle().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAgentWebX5.getWebLifeCycle().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWebX5.getWebLifeCycle().onDestroy();
    }
}
