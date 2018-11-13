package com.hjq.demo.common;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.hjq.demo.Constant;
import com.hjq.toast.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.xutils.x;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目中的Activity基类
 */
public class CommonApplication extends UIApplication {

    private Context that;

    @Override
    public void onCreate() {
        super.onCreate();
        that = this;

        initXutils();
        initUmeng();
        // 初始化吐司工具类
        ToastUtils.init(getApplicationContext());


    }

    //友盟
    void initUmeng() {
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        initAnalytics(); //统计
        initPush(); //推送
        //第三方登录分享 APPID, APPKEY
        PlatformConfig.setWeixin(Constant.WXAPP_ID, Constant.WXAPP_SECRET);
        PlatformConfig.setQQZone(Constant.QQAPP_ID, Constant.QQAPP_SECRET);
    }

    void initAnalytics() {
        // 友盟统计
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    void initPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
//                Log.d("deviceToken", "onSuccess: " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d("deviceToken", "onFailure: " + s + "--" + s1);
            }
        });
    }

    void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志, 开启debug会影响性能.
    }

    // 解决函数超过65535时的分包问题
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}