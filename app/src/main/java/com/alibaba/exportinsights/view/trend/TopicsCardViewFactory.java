package com.alibaba.exportinsights.view.trend;

import android.content.Context;

public class TopicsCardViewFactory extends TrendViewFactory{

    @Override
    public TrendView createView(Context context) {
        return new TopicsCardView(context);
    }
}
