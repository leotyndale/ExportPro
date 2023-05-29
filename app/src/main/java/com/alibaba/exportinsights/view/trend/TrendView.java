package com.alibaba.exportinsights.view.trend;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.bean.TrendInfo;
import com.alibaba.base.net.Volley;

import java.util.List;

public abstract class TrendView extends LinearLayout {

    protected final Context mContext;
    protected String mCountryName;

    public TrendView(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initView(context);
    }

    abstract TrendView initView(Context context);

    public void startRender(String keyword, String geoCode, String countryName) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        this.mCountryName = countryName;
        if (TextUtils.isEmpty(geoCode)) {
            geoCode = "US";
        }
        String url = getRequestUrl();
        Volley.obtain().get(url + "?keyword=" + keyword + "&geo=" + geoCode,
                TrendInfo.class, response -> {
                    notifyDataChanged(response.getData());
                });

    }

    /**
     * start render by network data
     *
     * @param keyword search key，like "mp3"
     * @param geoCode area，default "US"
     */
    public void startRender(String keyword, String geoCode) {
        this.startRender(keyword, geoCode, "USA");
    }

    protected void notifyDataChanged(List<Object> data) {
        //title
        TextView cardTitleView = findViewById(R.id.rising_card_title);
        cardTitleView.setVisibility(VISIBLE);
        cardTitleView.setText(getTitle());
        TextView subTitleView = findViewById(R.id.rising_card_subTitle);
        subTitleView.setVisibility(VISIBLE);
        subTitleView.setText(Html.fromHtml("Data taken from <b>Google Trends</b>"));
    }

    protected abstract String getTitle();

    protected abstract String getRequestUrl();

}
