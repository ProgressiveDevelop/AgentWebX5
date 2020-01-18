package com.webx5.controller.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.ValueCallback;
import com.webx5.AndroidInterface;
import com.webx5.R;

import org.json.JSONObject;

/**
 * Js 与Android 通信
 */

public class JsAgentWebFragment extends AgentWebX5Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) view;
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_js, mLinearLayout, true);
        super.onViewCreated(view, savedInstanceState);
        if (mAgentWebX5 != null) {
            mAgentWebX5.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWebX5, this.getActivity()));
        }
        view.findViewById(R.id.one).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.two).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.three).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.four).setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.one:
                    // mAgentWebX5.getJsEntraceAccess().quickCallJs("callByAndroid");
                    mAgentWebX5.getWebCreator().get().loadUrl("javascript:callByAndroid()");
                    break;
                case R.id.two:
                    mAgentWebX5.getJsAccess().callJs("callByAndroidParam", "Hello ! Agentweb");
                    break;
                case R.id.three:
                    mAgentWebX5.getJsAccess().callJs("callByAndroidMoreParams", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogUtils.getInstance().e(getClass().getSimpleName(), "value:" + value);
                        }
                    }, getJson(), "say:", " Hello! Agentweb");
                    break;
                case R.id.four:
                    mAgentWebX5.getJsAccess().callJs("callByAndroidInteraction", "你好Js");
                    break;
            }

        }
    };

    private String getJson() {
        String result = null;
        try {
            JSONObject mJSONObject = new JSONObject();
            mJSONObject.put("id", 1);
            mJSONObject.put("name", "Agentweb");
            mJSONObject.put("age", 18);
            result = mJSONObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
