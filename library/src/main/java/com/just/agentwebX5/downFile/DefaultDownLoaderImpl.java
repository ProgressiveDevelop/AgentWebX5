package com.just.agentwebX5.downFile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.just.agentwebX5.ActionActivity;
import com.just.agentwebX5.permission.IPermissionInterceptor;
import com.just.agentwebX5.IProvider;
import com.just.agentwebX5.PermissionConstant;
import com.just.agentwebX5.R;
import com.just.agentwebX5.helpClass.AgentWebX5Config;
import com.just.agentwebX5.permission.IPermissionRequestListener;
import com.just.agentwebX5.util.AgentWebX5Utils;
import com.just.agentwebX5.util.LogUtils;
import com.tencent.smtt.sdk.DownloadListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */

public class DefaultDownLoaderImpl implements DownloadListener, DownLoadResultListener {
    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;
    private volatile static int NoticationID = 1;
    private List<DownLoadResultListener> mDownLoadResultListeners;
    private WeakReference<Activity> mActivityWeakReference;
    private DownLoadMsgConfig mDownLoadMsgConfig;
    private static final String TAG = DefaultDownLoaderImpl.class.getSimpleName();
    private IPermissionInterceptor mPermissionListener;
    private String url;
    private String contentDisposition;
    private long contentLength;
    private AtomicBoolean isParallelDownload = new AtomicBoolean(false);
    private int icon;


    private DefaultDownLoaderImpl(Builder builder) {
        mActivityWeakReference = new WeakReference<>(builder.mActivity);
        this.mContext = builder.mActivity.getApplicationContext();
        this.isForce = builder.isForce;
        this.enableIndicator = builder.enableIndicator;
        this.mDownLoadResultListeners = builder.mDownLoadResultListeners;
        this.mDownLoadMsgConfig = builder.mDownLoadMsgConfig;
        this.mPermissionListener = builder.mPermissionInterceptor;
        isParallelDownload.set(builder.isParallelDownload);
        icon = builder.icon;

    }

    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        LogUtils.getInstance().e(TAG, "disposition" + contentDisposition);
        onDownloadStartInternal(url, contentDisposition, mimetype, contentLength);
    }

    private void onDownloadStartInternal(String url, String contentDisposition, String mimetype, long contentLength) {
        if (mActivityWeakReference.get() == null || mActivityWeakReference.get().isFinishing()) {
            return;
        }
        LogUtils.getInstance().e(TAG, "mime:" + mimetype);
        if (this.mPermissionListener != null) {
            if (this.mPermissionListener.intercept(url, PermissionConstant.STORAGE, "download")) {
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkNeedPermission().isEmpty()) {
                preDownload(url, contentDisposition, contentLength);
            } else {
                ActionActivity.setPermissionListener(getPermissionListener());
                this.url = url;
                this.contentDisposition = contentDisposition;
                this.contentLength = contentLength;
                ActionActivity.start(mActivityWeakReference.get(), AgentWebX5Config.ACTION_PERMISSION, 0, PermissionConstant.STORAGE);
            }
        } else {
            preDownload(url, contentDisposition, contentLength);
        }
    }

    private IPermissionRequestListener getPermissionListener() {
        return new IPermissionRequestListener() {
            @Override
            public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
                if (checkNeedPermission().isEmpty()) {
                    preDownload(DefaultDownLoaderImpl.this.url, DefaultDownLoaderImpl.this.contentDisposition, DefaultDownLoaderImpl.this.contentLength);
                    url = null;
                    contentDisposition = null;
                    contentLength = -1;
                } else {
                    LogUtils.getInstance().e(TAG, "储存权限获取失败~");
                }

            }
        };
    }

    private List<String> checkNeedPermission() {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < PermissionConstant.STORAGE.length; i++) {
            if (ContextCompat.checkSelfPermission(mActivityWeakReference.get(), PermissionConstant.STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(PermissionConstant.STORAGE[i]);
            }
        }
        return deniedPermissions;
    }

    private void preDownload(String url, String contentDisposition, long contentLength) {
        File mFile = getFile(contentDisposition, url);
        if (mFile == null)
            return;
        if (mFile.exists() && mFile.length() >= contentLength) {

            Intent mIntent = AgentWebX5Utils.getCommonFileIntent(mContext, mFile);
            try {
                if (mIntent != null) {
                    if (!(mContext instanceof Activity))
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(mIntent);
                }
                return;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }


        if (ExecuteTasksMap.getInstance().contains(url)) {
            AgentWebX5Utils.toastShowShort(mContext, mDownLoadMsgConfig.getTaskHasBeenExist());
            return;
        }


        if (AgentWebX5Utils.checkNetworkType(mContext) > 1) { //移动数据

            showDialog(url, contentLength, mFile);
            return;
        }
        performDownload(url, contentLength, mFile);
    }

    private void forceDown(final String url, final long contentLength, final File file) {
        isForce = true;
        performDownload(url, contentLength, file);
    }

    private void showDialog(final String url, final long contentLength, final File file) {
        Activity mActivity;
        if ((mActivity = mActivityWeakReference.get()) == null || mActivity.isFinishing()) {
            return;
        }
        AlertDialog mAlertDialog;
        mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setTitle(mDownLoadMsgConfig.getTips())//
                .setMessage(mDownLoadMsgConfig.getHoneycomblow())//
                .setNegativeButton(mDownLoadMsgConfig.getDownLoad(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                        forceDown(url, contentLength, file);
                    }
                })//
                .setPositiveButton(mDownLoadMsgConfig.getCancel(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null)
                            dialog.dismiss();
                    }
                }).create();
        mAlertDialog.show();
    }

    private void performDownload(String url, long contentLength, File file) {
        ExecuteTasksMap.getInstance().addTask(url, file.getAbsolutePath());
        //并行下载.
        if (isParallelDownload.get()) {
            new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownLoadMsgConfig, icon == -1 ? R.mipmap.download : icon)).executeOnExecutor(ExecutorProvider.getInstance().provide(), (Void[]) null);
        } else {
            //默认串行下载.
            new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, mDownLoadMsgConfig, icon == -1 ? R.mipmap.download : icon)).execute();
        }
    }


    private File getFile(String contentDisposition, String url) {
        try {
            String fileName = getFileName(contentDisposition);
            if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(url)) {
                Uri mUri = Uri.parse(url);
                fileName = mUri.getPath().substring(mUri.getPath().lastIndexOf('/') + 1);
            }
            if (!TextUtils.isEmpty(fileName) && fileName.length() > 64) {
                fileName = fileName.substring(fileName.length() - 64);
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = AgentWebX5Utils.md5(url);
            }
            return AgentWebX5Utils.createFileByName(mContext, fileName);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getFileName(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) {
            return null;
        } else {
            Matcher m = Pattern.compile(".*filename=(.*)").matcher(contentDisposition.toLowerCase());
            if (m.find()) {
                return m.group(1);
            } else {
                return null;
            }
        }
    }

    @Override
    public void success(String path) {
        ExecuteTasksMap.getInstance().removeTask(path);
        if (AgentWebX5Utils.isEmptyCollection(mDownLoadResultListeners)) {
            return;
        }
        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            if (mDownLoadResultListener != null) {
                mDownLoadResultListener.success(path);
            }
        }
    }


    @Override
    public void error(String path, String resUrl, String cause, Throwable e) {
        ExecuteTasksMap.getInstance().removeTask(path);
        if (AgentWebX5Utils.isEmptyCollection(mDownLoadResultListeners)) {
            AgentWebX5Utils.toastShowShort(mContext, mDownLoadMsgConfig.getDownLoadFail());
            return;
        }
        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            if (mDownLoadResultListener == null) {
                continue;
            }
            mDownLoadResultListener.error(path, resUrl, cause, e);
        }
    }

    static class ExecutorProvider implements IProvider<Executor> {
        private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private final int CORE_POOL_SIZE = (int) (Math.max(2, Math.min(CPU_COUNT - 1, 4)) * 1.5);
        private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

        private final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);
            private SecurityManager securityManager = System.getSecurityManager();
            private ThreadGroup group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();

            public Thread newThread(Runnable r) {
                Thread mThread = new Thread(group, r, "pool-agentweb-thread-" + mCount.getAndIncrement());
                if (mThread.isDaemon()) {
                    mThread.setDaemon(false);
                }
                mThread.setPriority(Thread.MIN_PRIORITY);
                LogUtils.getInstance().e(TAG, "Thread Name:" + mThread.getName());
                LogUtils.getInstance().e(TAG, "live:" + mThreadPoolExecutor.getActiveCount() + "    getCorePoolSize:" + mThreadPoolExecutor.getCorePoolSize() + "  getPoolSize:" + mThreadPoolExecutor.getPoolSize());
                return mThread;
            }
        };

        private static final BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<Runnable>(128);
        private ThreadPoolExecutor mThreadPoolExecutor;

        private ExecutorProvider() {
            internalInit();
        }

        private void internalInit() {
            if (mThreadPoolExecutor != null && !mThreadPoolExecutor.isShutdown()) {
                mThreadPoolExecutor.shutdownNow();
            }
            int KEEP_ALIVE_SECONDS = 15;
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            mThreadPoolExecutor.allowCoreThreadTimeOut(true);
        }


        public static ExecutorProvider getInstance() {
            return InnerHolder.M_EXECUTOR_PROVIDER;
        }

        static class InnerHolder {
            private static final ExecutorProvider M_EXECUTOR_PROVIDER = new ExecutorProvider();
        }

        @Override
        public Executor provide() {
            return mThreadPoolExecutor;
        }

    }

    //静态缓存当前正在下载的任务url
    public static class ExecuteTasksMap extends ReentrantLock {

        private LinkedList mTasks;

        private ExecuteTasksMap() {
            super(false);
            mTasks = new LinkedList();
        }

        private static ExecuteTasksMap sInstance = null;

        static ExecuteTasksMap getInstance() {


            if (sInstance == null) {
                synchronized (ExecuteTasksMap.class) {
                    if (sInstance == null)
                        sInstance = new ExecuteTasksMap();
                }
            }
            return sInstance;
        }

        void removeTask(String path) {

            int index = mTasks.indexOf(path);
            if (index == -1)
                return;
            try {
                lock();
                int position;
                if ((position = mTasks.indexOf(path)) == -1)
                    return;
                mTasks.remove(position);
                mTasks.remove(position - 1);
            } finally {
                unlock();
            }

        }

        void addTask(String url, String path) {
            try {
                lock();
                mTasks.add(url);
                mTasks.add(path);
            } finally {
                unlock();
            }

        }

        //加锁读
        boolean contains(String url) {
            try {
                lock();
                return mTasks.contains(url);
            } finally {
                unlock();
            }
        }
    }

    public static class Builder {
        private Activity mActivity;
        private boolean isForce;
        private boolean enableIndicator;
        private List<DownLoadResultListener> mDownLoadResultListeners;
        private DownLoadMsgConfig mDownLoadMsgConfig;
        private IPermissionInterceptor mPermissionInterceptor;
        private int icon = -1;
        private boolean isParallelDownload = false;

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setForce(boolean force) {
            isForce = force;
            return this;
        }

        public Builder setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
            return this;
        }

        public Builder setDownLoadResultListeners(List<DownLoadResultListener> downLoadResultListeners) {
            this.mDownLoadResultListeners = downLoadResultListeners;
            return this;
        }

        public Builder setDownLoadMsgConfig(DownLoadMsgConfig downLoadMsgConfig) {
            mDownLoadMsgConfig = downLoadMsgConfig;
            return this;
        }

        public Builder setPermissionInterceptor(IPermissionInterceptor permissionInterceptor) {
            mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setParallelDownload(boolean parallelDownload) {
            isParallelDownload = parallelDownload;
            return this;
        }

        public DefaultDownLoaderImpl create() {
            return new DefaultDownLoaderImpl(this);
        }
    }
}
