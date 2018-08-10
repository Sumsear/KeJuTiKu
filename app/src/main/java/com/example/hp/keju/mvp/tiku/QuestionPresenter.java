package com.example.hp.keju.mvp.tiku;


import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.util.BMobCRUDUtil;
import com.example.hp.keju.util.HttpUtil;
import com.example.hp.keju.util.LocalQuestionCRUDUtil;
import com.example.hp.keju.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class QuestionPresenter implements QuestionContract.Presenter {

    private QuestionContract.View mView;
    private volatile int initCount;//多线程调用，volatile 防止出现并发问题
    private final List<QuestionEntity> questions = new ArrayList<>(1000);

    public QuestionPresenter(QuestionContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void initQuestions() {

        mView.showProgressBar(true);
        int offset = 0;
        int count = 500;
        initCount = count;
        synchronized (questions) {
            questions.clear();
            getQuestionByBmob(offset, count);
        }
    }

    @Override
    public void getQuestionsByLocal(String condition) {
        mView.showProgressBar(true);
        //长度大于5  去掉首尾，防止首尾文字识别错误
        if (condition.length() > 5) {
            condition = condition.substring(1, condition.length() - 1);
        }
        //去掉首尾后检测字符串首端字符是否是逗号，如果是去掉
        if (condition.indexOf(",") == 0) {
            condition = condition.substring(1, condition.length());
        }
        //检测字符串末尾字符是否是逗号，如果是去掉
        if (condition.lastIndexOf(",") == condition.length() - 1) {
            condition = condition.substring(0, condition.length() - 1);
        }
        //检测字符串末尾字符是否是问号，如果是去掉
        if (condition.lastIndexOf("?") == condition.length() - 1) {
            condition = condition.substring(0, condition.length() - 1);
        }
        LogUtil.e("condition", condition);
        List<QuestionEntity> data = LocalQuestionCRUDUtil.getInstance(mView.getApp()).retrieve(condition);

        if (data.size() <= 0) {
            mView.showToast("没有查询到试题的答案，施主还是自强吧！");
        } else {
            mView.showToast("数量:" + data.size());
        }
        mView.showQuestions(data);
        mView.showProgressBar(false);
    }

    @Override
    public void getQuestionsByDuoWan(String condition) {
        getQuestionByDuoWan(condition);
    }


    /**
     * TODO
     *
     * @param offset
     * @param count
     */
    private void getQuestionByBmob(final int offset, final int count) {
        //服务端查询问题以及答案
        BMobCRUDUtil.getInstance().retrieve(offset, count, new RequestCallBack<List<QuestionEntity>>() {
            @Override
            public void success(int code, List<QuestionEntity> data) {
                initCount = data.size();
                questions.addAll(data);
                if (initCount == count) {
                    getQuestionByBmob(offset + count, count);
                } else {
                    LocalQuestionCRUDUtil.getInstance(mView.getApp()).create(questions);
                    mView.showProgressBar(false);
                    mView.showToast("共加载题目: " + questions.size());
                }
            }

            @Override
            public void defeated(int code, String msg) {
                mView.showProgressBar(false);
            }
        });
    }

    /**
     * TODO 查找DuoWan数据
     *
     * @param q question
     */
    private void getQuestionByDuoWan(String q) {
        String url = String.format("http://tool.duowan.com/jx3/ui/exam/ex.php?s=1&q=%s&_=" + System.currentTimeMillis(), q);
        LogUtil.e("getQuestionByDuoWan", url);
        HttpUtil.getInstance().get(url, new RequestCallBack<List<QuestionEntity>>() {
            @Override
            public void success(int code, List<QuestionEntity> questions) {

                //显示题目
                mView.showQuestions(questions);
                //将题目添加到题库
                BMobCRUDUtil.getInstance().create(questions, new RequestCallBack<Integer>() {
                    @Override
                    public void success(int code, Integer data) {
                        //添加成功 暂时不做处理
                    }

                    @Override
                    public void defeated(int code, String msg) {
                        //添加失败 暂时不做处理
                    }
                });
            }

            @Override
            public void defeated(int code, String msg) {
                //查询失败 暂时不做处理
                mView.showToast("没有查询到试题的答案，施主还是自强吧！");
            }
        });
    }
}
