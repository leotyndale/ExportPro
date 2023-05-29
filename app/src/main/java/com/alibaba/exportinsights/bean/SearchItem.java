package com.alibaba.exportinsights.bean;

import java.util.List;

public class SearchItem {
    private List<ModelBean> model;

    public List<ModelBean> getModel() {
        return model;
    }

    public void setModel(List<ModelBean> model) {
        this.model = model;
    }

    public static class ModelBean {
        private String hsCode;
        private String hsCodeCN;
        private String imageUrl;
        private String name;
        private String desc;

        public String getHsCode() {
            return hsCode;
        }

        public void setHsCode(String hsCode) {
            this.hsCode = hsCode;
        }

        public String getHsCodeCN() {
            return hsCodeCN;
        }

        public void setHsCodeCN(String hsCodeCN) {
            this.hsCodeCN = hsCodeCN;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
