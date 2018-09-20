package com.example.hp.keju.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.hp.keju.callback.RequestCallback;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.util.BMobCRUDUtil;
import com.example.hp.keju.util.CustomToast;
import com.example.hp.keju.util.LocalQuestionCRUDUtil;

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

    private volatile int initCount;//多线程调用，volatile 防止出现并发问题
    private final List<QuestionEntity> questions = new ArrayList<>(1000);


    public QIntentService() {
        super("QIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                initQuestion();
            }
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
        BMobCRUDUtil.getInstance().retrieveQuestion(offset, count, new RequestCallback<List<QuestionEntity>>() {
            @Override
            public void success(int code, List<QuestionEntity> data) {
                initCount = data.size();
                questions.addAll(data);
                if (initCount == count) {
                    getQuestionByBmob(offset + count, count);
                } else {
                    LocalQuestionCRUDUtil.getInstance(QIntentService.this.getApplication()).create(questions);
                    CustomToast.show(QIntentService.this, String.format("本次更新题目 %s 条", questions.size()));
                }
            }

            @Override
            public void failure(int code, String msg) {

            }
        });
    }

}
