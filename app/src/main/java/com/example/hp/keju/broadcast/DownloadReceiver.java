package com.example.hp.keju.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.example.hp.keju.BuildConfig;
import com.example.hp.keju.R;
import com.example.hp.keju.util.LogUtil;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        LogUtil.e("action" + action);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm == null) return;
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = dm.query(query);
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        install(context, dm, downloadId);
                        break;
                    case DownloadManager.STATUS_FAILED:
                        LogUtil.e("下载失败!");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        LogUtil.e("下载中!");
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        LogUtil.e("下载暂停!");
                        break;
                }
            }
            cursor.close();
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
            //处理 如果还未完成下载，用户点击Notification ，跳转到下载中心
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }

    /**
     * TODO 安装APK
     *
     * @param context    上下文
     * @param dm         DownloadManager
     * @param downloadId download id
     */
    private void install(Context context, DownloadManager dm, long downloadId) {
        try {
            Intent install = new Intent(Intent.ACTION_VIEW);
            Uri uri = dm.getUriForDownloadedFile(downloadId);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            install.setDataAndType(uri, "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
