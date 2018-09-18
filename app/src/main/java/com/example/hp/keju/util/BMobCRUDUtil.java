package com.example.hp.keju.util;

import android.text.TextUtils;

import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.constant.Constants;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.entity.UpdateApplication;

import java.lang.reflect.Field;
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
     * TODO 单个增加
     *
     * @param entity   实例
     * @param callBack 回调
     */
    public <T extends BmobObject> void create(T entity, final RequestCallBack<String> callBack) {

        if (entity == null) {
            callBack.failure(Constants.GET_DATA_PARAMS_ERROR, "参数异常!");
            return;
        }

        //通过反射查看传入的对象是否有变量未赋值
        Class clz = entity.getClass();
        Field[] fields = clz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                Class<?> type = field.getType();
                if (type == String.class) {
                    String val = (String) field.get(entity);
                    if (TextUtils.isEmpty(val)) {
                        callBack.failure(Constants.GET_DATA_PARAMS_ERROR, "参数异常!");
                        return;
                    }
                } else if (type == List.class) {
                    List<?> val = (List<?>) field.get(entity);
                    if (val == null || val.size() == 0) {
                        callBack.failure(Constants.GET_DATA_PARAMS_ERROR, "参数异常!");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        entity.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    callBack.success(Constants.GET_DATA_SUCCESS, s);
                } else {
                    callBack.failure(e.getErrorCode(), e.getMessage());
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
    public <T extends BmobObject> void create(List<T> entities, final RequestCallBack<Integer> callBack) {

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
                    callBack.failure(e.getErrorCode(), e.getMessage());
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
    public void retrieveQuestion(int offset, int count, final RequestCallBack<List<QuestionEntity>> callBack) {

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
                    callBack.failure(code, msg);
                }
            }
        });
    }

    /**
     * TODO 查询应用更新信息
     *
     * @param callBack 回调
     */
    public void retrieveUpdateApp(final RequestCallBack<List<UpdateApplication>> callBack) {

        BmobQuery<UpdateApplication> bq = new BmobQuery<>();
        bq.findObjects(new FindListener<UpdateApplication>() {
            @Override
            public void done(List<UpdateApplication> list, BmobException e) {
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
                    callBack.failure(code, msg);
                }
            }
        });
    }

    public void update() {

    }

    public void delete() {

    }

}