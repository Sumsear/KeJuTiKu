package com.example.hp.keju.constant;

import android.os.Environment;

public class Constants {

    public final static int GET_DATA_PARAMS_ERROR = 1000;
    public final static int GET_DATA_NetWORK_ERROR = 1001;
    public final static int GET_DATA_ANALYSE_ERROR = 1002;
    public final static int GET_DATA_NO_DATA = 1003;

    public final static int GET_DATA_SUCCESS = 2000;

    public final static String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.xzh.keju";
    public final static String DOWNLOAD = BASE_PATH + "/download";
    public final static String LOGS = BASE_PATH + "/logs";
}
