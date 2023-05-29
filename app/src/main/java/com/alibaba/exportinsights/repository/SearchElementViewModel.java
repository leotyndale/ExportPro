package com.alibaba.exportinsights.repository;

import com.alibaba.base.App;
import com.alibaba.exportinsights.bean.ElementBean;
import com.alibaba.exportinsights.bean.BaseProperty;
import com.alibaba.exportinsights.bean.ElementTariffRequest;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchElementViewModel extends ViewModel {

    MutableLiveData<ElementBean> mElementBeanLiveData = new MutableLiveData<>();

    MutableLiveData<Boolean> mConfirmEnabled = new MutableLiveData<>();

    MutableLiveData<Boolean> mProgressBarShow = new MutableLiveData<>();

    private final SearchElementRepository mSearchElementRepository;

    public SearchElementViewModel() {
        mSearchElementRepository = new SearchElementRepository();
    }

    public MutableLiveData<ElementBean> getElementBeanLiveData() {
        return mElementBeanLiveData;
    }

    public MutableLiveData<Boolean> getConfirmEnabled() {
        return mConfirmEnabled;
    }

    public MutableLiveData<Boolean> getProgressBarShow() {
        return mProgressBarShow;
    }

    public void request(String name, List<BaseProperty> basePropertyValues) {
        ElementTariffRequest elementTariffRequest = new ElementTariffRequest();
        elementTariffRequest.name = name;
        elementTariffRequest.isoCode = App.isoCode();
        elementTariffRequest.propertyValues = basePropertyValues;
        mSearchElementRepository.request(elementTariffRequest, mElementBeanLiveData);
    }
}
