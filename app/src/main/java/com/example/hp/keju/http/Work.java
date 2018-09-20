package com.example.hp.keju.http;

import android.support.annotation.NonNull;

import com.example.hp.keju.callback.RequestCallback;
import com.example.hp.keju.util.LogUtil;

import java.util.concurrent.FutureTask;

public class Work extends FutureTask<String> implements Canceller{

    private RequestCallback<String> mCallback;
    private BaseRequest mRequest;

    public Work(@NonNull BaseRequest request, RequestCallback<String> callback) {
        super(request);
        this.mRequest = request;
        this.mCallback = callback;
    }

    @Override
    protected void done() {
        super.done();
        try {
            String result = get();
            mCallback.success(200, result);
        } catch (Exception e) {
            if (isCancelled()){
                LogUtil.e("HttpUtil", "connection is canceled!");
                mCallback.onCancel();
            }
        }
    }


    @Override
    public void cancel() {
        cancel(true);
        mRequest.cancel();
    }
}
