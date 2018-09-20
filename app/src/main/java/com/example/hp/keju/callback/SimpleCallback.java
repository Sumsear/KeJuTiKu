package com.example.hp.keju.callback;

public class SimpleCallback<T> implements RequestCallback<T> {

    @Override
    public void success(int code, T data) {

    }

    @Override
    public void failure(int code, String msg) {

    }

    @Override
    public void onCancel() {

    }
}
