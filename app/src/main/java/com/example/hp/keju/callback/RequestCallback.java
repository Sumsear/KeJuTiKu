package com.example.hp.keju.callback;


public interface RequestCallback<T> {

    void success(int code, T data);

    void failure(int code, String msg);

    void onCancel();
}
