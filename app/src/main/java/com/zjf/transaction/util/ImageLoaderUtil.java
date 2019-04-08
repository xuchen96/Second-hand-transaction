package com.zjf.transaction.util;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.zjf.transaction.R;
import com.zjf.transaction.util.glide.GlideApp;


/**
 * Created by zjfcabbage on 2019/2/6
 *
 * @author 糟老头子 zjfcabbage
 */
public class ImageLoaderUtil {
    public static void loadImage(ImageView imageView, String imageUrl) {
        if (imageView == null || TextUtils.isEmpty(imageUrl)) {
            return;
        }
        GlideApp.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.icon_placeholder)
                .into(imageView);
    }
    public static void loadImage(ImageView imageView, @DrawableRes @NonNull int drawableId) {
        if (imageView == null) {
            return;
        }
        GlideApp.with(imageView.getContext())
                .load(drawableId)
                .placeholder(R.drawable.icon_placeholder)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, Bitmap bitmap) {
        if (imageView == null) {
            return;
        }
        GlideApp.with(imageView.getContext())
                .load(bitmap)
                .placeholder(R.drawable.icon_placeholder)
                .into(imageView);
    }
}
