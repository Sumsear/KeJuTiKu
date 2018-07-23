package com.example.hp.keju.util;

import android.text.TextUtils;

import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.constant.Constants;
import com.example.hp.keju.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

import cn.bmob.v3.listener.SaveListener;

public class BMobCRUDUtil {


    private static BMobCRUDUtil instance;


    private BMobCRUDUtil() {
    }

    public static BMobCRUDUtil getInstance() {
        if (instance == null) {
            synchronized (BMobCRUDUtil.class) {
                if (instance == null) instance = new BMobCRUDUtil();
            }
        }
        return instance;
    }

    /**
     * TODO 批量增加
     *
     * @param entity   实例
     * @param callBack 回调
     */
    public void create(QuestionEntity entity, final RequestCallBack<String> callBack) {

        if (entity == null || TextUtils.isEmpty(entity.getQ())
                || TextUtils.isEmpty(entity.getA())) {
            callBack.defeated(Constants.GET_DATA_PARAMS_ERROR, "参数异常!");
            return;
        }

        entity.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    callBack.success(Constants.GET_DATA_SUCCESS, s);
                } else {
                    callBack.defeated(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    /**
     * TODO 批量增加
     *
     * @param entities 实例集合
     * @param callBack 回调
     */
    public void create(List<QuestionEntity> entities, final RequestCallBack<Integer> callBack) {

        int size;
        if (entities == null || (size = entities.size()) == 0) {
            return;
        }

        List<BmobObject> datas = new ArrayList<>(size);
        datas.addAll(entities);
        new BmobBatch().insertBatch(datas).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {
                    callBack.success(Constants.GET_DATA_SUCCESS, list.size());
                } else {
                    callBack.defeated(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }


    /**
     * TODO 查
     *
     * @param offset   位移点
     * @param count    每次获取总数
     * @param callBack 回调
     */
    public void retrieve(int offset, int count, final RequestCallBack<List<QuestionEntity>> callBack) {

        BmobQuery<QuestionEntity> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(count);
        bmobQuery.addWhereGreaterThanOrEqualTo("id", offset);
        bmobQuery.addWhereLessThan("id", offset + count);
        bmobQuery.findObjects(new FindListener<QuestionEntity>() {
            @Override
            public void done(List<QuestionEntity> list, BmobException e) {
                LogUtil.e("size: " + list.size());
                if (e == null && list.size() > 0) {
                    callBack.success(Constants.GET_DATA_SUCCESS, list);
                } else {
                    int code;
                    String msg;
                    if (e != null) {
                        code = e.getErrorCode();
                        msg = e.getMessage();
                    } else {
                        code = Constants.GET_DATA_NO_DATA;
                        msg = "没有数据!";
                    }
                    LogUtil.e("result: " + code + msg);
                    callBack.defeated(code, msg);
                }
            }
        });

    }

    public void update() {

    }

    public void delete() {

    }

}
