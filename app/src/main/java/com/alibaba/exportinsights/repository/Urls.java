package com.alibaba.exportinsights.repository;

public interface Urls {
    boolean isPre = false;
    String HOST_PRE = "https://pre-butterfly.alibaba-inc.com";
    String HOST = isPre ? HOST_PRE : "https://m.alibaba.com";

    String MEMBER_ECONOMIES = HOST + "/apec/insights/member-economies.do";
    String SEARCH_NAME = HOST + "/apec/insights/searchName.do";
    String POPULAR_SEARCH = HOST + "/apec/insights/popularSearch.do";
    String SEARCH_ELEMENT = HOST + "/apec/insights/searchElement.do";
    String QUERY_TARIFF = HOST + "/apec/insights/queryTariff.do";
    String DETAIL = HOST + "/apec/insights/queryImportDetail.do";

    String LINE_CHART_API = "https://trends.cocoaz.com/api/interest-over-time";
    String TOPICS_RISING_API = "https://trends.cocoaz.com/api/related-topics";
    String QUERIES_RISING_API = "https://trends.cocoaz.com/api/related-queries";
}
