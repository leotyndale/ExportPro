package com.alibaba.base.net;

import android.content.Context;
import android.util.Log;

import com.alibaba.base.App;
import com.alibaba.base.bean.Body;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

public class Volley {
    private final RequestQueue mRequestQueue;

    private static class VolleyManagerHolder {
        private static final Volley INSTANCE = new Volley(App.context());
    }

    public static Volley obtain() {
        return VolleyManagerHolder.INSTANCE;
    }

    private Volley(Context context) {
        mRequestQueue = com.android.volley.toolbox.Volley.newRequestQueue(context);
    }

    private <T> Request<T> add(Request<T> request) {
        return mRequestQueue.add(request);
    }

    public StringRequest string(Object tag, String url,
                                Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(url, listener, errorListener);
        request.setTag(tag);
        add(request);
        return request;
    }

    public StringRequest string(Object tag, int method, String url,
                                Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(method, url, listener, errorListener);
        request.setTag(tag);
        add(request);
        return request;
    }

    public <T> GsonRequest<T> get(String url, Class<T> clazz, Response.Listener<T> listener) {
        return get(null, null, url, clazz, listener, null);
    }

    public <T> GsonRequest<T> get(Object tag, Map<String, String> params, String url, Class<T> clazz, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        GsonRequest<T> request = new GsonRequest<>(url, clazz, listener, errorListener);
        request.setTag(tag);
        add(request);
        return request;
    }

    public <T> GsonRequest<T> get(String url, Map<String, String> params, Class<T> clazz, Response.Listener<T> listener) {
        GsonRequest<T> request = new GsonRequest<>(Request.Method.GET, params, url, clazz, listener, null);
        request.setTag(null);
        add(request);
        return request;
    }

    public <T> void post(String url, Map<String, String> params, Class<T> cls, Response.Listener<T> listener) {
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {
            Type type = TypeToken.getParameterized(Body.class, cls).getType();
            try {
                Body<T> body = GsonUtil.getGson().fromJson(response.toString(), type);
                T model = null;
                if (body != null) {
                    model = body.getModel();
                }
                listener.onResponse(model);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, getErrorListener());
        jsonObjectRequest.setTag(null);
        add(jsonObjectRequest);
    }

    private static Response.ErrorListener getErrorListener() {
        return error -> Log.e("onErrorResponse", error.toString());
    }

    public <T> GsonRequest<T> post(Object tag, Map<String, String> params, String url,
                                   Class<T> clazz, Response.Listener<T> listener,
                                   Response.ErrorListener errorListener) {
        GsonRequest<T> request = new GsonRequest<>(Request.Method.POST, params, url, clazz, listener, errorListener);
        request.setTag(tag);
        add(request);
        return request;
    }

    public void post(Object tag, String url, JSONObject jsonObject,
                     Response.Listener<JSONObject> listener,
                     Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                listener, errorListener);
        jsonObjectRequest.setTag(tag);
        add(jsonObjectRequest);

    }

    public void cancel(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
