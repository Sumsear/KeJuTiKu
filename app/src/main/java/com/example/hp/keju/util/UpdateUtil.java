package com.example.hp.keju.util;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUtil {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mUrl;
    private String mPath;
    private String mFileName;
    private DownloadListener mListener;

    public UpdateUtil(String url, String path, String fileName, DownloadListener listener) {
        this.mUrl = url;
        this.mPath = path;
        this.mFileName = fileName;
        this.mListener = listener;
    }

    /**
     * TODO 执行
     */
    public void excute() {

        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection conn = null;
                BufferedInputStream buf = null;
                FileOutputStream fos = null;
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onStart();
                        }
                    });
                    String absolute;
                    //文件操作
                    File path = new File(mPath);
                    if (!path.exists()) {
                        LogUtil.e(path.mkdirs() + "");
                    }
                    if (mPath.length() - mPath.lastIndexOf("/") != 1) {
                        absolute = mPath + "/" + mFileName;
                    } else {
                        absolute = mPath + mFileName;
                    }
                    File file = new File(absolute);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    //准备网络请求
                    URL url = new URL(mUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setDefaultUseCaches(true);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.connect();
                    int code = conn.getResponseCode();
                    LogUtil.e("response code = " + code);
                    if (code != 200) {
                        return;
                    }
                    buf = new BufferedInputStream(conn.getInputStream());
                    fos = new FileOutputStream(file);
                    final long len = conn.getContentLength();
                    byte[] bytes = new byte[1024];
                    int offset;
                    long size = 0;//必须用long类型，否则文件过大的情况下计算的时候会超出int的限制
                    int progress = 0;
                    while ((offset = buf.read(bytes)) > 0) {
                        fos.write(bytes, 0, offset);
                        size += offset;
                        final int finalSize = (int)(size * 100 / len);
                        if (progress != finalSize) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onProgress(finalSize);
                                }
                            });
                        }
                        progress = finalSize;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mListener.onFailure(e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onDone();
                        }
                    });
                } finally {
                    if (conn != null) conn.disconnect();
                    try {
                        if (buf != null) buf.close();
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onDone();
                    }
                });
            }
        });
        task.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, final Throwable e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onFailure(e.getMessage());
                    }
                });
            }
        });
        task.start();
    }


    public static class Builder {

        private String url;
        private String path;
        private String fileName;
        private DownloadListener listener;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public void setListener(DownloadListener listener) {
            this.listener = listener;
        }

        public UpdateUtil build() {
            //开始下载任务
            return new UpdateUtil(url, path, fileName, listener);
        }
    }

    public interface DownloadListener {

        void onStart();

        void onProgress(int progress);

        void onDone();

        void onFailure(String msg);
    }
}
