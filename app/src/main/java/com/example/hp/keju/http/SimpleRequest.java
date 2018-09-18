package com.example.hp.keju.http;

import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.util.IOUtil;

import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleRequest extends BaseRequest {

    private volatile HttpConnection conn;
    private volatile RequestCallBack<String> mCallBack;

    public SimpleRequest(HttpUtil.Builder builder) {
        this.mCallBack = builder.getCallBack();
        this.url = builder.getUrl();
        this.params = builder.getParams();
        this.tag = builder.getTag();
        this.method = builder.getMethod();
        this.readTimeout = builder.getReadTimeout();
        this.connectTimeout = builder.getConnectTimeout();
        this.proxy = builder.getProxy();
    }

    @Override
    public void run() {
        conn = HttpConnectionFactory.build(this);
        StringBuilder sb = new StringBuilder();
        InputStreamReader is = null;
        try {
            int code = conn.getResponseCode();
            if (code == 200) {
                is = new InputStreamReader(conn.getInputStream());
                while (is.read() > 0) {
                    char[] buff = new char[1024];
                    int len = 0;
                    int flag = 0;
                    while ((len = is.read(buff, len, 10)) != -1) {
                        sb.append(new String(buff, flag, 10));
                        flag = len;
                    }
                }
                mCallBack.success(code, sb.toString());
            } else {
                mCallBack.failure(code, conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cancel();
            IOUtil.close(is);
            RequestManager.getManager().cancel(tag);
        }
    }

    @Override
    public void cancel() {
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
