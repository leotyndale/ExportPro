package com.alibaba.exportinsights.bean;

import java.util.List;

public class SearchName {
    private boolean success;
    private ModelBean model;
    private Object msgCode;
    private int httpStatusCode;
    private Object msgInfo;
    private Object mappingCode;
    private HeadersBean headers;
    private Object bizExtMap;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ModelBean getModel() {
        return model;
    }

    public void setModel(ModelBean model) {
        this.model = model;
    }

    public Object getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(Object msgCode) {
        this.msgCode = msgCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Object getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(Object msgInfo) {
        this.msgInfo = msgInfo;
    }

    public Object getMappingCode() {
        return mappingCode;
    }

    public void setMappingCode(Object mappingCode) {
        this.mappingCode = mappingCode;
    }

    public HeadersBean getHeaders() {
        return headers;
    }

    public void setHeaders(HeadersBean headers) {
        this.headers = headers;
    }

    public Object getBizExtMap() {
        return bizExtMap;
    }

    public void setBizExtMap(Object bizExtMap) {
        this.bizExtMap = bizExtMap;
    }

    public static class ModelBean {
        private List<NameListBean> nameList;

        public List<NameListBean> getNameList() {
            return nameList;
        }

        public void setNameList(List<NameListBean> nameList) {
            this.nameList = nameList;
        }

        public static class NameListBean {
            private String name;
            private String enName;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getEnName() {
                return enName;
            }

            public void setEnName(String enName) {
                this.enName = enName;
            }
        }
    }

    public static class HeadersBean {
    }
}
