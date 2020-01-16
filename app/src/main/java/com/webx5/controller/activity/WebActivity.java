package com.webx5.controller.activity;

public class WebActivity extends BaseWebActivity {

    @Override
    public String getUrl() {
        return getIntent().getStringExtra("url");
    }
}
