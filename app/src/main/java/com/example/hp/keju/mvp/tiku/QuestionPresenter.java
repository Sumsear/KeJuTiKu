package com.example.hp.keju.mvp.tiku;


import android.text.TextUtils;

import com.example.hp.keju.BuildConfig;
import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.entity.ErrorQuestionEntity;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.entity.SearchEntity;
import com.example.hp.keju.entity.UpdateApplication;
import com.example.hp.keju.util.BMobCRUDUtil;
import com.example.hp.keju.http.HttpUtil;
import com.example.hp.keju.util.LocalQuestionCRUDUtil;
import com.example.hp.keju.util.LogUtil;
import com.google.gson.Gson;

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
            reportErrorQuestion(new ErrorQuestionEntity(condition));
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

    @Override
    public void reportErrorQuestion(ErrorQuestionEntity question) {

        if (question != null && !TextUtils.isEmpty(question.getQ())) {
            BMobCRUDUtil.getInstance().create(question, new RequestCallBack<String>() {
                @Override
                public void success(int code, String data) {
                    //错题上传成功
                    LogUtil.e("上报成功");
                }

                @Override
                public void defeated(int code, String msg) {
                    //错题上传失败
                    LogUtil.e(msg);
                }
            });
        }
    }

    @Override
    public void checkUpdate() {

        BMobCRUDUtil.getInstance().retrieveUpdateApp(new RequestCallBack<List<UpdateApplication>>() {
            @Override
            public void success(int code, List<UpdateApplication> data) {
                UpdateApplication ua = data.get(0);
                LogUtil.e(ua.getVersion() + " " + ua.getVersionInfo() + " " + ua.getDownload());
                if (BuildConfig.VERSION_CODE < ua.getVersionCode()) {
                    mView.displayNotificationView("发现新版本，点击更新！", true);
                }
            }

            @Override
            public void defeated(int code, String msg) {

            }
        });
    }


    /**
     * TODO
     *
     * @param offset
     * @param count
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
        HttpUtil.getInstance().get(url, new RequestCallBack<String>() {
            @Override
            public void success(int code, String str) {

                List<QuestionEntity> questions = analyse(str);
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

    /**
     * TODO 解析结果
     *
     * @param result json
     * @return list of result
     */
    private List<QuestionEntity> analyse(String result) {

        List<QuestionEntity> questions = new ArrayList<>();
        try {
            SearchEntity entity = new Gson().fromJson(result, SearchEntity.class);
            if ("ok".equals(entity.getStatus())) {
                questions.addAll(entity.getList());
                for (int i = 0; i < questions.size(); i++) {
                    QuestionEntity e = questions.get(i);
                    LogUtil.e("question", e.getQ());
                    LogUtil.e("answer", e.getA());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
