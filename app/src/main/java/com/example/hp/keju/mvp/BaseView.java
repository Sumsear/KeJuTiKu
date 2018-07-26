package com.example.hp.keju.mvp;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showToast(String msg);
}
