package com.alibaba.exportinsights.bean;

import java.util.List;

public class Questionnaire {
    // if need format
    public boolean needFormat;
    // name in chinese
    public String name;
    // name in english
    public String enName;
    // all options for this question
    public List<Option> values;
}
