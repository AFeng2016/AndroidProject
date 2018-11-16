package com.hjq.demo.tool;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by AFeng on 2018/11/16.
 */
public class SystemInfoUtils {

    //获取本机IMEI
    public static String getImei(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imeiCode = "";

        try {
            imeiCode = manager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeiCode;
    }



}
