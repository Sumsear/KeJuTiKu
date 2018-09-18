package com.example.hp.keju.http;

import com.example.hp.keju.util.LogUtil;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpConnectionFactory {

    public static HttpConnection build(BaseRequest request) {
        HttpURLConnection conn = null;
        try {

            Proxy proxy = request.getProxy();
            URL u = new URL(request.getUrl());

            if (proxy != null) {
                conn = (HttpURLConnection) u.openConnection(proxy);
            } else {
                LogUtil.e("xzh", u.toString());
                conn = (HttpURLConnection) u.openConnection();
            }
            conn.setReadTimeout(request.getReadTimeout());
            conn.setConnectTimeout(request.getConnectTimeout());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Language", "zh-CN");
            conn.setRequestMethod(request.getMethod().toString());
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HttpConnection(conn);
    }
}
