package com.hjq.demo.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

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


}