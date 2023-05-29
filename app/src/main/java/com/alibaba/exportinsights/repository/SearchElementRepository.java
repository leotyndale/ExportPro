package com.alibaba.exportinsights.repository;


import com.alibaba.exportinsights.bean.ElementBean;
import com.alibaba.exportinsights.bean.ElementTariffRequest;
import com.alibaba.base.net.Volley;
import com.alibaba.exportinsights.bean.Result;
import com.alibaba.base.net.GsonUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;

import androidx.lifecycle.MutableLiveData;

public class SearchElementRepository {

    public void request(ElementTariffRequest elementTariffRequest, MutableLiveData<ElementBean> elementBeanLiveData) {
        try {
            Volley.obtain().post(Urls.SEARCH_ELEMENT, Urls.SEARCH_ELEMENT, GsonUtil.toJSONObject(
                            elementTariffRequest),
                    response -> {
                        Type type = new TypeToken<Result<ElementBean>>(){}.getType();
                        Result<ElementBean> elementBeanResult = GsonUtil.getGson().fromJson(response.toString(), type);
                        if (elementBeanResult.success) {
                            elementBeanLiveData.setValue(elementBeanResult.model);
                        }
                    }, error -> elementBeanLiveData.setValue(null));
        } catch (JSONException e) {
            elementBeanLiveData.setValue(null);
        }
    }
}
