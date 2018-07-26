package com.example.hp.keju.util;

import android.app.Application;

import android.text.TextUtils;

import com.example.hp.keju.KeJuApplication;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.entity.QuestionEntityDao;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class LocalQuestionCRUDUtil {

    private static LocalQuestionCRUDUtil instance;
    private QuestionEntityDao mDao;

    private LocalQuestionCRUDUtil(Application app) {
        mDao = ((KeJuApplication) app).getSession().getQuestionEntityDao();
    }

    public static LocalQuestionCRUDUtil getInstance(Application app) {
        if (instance == null) {
            synchronized (LocalQuestionCRUDUtil.class) {
                if (instance == null) instance = new LocalQuestionCRUDUtil(app);
            }
        }
        return instance;
    }

    /**
     * TODO 增
     *
     * @param question
     */
    public void create(QuestionEntity question) {
        mDao.insert(question);
    }

    /**
     * TODO 增
     *
     * @param questions
     */
    public void create(List<QuestionEntity> questions) {
        mDao.deleteAll();
        mDao.insertInTx(questions);
    }

    /**
     * TODO 删
     *
     * @param question QuestionEntity
     */
    public void delete(QuestionEntity question) {
        mDao.delete(question);
    }

    /**
     * TODO 删除所有数据
     */
    public void deleteAll() {
        mDao.deleteAll();
    }

    /**
     * TODO 改
     */
    public void update() {
    }

    /**
     * TODO 查
     */
    public List<QuestionEntity> retrieve() {
        return retrieve("");
    }

    /**
     * TODO 查
     *
     * @param condition 条件
     */
    public List<QuestionEntity> retrieve(String condition) {

        QueryBuilder<QuestionEntity> builder = mDao.queryBuilder();
        if (!TextUtils.isEmpty(condition)) {
            builder.where(QuestionEntityDao.Properties.Q.like("%" + condition + "%"));
        }
        return retrieve(builder);
    }

    /**
     * TODO 查
     *
     * @param builder QueryBuilder
     */
    public <T> List<T> retrieve(QueryBuilder<T> builder) {
        return builder.build().list();
    }


    public void destroy() {
        mDao.detachAll();
    }
}
