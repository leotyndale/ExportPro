package com.alibaba.exportinsights.repository;


import com.alibaba.exportinsights.bean.ElementTariffRequest;
import com.alibaba.exportinsights.bean.TariffBean;
import com.alibaba.base.net.Volley;
import com.alibaba.exportinsights.bean.Result;
import com.alibaba.base.net.GsonUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;

import androidx.lifecycle.MutableLiveData;

public class QueryTariffRepository {

    public void request(ElementTariffRequest elementTariffRequest, MutableLiveData<TariffBean> tariffBeanMutableLiveData) {
        try {
            Volley.obtain().post(Urls.QUERY_TARIFF, Urls.QUERY_TARIFF, GsonUtil.toJSONObject(
                            elementTariffRequest),
                    response -> {
                        Type type = new TypeToken<Result<TariffBean>>(){}.getType();
                        Result<TariffBean> tariffResult = GsonUtil.getGson().fromJson(response.toString(), type);
                        if (tariffResult.success) {
                            tariffBeanMutableLiveData.setValue(tariffResult.model);
                        }
                    }, error -> tariffBeanMutableLiveData.setValue(null));
        } catch (JSONException e) {
            tariffBeanMutableLiveData.setValue(null);
        }
    }
}
