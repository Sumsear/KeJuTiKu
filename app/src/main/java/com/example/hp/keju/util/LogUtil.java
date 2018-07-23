package com.example.hp.keju.util;


import android.text.TextUtils;
import android.util.Log;

import com.example.hp.keju.KeJuApplication;

public class LogUtil {

    private final static String TAG = KeJuApplication.class.getSimpleName();
    private static boolean isEnable = false;

    public static void i(String msg){
        if (!isEnable) return;
        i(TAG, msg);
    }

    public static void i(String tag, String msg){
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) return;
        Log.i(tag,msg);
    }

    public static void d(String msg){
        if (!isEnable) return;
        d(TAG, msg);
    }

    public static void d(String tag, String msg){
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) return;
        Log.d(tag,msg);
    }


    public static void e(String msg){
        if (!isEnable) return;
        e(TAG, msg);
    }

    public static void e(String tag, String msg){
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) return;
        Log.e(tag,msg);
    }

    public static void setEnable(boolean enable){
        isEnable = enable;
    }
}
