package com.example.hp.keju.http;

import java.net.Proxy;

public class HttpConfig {


    private long readTimeOut;
    private long writeTimeOut;
    private Proxy proxy;

    private HttpConfig(Builder builder){
        this.readTimeOut = builder.getReadTimeOut() <= 0 ? 15000 : builder.getReadTimeOut();
        this.writeTimeOut = builder.getWriteTimeOut()<=0 ? 15000 : builder.getWriteTimeOut();
        this.proxy = builder.getProxy();
    }

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public long getWriteTimeOut() {
        return writeTimeOut;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder{

        private long readTimeOut;
        private long writeTimeOut;
        private Proxy proxy;

        public long getReadTimeOut() {
            return readTimeOut;
        }

        public Builder setReadTimeOut(long readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public long getWriteTimeOut() {
            return writeTimeOut;
        }

        public Builder setWriteTimeOut(long writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
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
