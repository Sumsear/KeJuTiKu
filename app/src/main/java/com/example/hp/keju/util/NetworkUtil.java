package com.example.hp.keju.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    /**
     * TODO 检测网络
     *
     * @param context 上下文
     * @return true if network is active
     */
    public static boolean checkNetwork(Context context) {

        Context app = context.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isAvailable());
        }
        return false;
    }

    /**
     * TODO check network type
     *
     * @param context 上下文
     * @return if the network is not available return -1 otherwise return network type
     */
    public int checkNetworkType(Context context) {
        Context app = context.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * TODO check zhe network type is wifi
     *
     * @param context 上下文
     * @return true if zhe network type is wifi
     */
    public boolean isWifi(Context context) {
        return checkNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }


    /**
     * TODO check zhe network type is wifi
     *
     * @param context 上下文
     * @return true if zhe network type is wifi
     */
    public boolean isMobileData(Context context) {
        return checkNetworkType(context) == ConnectivityManager.TYPE_MOBILE ||
                checkNetworkType(context) == ConnectivityManager.TYPE_MOBILE_DUN;
    }

}
