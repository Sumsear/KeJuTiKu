package com.example.hp.keju.http;

import java.net.Proxy;

public class HttpConfig {


    private int readTimeout;
    private int connectTimeout;
    private Proxy proxy;

    private HttpConfig(Builder builder){
        this.readTimeout = builder.getReadTimeout() <= 0 ? 15000 : builder.getReadTimeout();
        this.connectTimeout = builder.getConnectTimeout()<=0 ? 15000 : builder.getConnectTimeout();
        this.proxy = builder.getProxy();
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

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder{

        private int readTimeout;
        private int connectTimeout;
        private Proxy proxy;

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

        public HttpConfig build(){
            return new HttpConfig(this);
        }
    }

}
