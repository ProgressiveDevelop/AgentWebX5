package com.just.agentwebX5.progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.just.agentwebX5.util.AgentWebX5Utils;
import com.just.agentwebX5.util.LogUtils;

/**
 * 网页加载进度条
 */

public class WebProgress extends BaseIndicatorView implements IBaseProgressSpec {
    private int mColor;
    private Paint mPaint;
    private ValueAnimator mValueAnimator;
    private int targetWidth = 0;
    private float currentProgress = 0f;
    private int state = 0;
    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;
    private float target = 0f;

    public WebProgress(Context context) {
        this(context, null);
    }

    public WebProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mColor = Color.parseColor("#1aad19");
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        targetWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public void setColor(String color) {
        this.setColor(Color.parseColor(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode == MeasureSpec.AT_MOST) {
            w = w <= getContext().getResources().getDisplayMetrics().widthPixels ? w : getContext().getResources().getDisplayMetrics().widthPixels;
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = AgentWebX5Utils.dp2px(this.getContext(), 2);
        }
        this.setMeasuredDimension(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, currentProgress / 100 * (float) this.getWidth(), this.getHeight(), mPaint);
    }

    @Override
    public void show() {
        if (getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
            currentProgress = 0f;
            startAnim(-1, true);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.targetWidth = getMeasuredWidth();
        LogUtils.getInstance().e("WebProgress", "" + targetWidth);
    }

    @Override
    public void hide() {
        state = FINISH;
    }

    @Override
    public void reset() {
        currentProgress = 0;
        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));
    }

    public void setProgress(float progress) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        if (progress < 90f) {
            return;
        }
        startAnim(progress, false);
    }

    private void startAnim(float value, boolean isAuto) {
        if (target == value) {
            return;
        }
        if (value < currentProgress && value != -1) {
            return;
        }
        float v = (isAuto) ? 90f : value;
        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.cancel();
        }
        currentProgress = currentProgress == 0f ? 0.00000001f : currentProgress;
        mValueAnimator = ValueAnimator.ofFloat(currentProgress, v);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        long duration = (long) Math.abs((v / 100f * targetWidth) - (currentProgress / 100f * targetWidth));
        /*默认每个像素8毫秒*/
        mValueAnimator.setDuration(isAuto ? duration * 4 : (long) (duration * weightDuration(v, currentProgress)));
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WebProgress.this.currentProgress = (float) animation.getAnimatedValue();
                WebProgress.this.invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (state == FINISH && currentProgress == 100f) {
                    setVisibility(GONE);
                    currentProgress = 0f;
                }
                state = UN_START;
            }
        });
        mValueAnimator.start();
        state = STARTED;
        target = v;
    }

    private float weightDuration(float value, float current) {
        if (value > 70 && value < 85) {
            return 1.5f;
        } else if (value > 85) {
            return 0.8f;
        }
        float poor = Math.abs(value - current);
        if (poor < 25) {
            return 4f;
        } else if (poor > 25 && poor < 50) {
            return 3f;
        } else {
            return 2f;
        }
    }

    @Override
    public FrameLayout.LayoutParams offerLayoutParams() {
        return new FrameLayout.LayoutParams(-1, AgentWebX5Utils.dp2px(getContext(), 2));
    }
}