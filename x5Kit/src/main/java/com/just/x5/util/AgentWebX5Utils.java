package com.just.x5.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.loader.content.CursorLoader;

import com.just.x5.R;
import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.uploadFile.FilePO;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static com.just.x5.helpClass.AgentWebX5Config.FILE_CACHE_PATH;

public class AgentWebX5Utils {
    public static final String TAG = "AgentWebX5Utils";

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取应用名
     *
     * @param context context
     * @return 应用名
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static void clearWebView(WebView m) {
        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null) {
            m.getHandler().removeCallbacksAndMessages(null);
        }
        m.removeAllViews();
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
    }

    public static boolean checkWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static int checkNetworkType(Context context) {
        int netType = 0;
        //连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null)
            return netType;
        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return 1;
            case ConnectivityManager.TYPE_MOBILE:
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return 2;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return 3;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return 4;
                    default:
                        return netType;
                }
            default:
                return netType;
        }
    }

    public static long getAvailableStorage() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    public static Intent getFileIntent(File file) {
        Uri uri = Uri.fromFile(file);
        String type = getMIMEType(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    private static String getMIMEType(File f) {
        String type;
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        switch (end) {
            case "pdf":
                type = "application/pdf";
                break;
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                type = "audio/*";
                break;
            case "3gp":
            case "mp4":
                type = "video/*";
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                type = "image/*";
                break;
            case "apk":
                /* android.permission.INSTALL_PACKAGES */
                type = "application/vnd.android.package-archive";
                break;
            default:
                /*如果无法直接打开，就跳出软件列表给用户选择 */
                type = "*/*";
                break;
        }
        return type;
    }


    /**
     * 是否覆盖了某个方法
     *
     * @param currentObject
     * @param methodName
     * @param method
     * @param clazzs
     * @return
     */
    public static boolean isOverriedMethod(Object currentObject, String methodName, String method, Class... clazzs) {
        LogUtils.getInstance().e(TAG, "currentObject：" + currentObject + "  methodName:" + methodName + "   method:" + method);
        if (currentObject == null) {
            return false;
        }
        boolean tag = false;
        try {
            Class clazz = currentObject.getClass();
            Method mMethod = clazz.getMethod(methodName, clazzs);
            String gStr = mMethod.toGenericString();
            tag = !gStr.contains(method);
        } catch (Exception igonre) {
            igonre.printStackTrace();
        }
        LogUtils.getInstance().e(TAG, "isOverriedMethod:" + tag);
        return tag;
    }


    public static void clearWebViewAllCache(Context context, WebView webView) {
        try {
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            AgentWebX5Config.removeAllCookies(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearWebViewAllCache(Context context) {
        try {
            clearWebViewAllCache(context, new WebView(context.getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //// 来源stackflow
    static int clearCacheFolder(final File dir, final int numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Info", String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }

    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     *
     * // 来源stackflow
     */
    public static void clearCache(final Context context, final int numDays) {
        LogUtils.getInstance().e(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        LogUtils.getInstance().e(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }

    public static String[] uriToPath(Activity activity, Uri[] uris) {
        if (activity == null || uris == null || uris.length == 0) {
            return null;
        }
        String[] paths = new String[uris.length];
        int i = 0;
        for (Uri mUri : uris) {
            paths[i++] = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 ? getFileAbsolutePath(activity, mUri) : getRealPathBelowVersion(activity, mUri);
        }
        return paths;
    }

    private static String getRealPathBelowVersion(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    //必须执行在子线程, 会阻塞 直到完成;
    public static Queue<FilePO> convertFile(String[] paths) throws Exception {
        if (paths == null || paths.length == 0)
            return null;
        int tmp = Runtime.getRuntime().availableProcessors() + 1;
        int result = paths.length > tmp ? tmp : paths.length;
        Executor mExecutor = Executors.newFixedThreadPool(result);
        final Queue<FilePO> mQueue = new LinkedBlockingQueue<>();
        CountDownLatch mCountDownLatch = new CountDownLatch(paths.length);
        int i = 1;
        for (String path : paths) {
            LogUtils.getInstance().e(TAG, "path   :  :" + path);
            if (TextUtils.isEmpty(path)) {
                mCountDownLatch.countDown();
                continue;
            }
            mExecutor.execute(new EncodeFileRunnable(path, mQueue, mCountDownLatch, i++));
        }
        mCountDownLatch.await();
        if (!((ThreadPoolExecutor) mExecutor).isShutdown()) {
            ((ThreadPoolExecutor) mExecutor).shutdownNow();
        }
        LogUtils.getInstance().e(TAG, "isShutDown:" + (((ThreadPoolExecutor) mExecutor).isShutdown()));
        return mQueue;
    }

    /**
     * 运行在主线程
     *
     * @param runnable Runnable
     */
    public static void runInUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * 创建文件
     *
     * @param context context
     * @return 文件
     */
    public static File createImageFile(Context context) {
        File mFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String imageName = String.format("IMG_%s.jpg", timeStamp);
            mFile = createFileByName(context, imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFile;
    }

    /**
     * 创建文件
     *
     * @param context  Context
     * @param fileName 文件名
     * @throws IOException
     */
    public static File createFileByName(Context context, String fileName) throws IOException {
        //获取文件夹路径
        String path = getWebCacheFilePath(context);
        if (TextUtils.isEmpty(path)) {
            throw new NullPointerException(context.getResources().getString(R.string.tip_create_dir_fail));
        } else {
            File mFile = new File(path, fileName);
            if (!mFile.exists()) {
                boolean state = mFile.createNewFile();
                if (!state) {
                    throw new NullPointerException(context.getResources().getString(R.string.tip_create_file_fail));
                }
            }
            return mFile;
        }
    }

    /**
     * 获取文件保存路径
     *
     * @param context Context
     * @return
     */
    private static String getWebCacheFilePath(Context context) {
        File mFile = context.getExternalCacheDir();
        if (mFile == null) {
            return null;
        } else {
            if (Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(mFile))) {
                String dir = mFile.getAbsolutePath();
                LogUtils.getInstance().e(TAG, "dir absolutePath:" + mFile.getAbsolutePath());
                mFile = new File(dir, FILE_CACHE_PATH);
                if (!mFile.exists()) {
                    boolean state = mFile.mkdirs();
                    if (!state) {
                        return dir;
                    } else {
                        LogUtils.getInstance().e(TAG, "sub dir absolutePath:" + mFile.getAbsolutePath() + "  filePath:" + mFile.getPath());
                        return mFile.getAbsolutePath();
                    }
                } else {
                    return mFile.getAbsolutePath();
                }
            } else {
                return null;
            }
        }
    }

    /**
     * 获取打开文件意图
     *
     * @param context Context
     * @param file    File
     * @return Intent
     */
    public static Intent getCommonFileIntent(Context context, File file) {
        Intent mIntent = new Intent().setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        mIntent.setDataAndType(getUriFromFile(context, file), type);
        if (Build.VERSION.SDK_INT >= 24) {
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return mIntent;
    }

    /**
     * 获取拍照意图
     *
     * @param context Context
     * @param file    存储文件
     * @return Intent
     */
    public static Intent getIntentCaptureCompat(Context context, File file) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mUri = getUriFromFile(context, file);
        mIntent.addCategory(Intent.CATEGORY_DEFAULT);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        return mIntent;
    }


    /**
     * 获取Uri
     *
     * @param context Context
     * @param file    存储的文件
     * @return Uri
     */
    private static Uri getUriFromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + AgentWebX5Config.PROVIDER_SUFFIX, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    static class EncodeFileRunnable implements Runnable {
        private String filePath;
        private Queue<FilePO> mQueue;
        private CountDownLatch mCountDownLatch;
        private int id;

        public EncodeFileRunnable(String filePath, Queue<FilePO> queue, CountDownLatch countDownLatch, int id) {
            this.filePath = filePath;
            this.mQueue = queue;
            this.mCountDownLatch = countDownLatch;
            this.id = id;
        }

        @Override
        public void run() {
            InputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                File mFile = new File(filePath);
                if (mFile.exists()) {
                    is = new FileInputStream(mFile);
                    os = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = is.read(b, 0, 1024)) != -1) {
                        os.write(b, 0, len);
                    }
                    mQueue.offer(new FilePO(id, mFile.getAbsolutePath(), Base64.encodeToString(os.toByteArray(), Base64.DEFAULT)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeIO(is);
                closeIO(os);
                mCountDownLatch.countDown();
            }
        }
    }


    @TargetApi(19)
    public static String getFileAbsolutePath(Activity context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if (Objects.requireNonNull(fileUri.getAuthority()).equalsIgnoreCase(context.getPackageName() + ".AgentWebX5FileProvider")) {
            String path = fileUri.getPath();
            int index = path != null ? path.lastIndexOf("/") : 0;
            return getWebCacheFilePath(context) + File.separator + path.substring(index + 1, path.length());
        } else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static Intent getIntentCompat(Context context, File file) {
        Intent mIntent;
        LogUtils.getInstance().e("AgentWebX5Utils", "getIntentCompat  :" + context.getApplicationInfo().targetSdkVersion);
        if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.N) {
            mIntent = new Intent(Intent.ACTION_VIEW);
            mIntent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".AgentWebX5FileProvider", file), "application/vnd.android.package-archive");
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            mIntent = AgentWebX5Utils.getFileIntent(file);
        }
        return mIntent;
    }

    /**
     * 判断是否JSON
     *
     * @param target 数据
     * @return boolean
     */
    public static boolean isJson(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        boolean tag = false;
        try {
            if (target.startsWith("[")) {
                new JSONArray(target);
                tag = true;
            } else if (target.startsWith("{")) {
                new JSONObject(target);
                tag = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tag;
    }

    public static String convertFileParcelObjectsToJson(Collection<FilePO> collection) {
        if (collection == null || collection.size() == 0) {
            return null;
        }
        Iterator<FilePO> mFileParcels = collection.iterator();
        JSONArray mJSONArray = new JSONArray();
        try {
            while (mFileParcels.hasNext()) {
                JSONObject jo = new JSONObject();
                FilePO mFileParcel = mFileParcels.next();
                jo.put("contentPath", mFileParcel.getContentPath());
                jo.put("fileBase64", mFileParcel.getFileBase64());
                jo.put("id", mFileParcel.getId());
                mJSONArray.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(mJSONArray);
    }

    public static boolean isMainProcess(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean tag = false;
        int id = android.os.Process.myPid();
        String processName = "";
        String packgeName = context.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> mInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo mRunningAppProcessInfo : mInfos) {
            if (mRunningAppProcessInfo.pid == id) {
                processName = mRunningAppProcessInfo.processName;
                break;
            }
        }
        if (packgeName.equals(processName)) {
            tag = true;
        }
        return tag;
    }


    public static boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

    private static Toast mToast = null;

    public static void toastShowShort(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * Md5 加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭IO流
     *
     * @param closeable Closeable实体对象
     */
    public static void closeIO(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查权限
     *
     * @param activity    activity
     * @param permissions 权限数组
     * @return 未授权权限List
     */
    public static List<String> getDeniedPermissions(Activity activity, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }
}
