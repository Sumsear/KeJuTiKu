package com.example.hp.keju.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.example.hp.keju.R;

public class UpdateUtil {

    private static UpdateUtil instance;
    private static long downloadId = -1;

    private UpdateUtil() {
    }

    public static UpdateUtil getInstance() {
        if (instance == null) {
            synchronized (UpdateUtil.class) {
                if (instance == null) {
                    instance = new UpdateUtil();
                }
            }
        }
        return instance;
    }

    public long download(Context context, String url) {

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            LogUtil.e("DownloadManager != null");
            LogUtil.e("url: " + url);
            Uri uri = Uri.parse(url);
            if (downloadId != -1){
                dm.remove(downloadId);
            }
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(context.getString(R.string.app_name));
            request.setDescription("一个正在慢慢完善的科举答题App");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "KeJu");
            downloadId = dm.enqueue(request);
        }
        return downloadId;
    }
}
