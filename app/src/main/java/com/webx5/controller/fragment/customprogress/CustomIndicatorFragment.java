package com.webx5.controller.fragment.customprogress;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.just.x5.AgentWebX5;
import com.just.x5.SecurityType;
import com.just.x5.WebDefaultSettingsImpl;
import com.webx5.R;
import com.webx5.controller.fragment.AgentWebX5Fragment;

/**
 * 自定义进度条演示
 */
public class CustomIndicatorFragment extends AgentWebX5Fragment {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        CommonIndicator mCommonIndicator = new CommonIndicator(this.getActivity());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(50, 50, 50, 50);
        ProgressBar mProgressBar = new ProgressBar(this.getActivity());
        mProgressBar.setBackground(this.getResources().getDrawable(R.drawable.indicator_shape));
        mCommonIndicator.addView(mProgressBar, lp);

        this.mAgentWebX5 = AgentWebX5.with(this)//
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setCustomIndicator(mCommonIndicator)
                .setWebSettings(WebDefaultSettingsImpl.getInstance())//
                .setWebViewClient(mWebViewClient)
                .setReceivedTitleCallback(mCallback)
                .setSecurityType(SecurityType.strict)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());
        initView(view);
    }
}
