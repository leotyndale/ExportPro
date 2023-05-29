package com.alibaba.exportinsights.bean;

import java.util.ArrayList;
import java.util.List;

public class TradeDetail {

    // header
    private TariffInfo info = new TariffInfo();
    public PartnerInfo partnerInfo = new PartnerInfo();
    // Major importing
    private final List<ImportPartnersBean> importDetail = new ArrayList<>();
    // trends
    private String trendsUrl;
    private String tradeAnalysis;
    // Leading
    private final List<LeadingInfo> leadingInfo = new ArrayList<>();
    private Keyword keyword;

    public TradeDetail() {
        importDetail.add(new ImportPartnersBean());
        importDetail.add(new ImportPartnersBean());
        importDetail.add(new ImportPartnersBean());
        leadingInfo.add(new LeadingInfo());
        leadingInfo.add(new LeadingInfo());
        leadingInfo.add(new LeadingInfo());
    }

    public String getTradeAnalysis() {
        return tradeAnalysis;
    }

    public void setInfo(TariffInfo info) {
        this.info = info;
    }

    public void setPartnerInfo(PartnerInfo partnerInfo) {
        this.partnerInfo = partnerInfo;
    }

    public TariffInfo getInfo() {
        return info;
    }

    public PartnerInfo getPartnerInfo() {
        return partnerInfo;
    }

    public List<ImportPartnersBean> getImportDetail() {
        return importDetail;
    }

    public String getTrendsUrl() {
        return trendsUrl;
    }

    public List<LeadingInfo> getLeadingInfo() {
        return leadingInfo;
    }

    public TradeDetail setKeyword(Keyword keyword) {
        this.keyword = keyword;
        return this;
    }

    public Keyword getKeyword() {
        return keyword;
    }
}
