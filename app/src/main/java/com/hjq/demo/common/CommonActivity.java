package com.hjq.demo.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目中的Activity基类
 */
public abstract class CommonActivity extends UIActivity
        implements OnTitleBarListener {

    public Activity thisAct;


    @Override
    public void init() {

        //初始化标题栏的监听
        if (getTitleBarId() > 0) {
            if (findViewById(getTitleBarId()) instanceof TitleBar) {
                ((TitleBar) findViewById(getTitleBarId())).setOnTitleBarListener(this);
            }
        }

        x.view().inject(this);
        thisAct = this;
        MobclickAgent.onEvent(thisAct, getClass().getSimpleName());

        initOrientation();

        super.init();
    }

    /**
     * 初始化横竖屏方向，会和 LauncherTheme 主题样式有冲突，注意不要同时使用
     */
    protected void initOrientation() {
        //如果没有指定屏幕方向，则默认为竖屏
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TitleBar titleBar = getTitleBar();
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    protected TitleBar getTitleBar() {
        if (getTitleBarId() > 0 && findViewById(getTitleBarId()) instanceof TitleBar) {
            return findViewById(getTitleBarId());
        }
        return null;
    }

    @Override
    public boolean statusBarDarkFont() {
        //返回true表示黑色字体
        return true;
    }

    /**
     * {@link OnTitleBarListener}
     */

    /**
     * 标题栏左边的View被点击了
     */
    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }


    /**
     * 标题栏中间的View被点击了
     */
    @Override
    public void onTitleClick(View v) {
    }

    /**
     * 标题栏右边的View被点击了
     */
    @Override
    public void onRightClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 手动统计页面
        MobclickAgent.onPageStart(getClass().getSimpleName());
        // 友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 手动统计页面，必须保证 onPageEnd 在 onPause 之前调用，因为SDK会在 onPause 中保存onPageEnd统计到的页面数据
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        // 友盟统计
        MobclickAgent.onPause(this);
    }


    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

//    EventBus 使用示例
//    EventBus.getDefault().post(event); //发送事件
//    重写isRegisterEventBus方法并返回true实现注册订阅者
//    protected boolean isRegisterEventBus() { return true; }
//    在子类调用接受事件
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventReceived(MessageEvent<User> event) {
//        if (event != null && event.getCode() == 0) {
//            User user = event.getData();
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}