package com.just.agentwebX5.downFile;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.just.agentwebX5.util.AgentWebX5Utils;
import com.just.agentwebX5.util.LogUtils;
import com.just.agentwebX5.R;

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
 * source code  https://github.com/Justson/AgentWeb
 */

public class RealDownLoader extends AsyncTask<Void, Integer, Integer> implements Observer {
    private DownLoadTask mDownLoadTask;
    private long loaded = 0;
    private long totals;
    private long tmp = 0;
    private Exception e;
    private static final int TIME_OUT = 30000000;
    private Notify mNotify;
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
        checkNullTask(downLoadTask);
    }

    private void checkNullTask(DownLoadTask downLoadTask) {
        LogUtils.getInstance().e("Notify", "TAG:" + downLoadTask.getDrawableRes());
        if (downLoadTask.getDrawableRes() == -1 || downLoadTask.getDrawableRes() == 0) {
            downLoadTask.setDrawableRes(R.mipmap.download);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mObservable.addObserver(this);
        buildNotify(new Intent(), mDownLoadTask.getId(), mDownLoadTask.getDownLoadMsgConfig().getPreLoading());
    }

    private boolean checkDownLoaderCondition() {
        if (mDownLoadTask.getLength() - mDownLoadTask.getFile().length() > AgentWebX5Utils.getAvailableStorage()) {
            LogUtils.getInstance().e(TAG, " 空间不足");
            return false;
        }
        return true;
    }

    private boolean checknet() {
        if (!mDownLoadTask.isForce()) {
            return AgentWebX5Utils.checkWifi(mDownLoadTask.getContext());
        } else {
            return AgentWebX5Utils.checkNetwork(mDownLoadTask.getContext());
        }
    }


    @Override
    protected Integer doInBackground(Void... params) {
        int result = ERROR_LOAD;
        try {
            if (!checkDownLoaderCondition())
                return DownLoadMsg.STORAGE_ERROR.CODE;
            if (!checknet())
                return DownLoadMsg.NETWORK_ERROR_CONNECTION.CODE;
            result = doDownLoad();
        } catch (Exception e) {
            this.e = e;//发布
            LogUtils.getInstance().e(TAG, "doInBackground   Exception:" + e.getMessage());
        }
        return result;
    }

    private int doDownLoad() throws IOException {
        HttpURLConnection mHttpURLConnection = createUrlConnection(mDownLoadTask.getUrl());
        if (mDownLoadTask.getFile().length() > 0) {
            mHttpURLConnection.addRequestProperty("Range", "bytes=" + (tmp = mDownLoadTask.getFile().length()) + "-");
        }
        try {
            mHttpURLConnection.connect();
            if (mHttpURLConnection.getResponseCode() != 200 && mHttpURLConnection.getResponseCode() != 206) {
                return DownLoadMsg.NETWORK_ERROR_STATUS_CODE.CODE;
            }
            return doDownLoad(mHttpURLConnection.getInputStream(), new LoadingRandomAccessFile(mDownLoadTask.getFile()));
        } finally {
            if (mHttpURLConnection != null) {
                mHttpURLConnection.disconnect();
            }
        }
    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {
        HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        mHttpURLConnection.setRequestProperty("Accept", "application/*");
        mHttpURLConnection.setConnectTimeout(5000 * 2);
        return mHttpURLConnection;
    }

    private long time = 0;

    @Override
    protected void onProgressUpdate(Integer... values) {
        try {
            long c = System.currentTimeMillis();
            if (mNotify != null && c - time > 800) {
                time = c;
                if (!mNotify.hasDeleteContent()) {
                    mNotify.setDelecte(buildCancelContent(mDownLoadTask.getContext().getApplicationContext(), mDownLoadTask.getId()));
                }
                int mProgress = (int) ((tmp + loaded) / Float.valueOf(totals) * 100);
                mNotify.setContentText(String.format(mDownLoadTask.getDownLoadMsgConfig().getLoading(), mProgress + "%"));
                mNotify.setProgress(100, mProgress, false);
            }
        } catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            LogUtils.getInstance().e(TAG, "onPostExecute:" + integer);
            mObservable.deleteObserver(this);
            doCallback(integer);
            if (integer > 200) {
                if (mNotify != null) {
                    mNotify.cancel(mDownLoadTask.getId());
                }
                return;
            }
            if (mDownLoadTask.isEnableIndicator()) {
                if (mNotify != null)
                    mNotify.cancel(mDownLoadTask.getId());
                Intent mIntent = AgentWebX5Utils.getCommonFileIntent(mDownLoadTask.getContext(), mDownLoadTask.getFile());
                try {
                    if (mIntent != null) {
                        if (!(mDownLoadTask.getContext() instanceof Activity)) {
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        PendingIntent rightPendIntent = PendingIntent.getActivity(mDownLoadTask.getContext(),
                                mDownLoadTask.getId() << 4, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mNotify.setProgressFinish(mDownLoadTask.getDownLoadMsgConfig().getClickOpen(), rightPendIntent);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.getInstance().e(TAG, "e:" + e.getMessage());
        }
    }

    private void doCallback(Integer code) {
        DownLoadResultListener mDownLoadResultListener = null;
        if ((mDownLoadResultListener = mDownLoadTask.getDownLoadResultListener()) == null) {
            LogUtils.getInstance().e(TAG, "DownLoadResultListener has been death");
            DefaultDownLoaderImpl.ExecuteTasksMap.getInstance().removeTask(mDownLoadTask.getFile().getPath());
            return;
        }
        if (code > 200) {
            mDownLoadResultListener.error(mDownLoadTask.getFile().getAbsolutePath(), mDownLoadTask.getUrl(), DownLoadMsg.getMsgByCode(code), this.e == null ? new RuntimeException("download fail ， cause:" + DownLoadMsg.getMsgByCode(code)) : this.e);
        } else {
            mDownLoadResultListener.success(mDownLoadTask.getFile().getPath());
        }
    }


    private void buildNotify(Intent intent, int id, String progressHint) {
        Context mContext = mDownLoadTask.getContext().getApplicationContext();
        if (mContext != null && mDownLoadTask.isEnableIndicator()) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mContext,
                    0x33 * id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = mDownLoadTask.getDrawableRes();
            String ticker = mDownLoadTask.getDownLoadMsgConfig().getTrickter();
            mNotify = new Notify(mContext, id);
            mNotify.notify_progress(rightPendIntent, smallIcon, ticker, mDownLoadTask.getDownLoadMsgConfig().getFileDownLoad(), progressHint, false, false, false, buildCancelContent(mContext, id));
            mNotify.sent();
        }
    }


    private PendingIntent buildCancelContent(Context context, int id) {
        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction("com.agentweb.cancelled");
        intentCancel.putExtra("type", "type");
        intentCancel.putExtra("TAG", mDownLoadTask.getUrl());
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, id << 3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        LogUtils.getInstance().e(TAG, "id<<3:" + (id << 3));
        return pendingIntentCancel;
    }


    private int doDownLoad(InputStream in, RandomAccessFile out) throws IOException {
        byte[] buffer = new byte[10240];
        BufferedInputStream bis = new BufferedInputStream(in, 1024 * 10);
        try {
            out.seek(out.length());
            long previousBlockTime = -1;
            while (!atomic.get()) {
                int n = bis.read(buffer, 0, 1024 * 10);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                if (!checknet()) {
                    LogUtils.getInstance().e(TAG, "network");
                    return DownLoadMsg.NETWORK_ERROR_CONNECTION.CODE;
                }
                if (previousBlockTime == -1) {
                    previousBlockTime = System.currentTimeMillis();
                } else if ((System.currentTimeMillis() - previousBlockTime) > TIME_OUT) {
                    LogUtils.getInstance().e(TAG, "timeout");
                    return DownLoadMsg.TIME_OUT.CODE;
                }
            }
            LogUtils.getInstance().e(TAG, "atomic:" + atomic.get());
            if (atomic.get()) {
                return DownLoadMsg.USER_CANCEL.CODE;
            }
            return DownLoadMsg.SUCCESSFULL.CODE;
        } finally {
            AgentWebX5Utils.closeIO(out);
            AgentWebX5Utils.closeIO(bis);
        }
    }

    private void toCancel() {
        atomic.set(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        String url;
        if (arg instanceof String && !TextUtils.isEmpty(url = (String) arg) && url.equals(mDownLoadTask.getUrl())) {
            toCancel();
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
            LogUtils.getInstance().e(TAG, "  CODE:" + code);
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
            if ("com.agentweb.cancelled".equals(action)) {
                String url = intent.getStringExtra("TAG");
                Class<?> mClazz = mObservable.getClass();
                try {
                    Method mMethod = mClazz.getMethod("setChanged", (Class<?>[]) null);
                    mMethod.setAccessible(true);
                    mMethod.invoke(mObservable, (Object[]) null);
                    mObservable.notifyObservers(url);
                    LogUtils.getInstance().e(TAG, "size:" + mObservable.countObservers());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


}
