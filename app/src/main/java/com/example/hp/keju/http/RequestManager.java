package com.example.hp.keju.http;

import android.util.ArrayMap;

import com.example.hp.keju.callback.RequestCallback;
import com.example.hp.keju.util.LogUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestManager {

    private static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static RequestManager mInstance;
    private ExecutorService executor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT * 2, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
    private ArrayMap<Object, Canceller> mTags = new ArrayMap<>();

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
    public void preform(BaseRequest request, RequestCallback<String> callback) {
        mTags.put(request.getTag(), request);
        LogUtil.e(HttpUtil.TAG, request.getTag() + "");
        Work work = new Work(request, callback);
        mTags.put(request.getTag(), work);
        executor.execute(work);
    }

    /**
     * TODO 取消
     */
    public void cancel(Object tag) {
        Canceller request = mTags.get(tag);
        if (request != null) {
            request.cancel();
            mTags.remove(tag);
        }
    }
}
