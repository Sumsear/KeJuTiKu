package com.example.hp.keju.http;

import android.util.ArrayMap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestManager {

    private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static RequestManager mInstance;
    private ExecutorService executor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * 2, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
    private ArrayMap<Object, BaseRequest> mTags = new ArrayMap<>();

    private RequestManager() {
    }

    public static RequestManager getManager() {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                mInstance = new RequestManager();
            }
        }
        return mInstance;
    }

    /**
     * TODO 执行
     */
    public void preform() {

    }

    /**
     * TODO 取消
     */
    public void cancel(Object tag) {
        BaseRequest request = mTags.get(tag);
        if (request != null) request.cancel();
    }
}
