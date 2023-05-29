package com.alibaba.exportinsights.view.trend;

import android.content.Context;

public class LineChartViewFactory extends TrendViewFactory {

    @Override
    public TrendView createView(Context context) {
        return new LineChartView(context);
    }
}
