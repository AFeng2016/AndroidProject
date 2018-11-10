package com.hjq.demo.tool;

import android.content.Context;
import android.telephony.TelephonyManager;

public class CommonMethod {

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
