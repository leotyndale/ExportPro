package com.alibaba.exportinsights.view.trend;

import android.content.Context;
import android.text.TextUtils;

public abstract class TrendViewFactory {

    public static TrendViewFactory getFactory(String viewType) {
        if (TextUtils.equals(viewType, "lineChart")) {
            //line chart
            return new LineChartViewFactory();
        } else if (TextUtils.equals(viewType, "topicsView")) {
            //topics sort
            return new TopicsCardViewFactory();
        } else {
            //queries sort
            return new QueriesCardViewFactory();
        }
    }

    public abstract TrendView createView(Context context);
}
