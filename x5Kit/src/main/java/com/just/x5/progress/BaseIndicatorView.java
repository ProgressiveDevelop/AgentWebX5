package com.just.x5.progress;

import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.just.x5.ILayoutParamsOffer;

/**
 * 进度条基类
 */

public abstract class BaseIndicatorView extends FrameLayout implements IBaseProgressSpec, ILayoutParamsOffer {
    public BaseIndicatorView(Context context) {
        super(context);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void reset() {

    }

    @Override
    public void setProgress(int newProgress) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }


    @Override
    public LayoutParams offerLayoutParams() {
        return null;
    }
}
