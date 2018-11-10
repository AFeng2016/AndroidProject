package com.hjq.demo.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目中Fragment懒加载基类
 */
public abstract class CommonLazyFragment extends UILazyFragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        x.view().inject(this, view);
        MobclickAgent.onEvent(getContext(), getClass().getSimpleName());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 友盟统计
        MobclickAgent.onResume(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        // 友盟统计
        MobclickAgent.onPause(getContext());
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

}