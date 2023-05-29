package com.alibaba.exportinsights.adapter.holder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.alibaba.base.App;
import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.TradeDetail;
import com.alibaba.exportinsights.view.trend.TrendView;
import com.alibaba.exportinsights.view.trend.TrendViewFactory;

import java.util.Locale;

public class TrendsHolder extends BaseHolder<TradeDetail> {
    public TrendsHolder(@NonNull ViewGroup parent) {
        super(parent, R.layout.layout_trend_info);
    }

    @Override
    public void onBindViewHolder(TradeDetail detail, int position) {
        Keyword keyword = detail.getKeyword();
        if (keyword == null) {
            return;
        }
        LinearLayoutCompat container = itemView.findViewById(R.id.main_content);
        TrendView view = TrendViewFactory.getFactory("lineChart").createView(itemView.getContext());
        container.addView(view);
        String iso2Country = new Locale(App.isoCode()).getCountry();
        if (view != null) {
            view.startRender(keyword.getEn(), iso2Country, detail.partnerInfo.region);
        }
        TrendView view2 = TrendViewFactory.getFactory("topicsView")
                .createView(itemView.getContext());
        container.addView(view2);
        if (view2 != null) {
            view2.startRender(keyword.getEn(), iso2Country, detail.partnerInfo.region);
        }

        TrendView view3 = TrendViewFactory.getFactory("")
                .createView(itemView.getContext());
        container.addView(view3);
        if (view3 != null) {
            view3.startRender(keyword.getEn(), iso2Country, detail.partnerInfo.region);
        }
    }

}
