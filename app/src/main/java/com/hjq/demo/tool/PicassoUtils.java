package com.hjq.demo.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * 图片加载工具
 * Created by 韩小呆 on 2017/12/10.
 */

public class PicassoUtils {
    /**
     * 默认加载图片
     */
    public static void Display(Context context, String url, ImageView imageView) {

        Picasso.with(context).load(url).into(imageView);
    }

    /**
     * 从res文件内部加载图片
     */
    public static void Display(Context context, int resourceId, ImageView imageView) {
        Picasso.with(context).load(resourceId).into(imageView);
    }

    /**
     * 网上加载指定图片大小的图片
     */
    public static void Display(Context context, String url, int width, int height, ImageView imageView) {
        Picasso.with(context).load(url).resize(width, height).centerCrop().into(imageView);
    }

    /**
     * res文件内部加载指定大小图片
     */
    public static void Display(Context context, int id, int width, int height, ImageView imageView) {
        Picasso.with(context).load(id).resize(width, height).centerCrop().into(imageView);
    }

    /**
     * 1、加载中图片，加载错误图片，加载正确图片并且设置
     * 2、当没有图片时，因此imageView
     */
    public static void loadImageViewHolder(Context context, String url, int loadingImage, int loadingErrorImage, ImageView imageView) {
        if (url != null && !TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).placeholder(loadingImage).error(loadingErrorImage).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    public static void loadImageViewHolderSize(Context context, String url, int width, int height, int loadingImage, int loadingErrorImage, ImageView imageView) {
        if (url != null && !TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).resize(width, height).centerCrop().placeholder(loadingImage).error(loadingErrorImage).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

    }


    /**
     * 裁剪图片
     */
    public static void loadImageViewCrop(Context context, String url, ImageView imageView) {
        Picasso.with(context).load(url).transform(new CropSquareTransformation()).into(imageView);
    }

    /**
     * 按比例裁剪图片
     */
    public static class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "crop";
        }
    }
}
