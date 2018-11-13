package com.hjq.demo.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hjq.demo.R;

/**
 * Created by Administrator on 2016/3/14.
 */
public class LoadingDialog {
    private static PopupWindow pop;

    public static void show(Context context, View parent) {
        pop = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//这里设置宽高
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        pop.setContentView(view);
        pop.setOutsideTouchable(false);//点击pop外部的空间，可以取消pop
        pop.setBackgroundDrawable(new ColorDrawable(-(000000)));//需要这行，才能保证setOutsideTouchable生效
        pop.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public static void cancel() {
        pop.dismiss();
    }
}
