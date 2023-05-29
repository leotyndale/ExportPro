package com.alibaba.base.net;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtil {
    private static final Gson mGson = new Gson();

    private GsonUtil() {
    }

    public static Gson getGson() {
        return mGson;
    }

    public static JSONObject toJSONObject(Object obj) throws JSONException {
        String jsonString = getGson().toJson(obj);
        return new JSONObject(jsonString);
    }

}
