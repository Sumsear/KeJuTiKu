package com.example.hp.keju.http;

import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleRequest implements Runnable, BaseRequest {

    private String mUrl;
    private volatile HttpConnection conn;

    public SimpleRequest(String url) {
        this.mUrl = url;
        conn = HttpConnectionFactory.build(mUrl);

    }

    @Override
    public void run() {

        StringBuilder sb = new StringBuilder();
        try {
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStreamReader is = new InputStreamReader(conn.getInputStream());
                while (is.read() > 0){
                    char[] buff = new char[1024];
                    int len = 0;
                    int flag = 0;
                    while ((len = is.read(buff, len, 10)) != -1) {
                        sb.append(new String(buff, flag, 10));
                        flag = len;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
