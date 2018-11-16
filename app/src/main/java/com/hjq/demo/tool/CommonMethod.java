package com.hjq.demo.tool;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.hjq.baselibrary.listener.OnStringCallBack;
import com.squareup.picasso.Picasso;

public class CommonMethod {

    //图片显示
    public static void displayImage(Context context, String url, ImageView imageView) {
        Picasso.with(context).load(url).into(imageView);
    }

    //文件上传
    public static void uploadFile(String uploadToken, String uploadFilePath, OnStringCallBack callBack) {
        QiniuUtils.upload(uploadToken, uploadFilePath, callBack);
    }


    /**
     * 获取AndroidManifest中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }

        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.get(key) + "";
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }


}
