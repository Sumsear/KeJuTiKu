package com.example.hp.keju.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.hp.keju.R;
import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.util.BMobCRUDUtil;
import com.example.hp.keju.util.CustomToast;
import com.example.hp.keju.util.LocalQuestionCRUDUtil;
import com.example.hp.keju.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class QIntentService extends IntentService {

    private static final String ACTION_INIT_QUESTION = "com.example.hp.keju.service.action.INIT_QUESTION";
    private static final int NOTIFICATION_ID = 10086;
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFY_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFY_NAME";

    private volatile int initCount;//多线程调用，volatile 防止出现并发问题
    private final List<QuestionEntity> questions = new ArrayList<>(1000);
    private NotificationManager nm;

    public QIntentService() {
        super("QIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (nm != null) {
            nm.cancel(NOTIFICATION_ID);
        }
    }

    /**
     * TODO 启动IntentService初始化题库，如果这个IntentService已经启动，那么初始化题库任务将会被加入到队列
     *
     * @see IntentService
     */
    public static void startInitQuestion(Context context) {
        Intent intent = new Intent(context, QIntentService.class);
        intent.setAction(ACTION_INIT_QUESTION);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_QUESTION.equals(action)) {
                showNotification("正在更新", false);
                initQuestion();
            }
        }
    }

    /**
     * TODO 发送通知
     */
    private void showNotification(String content, boolean isComplete) {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        nb.setAutoCancel(true);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setContentTitle("更新题库");
        nb.setContentText(content);
        if (!isComplete) {
            nb.setProgress(100, 100, true);
        }
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel nc = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                nm.createNotificationChannel(nc);
            }
            nm.notify(NOTIFICATION_ID, nb.build());
        }
    }

    /**
     * TODO 初始化本地题库
     */
    private void initQuestion() {
        int offset = 0;//从0开始
        int count = 500;//每次获取500条
        initCount = count;
        synchronized (questions) {
            questions.clear();
            getQuestionByBmob(offset, count);
        }
    }


    /**
     * TODO 获取BMob上的题目
     *
     * @param offset 偏移位置
     * @param count  获取条数
     */
    private void getQuestionByBmob(final int offset, final int count) {
        //服务端查询问题以及答案
        BMobCRUDUtil.getInstance().retrieveQuestion(offset, count, new RequestCallBack<List<QuestionEntity>>() {
            @Override
            public void success(int code, List<QuestionEntity> data) {
                initCount = data.size();
                questions.addAll(data);
                if (initCount == count) {
                    getQuestionByBmob(offset + count, count);
                } else {
                    LocalQuestionCRUDUtil.getInstance(QIntentService.this.getApplication()).create(questions);
                    showNotification(String.format("本次更新题目 %s 条", questions.size()), true);
                }
            }

            @Override
            public void defeated(int code, String msg) {

            }
        });
    }

}
