package com.alibaba.exportinsights.bean;

import java.util.List;

public class ElementTariffRequest {
    public String name;
    public String isoCode;
    public List<BaseProperty> propertyValues;

    public List<String> hsCodes;
}
