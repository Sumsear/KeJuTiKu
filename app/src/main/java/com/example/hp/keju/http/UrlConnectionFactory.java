package com.example.hp.keju.http;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UrlConnectionFactory {

    public static URLConnection build(String url) {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Language", "zh-CN");
            conn.setRequestProperty("author", "evilwk");
            conn.setRequestMethod("GET");
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
