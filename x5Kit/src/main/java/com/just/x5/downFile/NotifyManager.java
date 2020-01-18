package com.just.x5.downFile;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

/**
 * 下载通知栏管理
 */
public class NotifyManager {
    private int NOTIFICATION_ID;
    private NotificationManager nm;
    private NotificationCompat.Builder cBuilder;

    public NotifyManager(Context context, int ID) {
        this.NOTIFICATION_ID = ID;
        // 获取系统服务来初始化对象
        nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(context, String.valueOf(NOTIFICATION_ID));
    }

    public void notify_progress(PendingIntent pendingIntent, int smallIcon,
                                String ticker, String title, String content,
                                boolean sound, boolean vibrate, boolean lights,
                                PendingIntent pendingIntentCancel) {
        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
        cBuilder.setTicker(ticker);// 在顶部状态栏中的提示信息
        cBuilder.setContentTitle(title);// 设置通知中心的标题
        cBuilder.setContentText(content);// 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());

        /*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
         * 不设置的话点击消息后也不清除，但可以滑动删除
         */
        cBuilder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
         * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
         */
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
         * Notification.DEFAULT_SOUND：系统默认铃声。
         * Notification.DEFAULT_VIBRATE：系统默认震动。
         * Notification.DEFAULT_LIGHTS：系统默认闪光。
         * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
         */
        int defaults = 0;
        cBuilder.setDeleteIntent(pendingIntentCancel);
        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }
        cBuilder.setDefaults(defaults);
    }

    public void setContentText(String text) {
        cBuilder.setContentText(text);
    }

    public boolean hasDeleteContent() {
        return cBuilder.build().deleteIntent != null;
    }

    public void setDelete(PendingIntent intent) {
        cBuilder.setDeleteIntent(intent);
    }

    /**
     * 更新进度
     */
    public void setProgress(int maxProgress, int currentProgress, boolean exc) {
        cBuilder.setProgress(maxProgress, currentProgress, exc);
        sent();
    }

    /**
     * 下载完成
     */
    public void setProgressFinish(String content, PendingIntent pendingIntent) {
        cBuilder.setContentText(content);
        cBuilder.setProgress(100, 100, false);
        cBuilder.setContentIntent(pendingIntent);
        sent();
    }

    /**
     * 发送通知
     */
    public void sent() {
        Notification notification = cBuilder.build();
        // 发送该通知
        nm.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 取消通知
     *
     * @param id 通知id
     */
    public void cancelById(int id) {
        nm.cancel(id);
    }

    /**
     * 取消全部通知
     */
    public void cancelAll() {
        nm.cancelAll();
    }

}
