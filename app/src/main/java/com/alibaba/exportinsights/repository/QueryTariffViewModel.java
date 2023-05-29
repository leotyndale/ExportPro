package com.alibaba.exportinsights.repository;

import com.alibaba.base.App;
import com.alibaba.exportinsights.bean.BaseProperty;
import com.alibaba.exportinsights.bean.ElementTariffRequest;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.TariffBean;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QueryTariffViewModel extends ViewModel {

    MutableLiveData<TariffBean> mTariffBeanLiveData = new MutableLiveData<>();

    MutableLiveData<List<Object>> mFlatTariff = new MutableLiveData<>();

    MutableLiveData<Keyword> mKeywordLiveData = new MutableLiveData<>();
    private final QueryTariffRepository mQueryTariffRepository;

    public QueryTariffViewModel() {
        mQueryTariffRepository = new QueryTariffRepository();
    }

    public MutableLiveData<TariffBean> getTariffBeanLiveData() {
        return mTariffBeanLiveData;
    }

    public MutableLiveData<List<Object>> getFlatTariff() {
        return mFlatTariff;
    }

    public MutableLiveData<Keyword> getKeywordLiveData() {
        return mKeywordLiveData;
    }

    public void request(String name, List<BaseProperty> basePropertyValues, List<String> hsCodes) {
        ElementTariffRequest elementTariffRequest = new ElementTariffRequest();
        elementTariffRequest.name = name;
        elementTariffRequest.isoCode = App.isoCode();
        elementTariffRequest.propertyValues = basePropertyValues;
        elementTariffRequest.hsCodes = hsCodes;
        mQueryTariffRepository.request(elementTariffRequest, mTariffBeanLiveData);
    }
}
