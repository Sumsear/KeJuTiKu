package com.example.hp.keju.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.hp.keju.R;

public class NotificationHelper {

    private String mNotificationChannelId = "NOTIFY_ID";
    private String mNotificationChannelName = "NOTIFY_NAME";
    private int mNotificationId = 10086;
    private Context mContext;
    private NotificationManager nm;
    private NotificationCompat.Builder nb;
    private String mTitle;
    private String mContent;
    private int mMaxProgress;

    public NotificationHelper(Context context) {
        this.mContext = context;
        if (mContext != null) {
            nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public NotificationHelper setId(int id) {
        this.mNotificationId = id;
        return this;
    }

    public NotificationHelper setChannelId(String id) {
        this.mNotificationChannelId = id;
        return this;
    }

    public NotificationHelper setChannelName(String name) {
        this.mNotificationChannelName = name;
        return this;
    }

    public NotificationHelper setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public NotificationHelper setContent(String content) {
        this.mContent = content;
        return this;
    }

    public NotificationHelper setMaxProgress(int max) {
        this.mMaxProgress = max;
        return this;
    }

    /**
     * TODO 通知
     */
    public void notification(int progress) {
        notification(mTitle, mContent, progress);
    }

    /**
     * TODO 通知
     */
    public void notification(String title, String content, int progress) {

        if (mContent == null) return;
        if (nb == null){
            nb = new NotificationCompat.Builder(mContext, mNotificationChannelId);
        }
        nb.setAutoCancel(true);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setContentTitle(title);
        nb.setContentText(content);
        nb.setProgress(mMaxProgress, progress, false);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel nc = new NotificationChannel(mNotificationChannelId, mNotificationChannelName, NotificationManager.IMPORTANCE_LOW);
                nm.createNotificationChannel(nc);
            }
            nm.notify(mNotificationId, nb.build());
        }
    }

    /**
     * TODO 释放资源
     */
    public void destroyed() {
        if (nm != null) {
            nm.cancel(mNotificationId);
        }
        mContext = null;
    }

}
