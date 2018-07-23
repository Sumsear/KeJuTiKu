package com.example.hp.keju.mvp;

import android.app.Application;

import com.example.hp.keju.entity.QuestionEntity;

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
    }

}
