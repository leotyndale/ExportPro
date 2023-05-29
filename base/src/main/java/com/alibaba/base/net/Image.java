package com.alibaba.base.net;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.widget.ImageView;

import com.alibaba.base.App;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class Image {
    private static class ImageHolder {
        private static final Image INSTANCE = new Image();
    }

    public static Image obtain() {
        return ImageHolder.INSTANCE;
    }

    public void load(ImageView view, String imageUrl, int radiusDp) {
        Glide.with(App.context())
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(radiusDp))))
                .placeholder(new ColorDrawable(Color.parseColor("#4DDDDDDD")))
                .into(view);
    }

    public void load(Context context, ImageView view, String imageUrl) {
        Glide.with(context).load(imageUrl).into(view);
    }

    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                Resources.getSystem().getDisplayMetrics());
    }
}
