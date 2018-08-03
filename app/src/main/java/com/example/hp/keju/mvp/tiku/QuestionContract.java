package com.example.hp.keju.mvp.tiku;

import android.app.Application;

import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.mvp.BasePresenter;
import com.example.hp.keju.mvp.BaseView;

import java.util.List;

public class QuestionContract {

    interface View extends BaseView<Presenter> {


        Application getApp();

        void showProgressBar(boolean show);

        void showQuestions(List<QuestionEntity> questions);

    }

    interface Presenter extends BasePresenter {

        void initQuestions();

        void getQuestionsByLocal(String condition);

        void getQuestionsByDuoWan(String condition);
    }

}
