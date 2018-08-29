package com.example.hp.keju.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.example.hp.keju.R;

import java.io.File;

public class UpdateUtil {

    private static UpdateUtil instance;
    private static long downloadId = -1;
    private Handler mHandler = new Handler(Looper.getMainLooper());

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
            if (downloadId != -1) {
                dm.remove(downloadId);
            }
            String title = context.getString(R.string.app_name);

            File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + title);
            if (file != null && file.exists()) {
                file.delete();
            }
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(title);
            request.setDescription("一个正在慢慢完善的科举答题App");
            request.setAllowedOverMetered(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, title);
            request.setVisibleInDownloadsUi(true);
            downloadId = dm.enqueue(request);
        }
        return downloadId;
    }


    /**
     * TODO 开始下载
     *
     * @param url      下载地址
     * @param path     存储地址
     * @param fileName 文件名
     */
    public void download(String url, String path, String fileName) {
        //需要处理
    }

    public class Build {

        private String url;
        private String path;
        private String fileName;
        private DownloadListener listener;

        public String getUrl() {
            return url;
        }

        public Build setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getPath() {
            return path;
        }

        public Build setPath(String path) {
            this.path = path;
            return this;
        }

        public String getFileName() {
            return fileName;
        }

        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public void build(DownloadListener listener) {
            this.listener = listener;
            //开始下载任务
            download(url, path, fileName);
        }
    }

    interface DownloadListener {

        void onStart();

        void onProgress(int progress);

        void onDone();

        void onFailure(String msg);
    }
}
