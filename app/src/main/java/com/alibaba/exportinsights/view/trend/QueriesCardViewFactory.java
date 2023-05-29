package com.alibaba.exportinsights.view.trend;

import android.content.Context;

public class QueriesCardViewFactory extends TrendViewFactory{

    @Override
    public TrendView createView(Context context) {
        return new QueriesCardView(context);
    }
}
