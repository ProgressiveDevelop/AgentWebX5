package com.webx5.controller.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.permission.kit.PermissionActivity;
import com.webx5.FragmentKeyDown;
import com.webx5.KeyCanstans;
import com.webx5.R;
import com.webx5.controller.fragment.AgentWebX5Fragment;
import com.webx5.controller.fragment.JsAgentWebFragment;
import com.webx5.controller.fragment.customprogress.CustomIndicatorFragment;
import com.webx5.controller.fragment.customsetting.CustomSettingsFragment;

public class CommonActivity extends PermissionActivity {
    private FragmentManager mFragmentManager;
    private AgentWebX5Fragment mAgentWebX5Fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        int key = getIntent().getIntExtra(getResources().getString(R.string.key_type), -1);
        mFragmentManager = this.getSupportFragmentManager();
        openFragment(key);
    }

    private void openFragment(int key) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Bundle mBundle = new Bundle();
        mAgentWebX5Fragment = new AgentWebX5Fragment();
        switch (key) {
            case 0:
                mBundle.putString(KeyCanstans.KEY_URL, "https://m.vip.com");
                break;
            case 1:
                 mBundle.putString(KeyCanstans.KEY_URL, "https://h5.m.jd.com/active/download/download.html");
                break;
            case 2:
                mBundle.putString(KeyCanstans.KEY_URL, "file:///android_asset/upload_file/uploadfile.html");
                break;
            case 3:
                 mBundle.putString(KeyCanstans.KEY_URL, "file:///android_asset/upload_file/jsuploadfile.html");
                break;
            case 4:
                mAgentWebX5Fragment = new JsAgentWebFragment();
                mBundle.putString(KeyCanstans.KEY_URL, "file:///android_asset/js_interaction/hello.html");
                break;
            case 5:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment);
                break;
            case 6:
                mAgentWebX5Fragment = new CustomIndicatorFragment();
                mBundle.putString(KeyCanstans.KEY_URL, "http://www.taobao.com");
                break;
            case 7:
                mAgentWebX5Fragment = new CustomSettingsFragment();
                mBundle.putString(KeyCanstans.KEY_URL, "http://www.wandoujia.com/apps");
                break;
            //电话、短信、邮件
            case 8:
                mBundle.putString(KeyCanstans.KEY_URL, "file:///android_asset/sms/sms.html");
                break;
        }
        mAgentWebX5Fragment.setArguments(mBundle);
        ft.add(R.id.container_framelayout, mAgentWebX5Fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAgentWebX5Fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWebX5Fragment != null) {
            if (((FragmentKeyDown) mAgentWebX5Fragment).onFragmentKeyDown(keyCode, event))
                return true;
            else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
