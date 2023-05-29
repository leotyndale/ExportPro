package com.alibaba.exportinsights.bean;

import java.util.List;

public class TrendInfo {
    private boolean success;
    private List<Object> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
