package com.alibaba.exportinsights.view.trend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.repository.Urls;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TopicsCardView extends TrendView {

    private LinearLayout mCardView;

    private final int TOP_MAX = 5;

    public TopicsCardView(@NonNull Context context) {
        super(context);
    }

    @Override
    TrendView initView(Context context) {
        //create view
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_rising_card,
                this, true);
        mCardView = view.findViewById(R.id.rising_card_layout);
        return this;
    }

    @Override
    protected void notifyDataChanged(List<Object> data) {
        if (data == null || data.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        super.notifyDataChanged(data);
        View itemView = null;
        int itemCount = 0;
        for (int i = 0; i < data.size(); i++) {
            if (i >= TOP_MAX) {
                break;
            }
            itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_rising_item, mCardView,
                    false);
            TextView titleView = itemView.findViewById(R.id.rising_title);
            TextView rateView = itemView.findViewById(R.id.rising_rate);
            ArrayList<Object> arrayList = (ArrayList<Object>) data.get(i);
            Object key = arrayList.get(0);
            Object value = arrayList.get(1);
            titleView.setText(MessageFormat.format("{0} {1}", i + 1, String.valueOf(key)));
            rateView.setText(String.valueOf(value));
            if (mCardView != null) {
                mCardView.addView(itemView);
            }
            itemCount++;
        }
        if (itemCount <= TOP_MAX && itemView != null) {
            itemView.findViewById(R.id.divide_line).setVisibility(GONE);
        }

    }

    @Override
    protected String getTitle() {
        return MessageFormat.format("Related rising topics in {0}", mCountryName);
    }

    @Override
    protected String getRequestUrl() {
        return Urls.TOPICS_RISING_API;
    }
}
