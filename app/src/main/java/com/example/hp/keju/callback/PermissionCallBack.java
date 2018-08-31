package com.example.hp.keju.callback;

public interface PermissionCallBack {

    void granted(int code, String permission);

    void denied(int code, String permission);
}
