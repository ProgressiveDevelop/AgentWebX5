package com.webx5.controller.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.permission.kit.PermissionActivity;
import com.webx5.FragmentKeyDown;
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
        Bundle mBundle;
        switch (key) {
            case 0:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "https://m.vip.com");
                break;
            case 1:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "https://h5.m.jd.com/active/download/download.html");
                break;
            case 2:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "file:///android_asset/upload_file/uploadfile.html");
                break;
            case 3:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "file:///android_asset/upload_file/jsuploadfile.html");
                break;
            case 4:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = JsAgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "file:///android_asset/js_interaction/hello.html");
                break;

            case 5:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "https://m.youku.com/alipay_video/id_XNDUxMDg3MTMzMg==.html");
                break;
            case 6:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = CustomIndicatorFragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "http://www.taobao.com");
                break;
            case 7:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = CustomSettingsFragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "http://www.wandoujia.com/apps");
                break;
            //电话、短信、邮件
            case 8:
                ft.add(R.id.container_framelayout, mAgentWebX5Fragment = AgentWebX5Fragment.getInstance(mBundle = new Bundle()), AgentWebX5Fragment.class.getName());
                mBundle.putString(AgentWebX5Fragment.URL_KEY, "file:///android_asset/sms/sms.html");
                break;
        }
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAgentWebX5Fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AgentWebX5Fragment mAgentWebX5Fragment = this.mAgentWebX5Fragment;
        if (mAgentWebX5Fragment != null) {
            if (((FragmentKeyDown) mAgentWebX5Fragment).onFragmentKeyDown(keyCode, event))
                return true;
            else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
