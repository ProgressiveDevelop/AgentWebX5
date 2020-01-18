package com.just.x5.helpClass;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Nullable;

import com.just.x5.util.LogUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;


/**
 *
 */

public class AgentWebX5Config {
    public static final String[] MEDIAS = new String[]{"相机", "文件选择器"};
    //取消通知
    public static final String NOTIFICATION_CANCEL_ACTION = "notification_cancel_action";
    private static final String AGENTWEB_CACHE_PATCH = "/agentweb_cache";
    public static final String FILE_CACHE_PATH = "web_cache";
    public static final int WEBVIEW_DEFAULT_TYPE = 1;
    public static final int WEBVIEW_AGENTWEB_SAFE_TYPE = 2;
    public static final int WEBVIEW_CUSTOM_TYPE = 3;
    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String KEY_URI = "KEY_URI";
    public static final String KEY_FROM_INTENTION = "KEY_FROM_INTENTION";
    public static final int ACTION_PERMISSION = 1;
    public static final String STRING_ARR = "string_arr";
    public static final int ACTION_FILE = 2;
    public static final int ACTION_CAMERA = 3;
    //文件选择请求码
    public static final int REQUEST_CODE = 0x254;
    public static int WEBVIEW_TYPE = WEBVIEW_DEFAULT_TYPE;
    /**
     * FileProvider 后缀
     */
    public static final String PROVIDER_SUFFIX = ".AgentWebX5FileProvider";

    //获取Cookie
    public static String getCookiesByUrl(String url) {
        return CookieManager.getInstance() == null ? null : CookieManager.getInstance().getCookie(url);
    }

    public static void removeExpiredCookies() {
        CookieManager mCookieManager = null;
        if ((mCookieManager = CookieManager.getInstance()) != null) { //同步清除{
            mCookieManager.removeExpiredCookie();
            toSyncCookies();
        }
    }

    public static void removeAllCookies() {
        removeAllCookies(null);
    }

    // 解决兼容 Android 4.4 java.lang.NoSuchMethodError: android.webkit.CookieManager.removeSessionCookies
    public static void removeSessionCookies() {
        removeSessionCookies(null);
    }

    public static void removeSessionCookies(ValueCallback<Boolean> callback) {
        if (callback == null)
            callback = getDefaultIgnoreCallback();
        if (CookieManager.getInstance() == null) {
            callback.onReceiveValue(Boolean.FALSE);
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeSessionCookie();
            toSyncCookies();
            callback.onReceiveValue(Boolean.TRUE);
            return;
        }
        CookieManager.getInstance().removeSessionCookies(callback);
        toSyncCookies();
    }

    //Android  4.4  NoSuchMethodError: android.webkit.CookieManager.removeAllCookies
    public static void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {
        if (callback == null)
            callback = getDefaultIgnoreCallback();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookie();
            toSyncCookies();
            callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
            return;
        }
        CookieManager.getInstance().removeAllCookies(callback);
        toSyncCookies();
    }

    private static ValueCallback<Boolean> getDefaultIgnoreCallback() {
        return new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean ignore) {
                LogUtils.getInstance().e("Info", "removeExpiredCookies:" + ignore);
            }
        };
    }

    private static boolean isInit = false;

    public static synchronized void initCookiesManager(Context context) {
        if (!isInit) {
            createCookiesSyncInstance(context);
            isInit = true;
        }
    }

    public static String getCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath() + AGENTWEB_CACHE_PATCH;
    }

    public static String getDatabasesCachePath(Context context) {
        return context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
    }


    public static void syncCookie(String url, String cookies) {
        CookieManager mCookieManager = CookieManager.getInstance();
        if (mCookieManager != null) {
            mCookieManager.setCookie(url, cookies);
            toSyncCookies();
        }
    }

    private static void createCookiesSyncInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
    }

    private static void toSyncCookies() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
            return;
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                CookieManager.getInstance().flush();
            }
        });
    }
}
