package com.example.hp.keju.callback;

public interface PermissionCallback {

    void granted(int code, String permission);

    void denied(int code, String permission);
}
