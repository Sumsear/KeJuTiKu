package com.example.hp.keju;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;


import com.example.hp.keju.entity.DaoMaster;
import com.example.hp.keju.entity.DaoSession;
import com.example.hp.keju.util.LogUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class KeJuApplication extends Application {

    private DaoSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化 BMOB
        BmobConfig bmobConfig = new BmobConfig.Builder(this)
                .setApplicationId("ae88526cf2cc64f644da86d15c15669c")
                .setConnectTimeout(15)//单位为秒
                .build();
        Bmob.initialize(bmobConfig);

        LogUtil.setEnable(BuildConfig.DEBUG);

        initDataBase();

        initOCR();
    }

    private void initDataBase() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "questions_db");
        SQLiteDatabase db = helper.getWritableDatabase();
        mSession = new DaoMaster(db).newSession();
    }

    private void initOCR(){

        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {

            }

            @Override
            public void onError(OCRError ocrError) {

            }
        }, this);
    }

    public DaoSession getSession() {
        return mSession;
    }
}
