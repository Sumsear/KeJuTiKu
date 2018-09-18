package com.example.hp.keju.callback;


public interface RequestCallBack<T> {

    void success(int code, T data);

    void failure(int code, String msg);
}
