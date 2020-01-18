package com.just.x5.downFile;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.just.x5.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 下载文件异步任务
 */

public class RealDownLoader extends AsyncTask<Void, Integer, Integer> implements Observer {
    private DownLoadTask mDownLoadTask;
    private long loaded = 0;
    private long totals;
    private long tmp = 0;
    private Exception e;
    private static final int TIME_OUT = 30000000;
    private NotifyManager mNotify;
    private static final int ERROR_LOAD = 406;
    private static final String TAG = RealDownLoader.class.getSimpleName();
    private AtomicBoolean atomic = new AtomicBoolean(false);
    private static Observable mObservable = new Observable() {
        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    };

    public RealDownLoader(DownLoadTask downLoadTask) {
        this.mDownLoadTask = downLoadTask;
        this.totals = mDownLoadTask.getLength();
        if (downLoadTask.getDrawableRes() == -1 || downLoadTask.getDrawableRes() == 0) {
            downLoadTask.setDrawableRes(R.mipmap.download);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //添加观察者
        mObservable.addObserver(this);
        Context mContext = mDownLoadTask.getContext().getApplicationContext();
        if (mContext != null && mDownLoadTask.isEnableIndicator()) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.tip_start_down), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            int id = mDownLoadTask.getId();
            PendingIntent rightPendIntent = PendingIntent.getActivity(mContext,
                    0x33 * id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = mDownLoadTask.getDrawableRes();
            String ticker = mDownLoadTask.getDownLoadMsgConfig().getTrickter();
            mNotify = new NotifyManager(mContext, id);
            mNotify.notify_progress(rightPendIntent, smallIcon, ticker, mDownLoadTask.getDownLoadMsgConfig().getFileDownLoad(), mDownLoadTask.getDownLoadMsgConfig().getPreLoading(), false, false, false, buildCancelContent(mContext, id));
            mNotify.sent();
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result = ERROR_LOAD;
        try {
            if (!checkDownLoaderCondition()) {
                return DownLoadMsg.STORAGE_ERROR.CODE;
            }
            if (!checkNet()) {
                return DownLoadMsg.NETWORK_ERROR_CONNECTION.CODE;
            }
            result = doDownLoad();
        } catch (Exception e) {
            this.e = e;
            LogUtils.getInstance().e(TAG, "doInBackground   Exception:" + e.getMessage());
        }
        return result;
    }

    //检查下载空间
    private boolean checkDownLoaderCondition() {
        if (mDownLoadTask.getLength() - mDownLoadTask.getFile().length() > AgentWebX5Utils.getAvailableStorage()) {
            LogUtils.getInstance().e(TAG, " 空间不足");
            return false;
        }
        return true;
    }

    //检查网络
    private boolean checkNet() {
        if (!mDownLoadTask.isForce()) {
            return AgentWebX5Utils.checkWifi(mDownLoadTask.getContext());
        } else {
            return AgentWebX5Utils.checkNetwork(mDownLoadTask.getContext());
        }
    }

    /**
     * 开始下载
     */
    private int doDownLoad() throws IOException {
        HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(mDownLoadTask.getUrl()).openConnection();
        mHttpURLConnection.setRequestProperty("Accept", "application/*");
        mHttpURLConnection.setConnectTimeout(5000 * 2);
        if (mDownLoadTask.getFile().length() > 0) {
            mHttpURLConnection.addRequestProperty("Range", "bytes=" + (tmp = mDownLoadTask.getFile().length()) + "-");
        }
        BufferedInputStream bis = null;
        LoadingRandomAccessFile out = null;
        try {
            mHttpURLConnection.connect();
            if (mHttpURLConnection.getResponseCode() != 200 && mHttpURLConnection.getResponseCode() != 206) {
                return DownLoadMsg.NETWORK_ERROR_STATUS_CODE.CODE;
            } else {
                InputStream in = mHttpURLConnection.getInputStream();
                out = new LoadingRandomAccessFile(mDownLoadTask.getFile());
                byte[] buffer = new byte[10240];
                bis = new BufferedInputStream(in, 1024 * 10);
                out.seek(out.length());
                long previousBlockTime = -1;
                while (!atomic.get()) {
                    int n = bis.read(buffer, 0, 1024 * 10);
                    if (n == -1) {
                        break;
                    }
                    out.write(buffer, 0, n);
                    if (!checkNet()) {
                        return DownLoadMsg.NETWORK_ERROR_CONNECTION.CODE;
                    }
                    if (previousBlockTime == -1) {
                        previousBlockTime = System.currentTimeMillis();
                    } else if ((System.currentTimeMillis() - previousBlockTime) > TIME_OUT) {
                        return DownLoadMsg.TIME_OUT.CODE;
                    }
                }
                if (atomic.get()) {
                    return DownLoadMsg.USER_CANCEL.CODE;
                }
                return DownLoadMsg.SUCCESSFULL.CODE;
            }
        } finally {
            mHttpURLConnection.disconnect();
            AgentWebX5Utils.closeIO(out);
            AgentWebX5Utils.closeIO(bis);
        }
    }

    private long time = 0;

    @Override
    protected void onProgressUpdate(Integer... values) {
        try {
            long c = System.currentTimeMillis();
            if (mNotify != null && c - time > 800) {
                time = c;
                if (!mNotify.hasDeleteContent()) {
                    mNotify.setDelete(buildCancelContent(mDownLoadTask.getContext().getApplicationContext(), mDownLoadTask.getId()));
                }
                int mProgress = (int) ((tmp + loaded) / (float) totals * 100);
                mNotify.setContentText(String.format(mDownLoadTask.getDownLoadMsgConfig().getLoading(), mProgress + "%"));
                mNotify.setProgress(100, mProgress, false);
            }
        } catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent buildCancelContent(Context context, int id) {
        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction(AgentWebX5Config.NOTIFICATION_CANCEL_ACTION);
        intentCancel.putExtra("taskUrl", mDownLoadTask.getUrl());
        return PendingIntent.getBroadcast(context, id << 3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        LogUtils.getInstance().e(TAG, "onPostExecute:" + integer);
        //删除观察
        mObservable.deleteObserver(this);
        if (mNotify != null) {
            mNotify.cancelById(mDownLoadTask.getId());
        }
        DownLoadResultListener mDownLoadResultListener = mDownLoadTask.getDownLoadResultListener();
        if (mDownLoadResultListener == null) {
            DefaultDownLoaderImpl.ExecuteTasksMap.getInstance().removeTask(mDownLoadTask.getFile().getPath());
        } else {
            if (integer > 200) {
                mDownLoadResultListener.error(mDownLoadTask.getFile().getAbsolutePath(), mDownLoadTask.getUrl(), DownLoadMsg.getMsgByCode(integer), this.e == null ? new RuntimeException("download fail ， cause:" + DownLoadMsg.getMsgByCode(integer)) : this.e);
            } else {
                Toast.makeText(mDownLoadTask.getContext(), mDownLoadTask.getContext().getResources().getString(R.string.tip_completed_down), Toast.LENGTH_LONG).show();
                mDownLoadResultListener.success(mDownLoadTask.getFile().getPath());
            }
            if (mDownLoadTask.isEnableIndicator()) {
                Intent mIntent = AgentWebX5Utils.getCommonFileIntent(mDownLoadTask.getContext(), mDownLoadTask.getFile());
                if (mIntent != null) {
                    if (!(mDownLoadTask.getContext() instanceof Activity)) {
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    PendingIntent rightPendIntent = PendingIntent.getActivity(mDownLoadTask.getContext(),
                            mDownLoadTask.getId() << 4, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mNotify.setProgressFinish(mDownLoadTask.getDownLoadMsgConfig().getClickOpen(), rightPendIntent);
                }
            }
        }
    }

    //观察改变
    @Override
    public void update(Observable o, Object arg) {
        String url;
        if (arg instanceof String && !TextUtils.isEmpty(url = (String) arg) && url.equals(mDownLoadTask.getUrl())) {
            atomic.set(true);
        }
    }

    private final class LoadingRandomAccessFile extends RandomAccessFile {
        public LoadingRandomAccessFile(File file) throws FileNotFoundException {
            super(file, "rw");
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            loaded += count;
            //更新进度
            publishProgress(0);
        }
    }


    enum DownLoadMsg {
        NETWORK_ERROR_CONNECTION(400), NETWORK_ERROR_STATUS_CODE(401), STORAGE_ERROR(402), TIME_OUT(403), USER_CANCEL(404), SUCCESSFULL(200);
        int CODE;

        DownLoadMsg(int e) {
            this.CODE = e;
        }

        public static String getMsgByCode(int code) {
            switch (code) {
                case 400:
                    return "Network connection error";
                case 401:
                    return "Connection status code error, non-200 or non 206";
                case 402:
                    return "Insufficient memory space";
                case 403:
                    return "Download time is overtime";
                case 404:
                    return "The user canceled the download";
                case 200:
                    return "Download successful";
                default:
                    return "Unknown exception";
            }
        }
    }


    public static class NotificationBroadcastReceiver extends BroadcastReceiver {
        public NotificationBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AgentWebX5Config.NOTIFICATION_CANCEL_ACTION.equals(action)) {
                String url = intent.getStringExtra("taskUrl");
                Class<?> mClazz = mObservable.getClass();
                try {
                    Method mMethod = mClazz.getMethod("setChanged", (Class<?>[]) null);
                    mMethod.setAccessible(true);
                    mMethod.invoke(mObservable, (Object[]) null);
                    //通知观察者
                    mObservable.notifyObservers(url);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


}
