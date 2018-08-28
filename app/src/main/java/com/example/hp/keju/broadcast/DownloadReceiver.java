package com.example.hp.keju.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.hp.keju.util.LogUtil;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        LogUtil.e("action" + action);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            try {
                if (dm == null) return;
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                LogUtil.e("download id：" + id);
                Uri uri = dm.getUriForDownloadedFile(id);
                if (uri != null) {
                    LogUtil.e("Uri = " + uri.toString());
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
            //处理 如果还未完成下载，用户点击Notification ，跳转到下载中心
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }
}
