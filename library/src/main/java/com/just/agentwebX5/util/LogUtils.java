package com.just.agentwebX5.util;

import android.util.Log;

/**
 *
 */

public class LogUtils {
    private static final String PREFIX = "X5---> "; //

    private LogUtils() {
    }

    private static class LogHolder {
        static final LogUtils INSTANCE = new LogUtils();
    }

    public static LogUtils getInstance() {
        return LogHolder.INSTANCE;
    }

    private boolean debug;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void i(String tag, String message) {
        if (getDebug()) {
           LogUtils.getInstance().e(PREFIX.concat(tag), message);
        }
    }

    public void v(String tag, String message) {
        if (getDebug()) {
            Log.v(PREFIX.concat(tag), message);
        }
    }

    public void e(String tag, String message) {
        if (getDebug()) {
            Log.e(PREFIX.concat(tag), message);
        }
    }

    public void e(String message) {
        if (getDebug()) {
            Log.e(PREFIX, message);
        }
    }
}
