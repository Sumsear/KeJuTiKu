package com.example.hp.keju.mvp;


import android.app.Activity;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.example.hp.keju.util.CustomToast;

import java.lang.ref.SoftReference;


public abstract class BaseActivity extends AppCompatActivity{

    MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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
     * TODO 处理消息
     *
     * @param activity activity
     * @param msg      messag
     */
    void handleMessage(Activity activity, Message msg) {
    }

    /**
     * TODO 用来做线程间通信
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
                        CustomToast.show(ac,m);
                        break;
                    default:
                        ac.handleMessage(ac, msg);
                        break;
                }
            }
        }
    }
}
