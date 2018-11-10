package com.hjq.demo.common;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.hjq.toast.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.xutils.x;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目中的Activity基类
 */
public class CommonApplication extends UIApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        initXutils();

        // 初始化吐司工具类
        ToastUtils.init(getApplicationContext());

        // 友盟统计
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }


    public void initXutils() {
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