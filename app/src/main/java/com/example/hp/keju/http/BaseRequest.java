package com.example.hp.keju.http;


import java.net.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseRequest implements Callable<String>, Canceller {

    String url;
    ConcurrentHashMap<String, Object> params;
    Object tag;
    RequestMethod method;
    int readTimeout;
    int connectTimeout;
    Proxy proxy;

    public String getUrl() {
        return url;
    }

    public ConcurrentHashMap<String, Object> getParams() {
        return params;
    }

    public Object getTag() {
        return tag;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Proxy getProxy() {
        return proxy;
    }

}
