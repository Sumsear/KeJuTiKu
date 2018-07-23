package com.example.hp.keju.callback;


public interface RequestCallBack<T> {

    void success(int code, T data);

    void defeated(int code, String msg);
}
