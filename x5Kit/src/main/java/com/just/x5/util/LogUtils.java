package com.just.x5.util;

import android.util.Log;

/**
 * 日志工具类
 */

public class LogUtils {
    private static final String PREFIX = "---> ";

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

    public void d(String tag, String message) {
        if (getDebug()) {
            Log.v(PREFIX.concat(tag), message);
        }
    }

    public void d(String message) {
        if (getDebug()) {
            Log.v(PREFIX, message);
        }
    }

    public void i(String tag, String message) {
        if (getDebug()) {
            LogUtils.getInstance().e(PREFIX.concat(tag), message);
        }
    }

    public void i(String message) {
        if (getDebug()) {
            LogUtils.getInstance().e(PREFIX, message);
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
