package com.just.x5.progress;


import com.tencent.smtt.sdk.WebView;

/**
 *
 */

public class IndicatorHandlerImpl implements IndicatorController, IProgressLifeCyclic {
    private IBaseProgressSpec baseProgressSpec;

    @Override
    public void progress(WebView v, int newProgress) {
        if (newProgress == 0) {
            reset();
        } else if (newProgress > 0 && newProgress <= 10) {
            showProgressBar();
        } else if (newProgress > 10 && newProgress < 95) {
            setProgressBar(newProgress);
        } else {
            setProgressBar(newProgress);
            finish();
        }
    }

    @Override
    public IBaseProgressSpec offerIndicator() {
        return this.baseProgressSpec;
    }

    public void reset() {
        if (baseProgressSpec != null) {
            baseProgressSpec.reset();
        }
    }

    public void finish() {
        if (baseProgressSpec != null) {
            baseProgressSpec.hide();
        }
    }

    public void setProgressBar(int n) {
        if (baseProgressSpec != null) {
            baseProgressSpec.setProgress(n);
        }
    }

    public void showProgressBar() {
        if (baseProgressSpec != null) {
            baseProgressSpec.show();
        }
    }

    public static IndicatorHandlerImpl getInstance() {
        return new IndicatorHandlerImpl();
    }


    public IndicatorHandlerImpl inJectProgressView(IBaseProgressSpec baseProgressSpec) {
        this.baseProgressSpec = baseProgressSpec;
        return this;
    }
}
