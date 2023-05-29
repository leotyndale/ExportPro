package com.alibaba.base.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.base.App;
import com.alibaba.base.net.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Cache {
    private static final String SP = "Cache";

    private Cache() {
    }

    private static final Cache instance = new Cache();
    private static SharedPreferences mSp = null;

    public static Cache obtain() {
        if (mSp == null) {
            mSp = App.context().getSharedPreferences(SP, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public void write(String key, String value) {
        mSp.edit().putString(key, value).apply();
    }

    public void write(String key, boolean value) {
        mSp.edit().putBoolean(key, value).apply();
    }

    public void write(String key, int value) {
        mSp.edit().putInt(key, value).apply();
    }

    public String read(String key) {
        return mSp.getString(key, "");
    }

    public boolean read(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    public int read(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    public <T> List<T> read(String key, Class<T> classType) {
        Type type = TypeToken.getParameterized(List.class, classType).getType();
        return GsonUtil.getGson().fromJson(read(key), type);
    }

    public <T> void write(String key, List<T> list) {
        write(key, GsonUtil.getGson().toJson(list));
    }


}
