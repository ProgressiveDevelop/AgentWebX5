package com.webx5;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.just.x5.AgentWebX5;
import com.just.x5.util.LogUtils;


public class AndroidInterface {
    private Context context;
    private Handler deliver = new Handler(Looper.getMainLooper());

    public AndroidInterface(AgentWebX5 agent, Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void callAndroid(final String msg) {
        LogUtils.getInstance().e("AndroidInterface", "Thread:" + Thread.currentThread());
        deliver.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.getInstance().e("AndroidInterface", "main Thread:" + Thread.currentThread());
                Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
