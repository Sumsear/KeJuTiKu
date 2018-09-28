package com.example.hp.keju.mvp;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.example.hp.keju.BuildConfig;
import com.example.hp.keju.callback.PermissionCallback;
import com.example.hp.keju.util.CustomToast;

import java.lang.ref.SoftReference;


public abstract class BaseActivity extends AppCompatActivity {

    public MyHandler mHandler = new MyHandler(this);
    private PermissionCallback mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * TODO show  toast
     *
     * @param msg message
     */
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Message message = new Message();
            message.what = 10086;
            Bundle data = new Bundle();
            data.putString("msg", msg);
            message.setData(data);
            mHandler.sendMessage(message);
        }
    }

    /**
     * TODO 申请权限
     *
     * @param permission 权限
     * @param code       请求码
     * @param callBack   回调
     */
    public void requestPermission(String permission, int code, PermissionCallback callBack) {

        if (callBack == null) return;
        mCallBack = callBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{permission}, code);
            } else {
                mCallBack.granted(code, permission);
            }
        }
    }

    /**
     * TODO 申请权限
     *
     * @param permission 权限
     * @param code       请求码
     * @param callBack   回调
     */
    public void requestPermission(String[] permission, int code, PermissionCallback callBack) {

        if (callBack == null) return;
        mCallBack = callBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] temp = new String[permission.length];
            int index = 0;
            for (String p : permission) {
                if (checkSelfPermission(p) == PackageManager.PERMISSION_DENIED) {
                    temp[index] = p;
                    index++;
                } else {
                    mCallBack.granted(code, p);
                }
            }
            if (index > 0) {
                requestPermissions(temp, code);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                mCallBack.granted(requestCode, permissions[i]);
            } else {
                mCallBack.denied(requestCode, permissions[i]);
            }
        }
    }

    /**
     * TODO 处理消息
     *
     * @param activity activity
     * @param msg      messag
     */
    public void handleMessage(Activity activity, Message msg) {
    }

    /**
     * TODO 线程间通信
     */
    public static class MyHandler extends Handler {

        SoftReference<BaseActivity> ref;

        MyHandler(BaseActivity activity) {
            this.ref = new SoftReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity ac = ref.get();
            if (ac != null) {
                switch (msg.what) {
                    case 10086:
                        String m = msg.getData().getString("msg");
                        CustomToast.show(ac, m);
                        break;
                    default:
                        ac.handleMessage(ac, msg);
                        break;
                }
            }
        }
    }
}
