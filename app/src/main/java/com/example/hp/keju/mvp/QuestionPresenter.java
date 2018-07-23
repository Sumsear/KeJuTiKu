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
    private List<QuestionEntity> questions = new ArrayList<>(1000);

    public QuestionPresenter(QuestionContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void initQuestions() {

        int offset = 0;
        int count = 500;
        initCount = count;
        getQuestionByBmob(offset, count);

    }

    @Override
    public void getQuestionsByLocal(String condition) {
        List<QuestionEntity> data = LocalQuestionCRUDUtil.getInstance(mView.getApp()).retrieve(condition);
        mView.showQuestions(data);
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
                    LogUtil.e("size" + questions.size());
//                    mView.showQuestions(questions);
//                    mView.showProgressBar(false);
//                    LocalQuestionCRUDUtil.getInstance(mView.getApp()).deleteAll();
                    LocalQuestionCRUDUtil.getInstance(mView.getApp()).create(questions);
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
        HttpUtil.getInstance().get(url, new RequestCallBack<List<QuestionEntity>>() {
            @Override
            public void success(int code, List<QuestionEntity> questions) {
                BMobCRUDUtil.getInstance().create(questions, new RequestCallBack<Integer>() {
                    @Override
                    public void success(int code, Integer data) {
                        //添加成功
                    }

                    @Override
                    public void defeated(int code, String msg) {
                        //添加失败
                    }
                });
            }

            @Override
            public void defeated(int code, String msg) {

            }
        });
    }
}
