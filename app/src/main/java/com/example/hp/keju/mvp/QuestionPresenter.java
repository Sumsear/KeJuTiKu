package com.example.hp.keju.mvp;


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
    private int initCount;
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
        if (condition.indexOf(",") == 0) {
            condition = condition.replaceFirst(",", "");
        }
        if (condition.lastIndexOf("?") == condition.length() - 1) {
            condition = condition.substring(0, condition.length() - 1);
        }
        LogUtil.e("condition", condition);
        List<QuestionEntity> data = LocalQuestionCRUDUtil.getInstance(mView.getApp()).retrieve(condition);
        mView.showToast("数量:" + data.size());
        mView.showQuestions(data);
        mView.showProgressBar(false);
    }

    @Override
    public void getQuestionsByDuoWan(String condition) {
        getQuestionByDuoWan(condition);
    }


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
