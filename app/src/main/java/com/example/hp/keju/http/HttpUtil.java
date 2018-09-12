package com.example.hp.keju.http;


import android.util.Log;

import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.util.LogUtil;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static HttpUtil instance;
    private static HttpConfig mConfig;
    private ExecutorService executor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * 2, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    private HttpUtil() {

    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) instance = new HttpUtil();
            }
        }
        return instance;
    }

    public static void setConfig(HttpConfig config) {
        if (mConfig == null) {
            synchronized (HttpUtil.class) {
                if (mConfig == null)
                    mConfig = config == null ? HttpConfig.newBuilder().build() : config;
                else
                    Log.w("HttpUtil", new IllegalStateException("Only allowed to configure once."));
            }
        }
    }

    public static HttpConfig getConfig(){
        setConfig(null);
        return mConfig;
    }

    public static Builder get(String url) {
        return new Builder();
    }

    public static void cancel(Object tag){

    }

    public void get(final String urlAddress, final RequestCallBack<String> callBack) {

        executor.execute(new Runnable() {
            @Override
            public void run() {

                StringBuilder sb = new StringBuilder();
                InputStreamReader is = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlAddress);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Language", "zh-CN");
                    conn.setRequestProperty("author", "evilwk");
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        is = new InputStreamReader(conn.getInputStream());
                        char[] buff = new char[1024];
                        int len = 0;
                        int flag = 0;
                        while ((len = is.read(buff, len, 10)) != -1) {
                            sb.append(new String(buff, flag, 10));
                            flag = len;
                        }
                        String result = sb.toString().substring(sb.toString().indexOf("{"), sb.lastIndexOf("}") + 1);
                        LogUtil.e("result", result);
                        callBack.success(code, result);
                    } else {
                        callBack.defeated(code, conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeInputStream(is);
                    disconnect(conn);
                }
            }
        });
    }

    public static class Builder {

        private ConcurrentHashMap<String, Object> params = new ConcurrentHashMap();
        private Object tag;

        public ConcurrentHashMap<String, Object> getParams() {
            return params;
        }

        public Builder params(String key, Object obj) {
            if (key != null && obj != null)
                this.params.put(key, obj);
            return this;
        }

        public Object getTag() {
            return tag;
        }

        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public void perform(RequestCallBack callBack){
            //执行网络请求

            HttpConnection conn = HttpConnectionFactory.build("");
            try {
                int code = conn.getResponseCode();
                InputStreamReader is = new InputStreamReader(conn.getInputStream());

            }catch (Exception e){

            }
        }
    }

    /**
     * TODO 注销
     */
    public void destroyed() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
    }


    /**
     * TODO 关闭输入流
     *
     * @param is 输入流
     */
    private void closeInputStream(InputStreamReader is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 关闭输出流
     *
     * @param os 输出流
     */
    private void closeOutputStream(OutputStreamWriter os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 关闭HttpUrlConnection网络连接
     *
     * @param conn HttpUrlConnection
     */
    private void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }
}
