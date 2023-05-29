package com.alibaba.exportinsights.bean;

import java.io.Serializable;

public class Keyword implements Serializable {
    private String en;
    private String zh;

    public Keyword(String en, String zh) {
        this.en = en;
        this.zh = zh;
    }

    public String getEn() {
        return en;
    }

    public String getZh() {
        return zh;
    }
}
