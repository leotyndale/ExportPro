package com.alibaba.base;

import android.app.Application;

public class App extends Application {

    private static String mIsoCode = "CHN";
    private static App mInstance;

    public static App context() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static void setIsoCode(String isoCode) {
        App.mIsoCode = isoCode;
    }

    public static String isoCode() {
        return mIsoCode;
    }
}
