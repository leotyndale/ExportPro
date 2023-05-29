package com.alibaba.base.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {
    private static float mDeviceDensity = -1;
    private static float mDeviceScaleDensity = -1;

    private static void checkDeviceDensity(Context context) {
        if (mDeviceDensity == -1) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            mDeviceDensity = displayMetrics.density;
            mDeviceScaleDensity = displayMetrics.scaledDensity;
        }
    }

    /**
     * transfer dp to px by phone resolution
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        checkDeviceDensity(context);
        return (int) (dpValue * mDeviceDensity + 0.5f);
    }

    /**
     * transfer px to dp by phone resolution
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            return 0;
        }
        checkDeviceDensity(context);
        return (int) (pxValue / mDeviceDensity + 0.5f);
    }

    /**
     * transfer sp to px by phone resolution
     */
    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            return 0;
        }
        checkDeviceDensity(context);
        return (int) (spValue * mDeviceScaleDensity + 0.5f);
    }
}
