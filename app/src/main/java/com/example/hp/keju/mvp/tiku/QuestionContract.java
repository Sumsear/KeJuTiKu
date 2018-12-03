package com.example.hp.keju.mvp.tiku;

import android.app.Application;

import com.example.hp.keju.entity.ErrorQuestionEntity;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.mvp.BasePresenter;
import com.example.hp.keju.mvp.BaseView;

import java.util.List;

public class QuestionContract {

    interface View extends BaseView<Presenter> {


        Application getApp();

        void showProgressBar(boolean show);

        void showQuestions(List<QuestionEntity> questions);

        void displayNotificationView(String content, boolean display);

    }

    interface Presenter extends BasePresenter {

        void initQuestions();

        void getQuestionsByLocal(String condition);

        void getQuestionsByLocal(List<String> conditions);

        void getQuestionsByDuoWan(String condition);

        void reportErrorQuestion(ErrorQuestionEntity question);

        void checkUpdate();
    }

}
