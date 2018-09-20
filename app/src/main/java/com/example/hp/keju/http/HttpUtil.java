package com.example.hp.keju.http;


import android.util.Log;

import com.example.hp.keju.callback.RequestCallback;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpUtil {

    public final static String TAG = HttpUtil.class.getSimpleName();
    private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static HttpConfig mConfig;
    private ExecutorService executor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * 2, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    private HttpUtil() {

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

    public static HttpConfig getConfig() {
        setConfig(null);
        return mConfig;
    }

    public static Builder get(String url) {
        return new Builder(url, RequestMethod.GET);
    }

    public static void cancel(Object tag) {
        RequestManager.getManager().cancel(tag);
    }

    public static class Builder {

        private String url;
        private ConcurrentHashMap<String, Object> params = new ConcurrentHashMap();
        private Object tag = new AtomicInteger().getAndIncrement();
        private RequestMethod method;
        private int readTimeout;
        private int connectTimeout;
        private Proxy proxy;

        Builder(String url, RequestMethod method) {
            HttpConfig config = getConfig();
            this.url = url;
            this.method = method;
            this.readTimeout = config.getReadTimeout();
            this.connectTimeout = config.getConnectTimeout();
            this.proxy = config.getProxy();
        }

        public String getUrl() {
            return url;
        }

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

        public RequestMethod getMethod() {
            return method;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public Builder setProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public void perform(RequestCallback<String> callback) {
            //执行网络请求
            if (RequestMethod.GET.toString().equals(method.toString())) {
                Set<String> keySet = params.keySet();
                Iterator<String> keys = keySet.iterator();
                int index = 0;
                while (keys.hasNext()) {
                    String key = keys.next();
                    String wildcard = index == 0 ? "?" : "&";
                    //这里有问题
                    url = url.concat(wildcard).concat(key).concat("=").concat("" + params.get(key));
                    index++;
                }
            }
            RequestManager.getManager().preform(new SimpleRequest(this), callback);
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
