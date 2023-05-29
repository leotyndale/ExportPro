package com.alibaba.exportinsights.repository;

import com.alibaba.exportinsights.bean.Property;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TagViewModel extends ViewModel {

    private final MutableLiveData<List<Property>> mTags = new MutableLiveData<>();

    public MutableLiveData<List<Property>> getTags() {
        return mTags;
    }

    public void setTags(List<Property> tags) {
        mTags.setValue(tags);
    }
}
