package com.alibaba.exportinsights.view.trend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.repository.Urls;
import com.alibaba.base.utils.DensityUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LineChartView extends TrendView {
    private LineChart mChart;
    private LinearLayout mCardView;

    public LineChartView(@NonNull Context context) {
        super(context);
    }

    public TrendView initView(Context context) {
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

        initLineChartView();
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(parseLineChartResponseData(data), "DataSet 1");
        int themeColor = Color.parseColor("#497B53");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(themeColor);
        set1.setValueTextColor(themeColor);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(true);
        set1.setCircleColor(themeColor);
        set1.setDrawValues(false);
        set1.setDrawFilled(true);
        set1.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_line_chart_fill_color));
        set1.setHighLightColor(Color.TRANSPARENT);

        // create a data object with the data sets
        LineData lineData = new LineData(set1);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueTextSize(9f);
        // set data
        mChart.setData(lineData);

        // redraw
        mChart.invalidate();
    }

    @Override
    protected String getTitle() {
        return MessageFormat.format("Search interest for this product in {0}", mCountryName);
    }

    private void initLineChartView() {
        mChart = (LineChart) LayoutInflater.from(mContext).inflate(R.layout.layout_linechart_time,
                mCardView, false);
        Typeface tfLight = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Light.ttf");
        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.parseColor("#00000000"));
        //chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        //set view padding
        mChart.setExtraRightOffset(15f);
        mChart.setExtraTopOffset(50f);
        mChart.setExtraBottomOffset(10f);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        //X Axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        //xAxis.setLabelRotationAngle(45);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setAxisLineColor(Color.parseColor("#D8D8D8"));
        xAxis.enableGridDashedLine(DensityUtil.dip2px(mContext, 3),
                DensityUtil.dip2px(mContext, 3), 0);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(12);
        //xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return mFormat.format(value);
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        // set grid line
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisLineColor(Color.parseColor("#D8D8D8"));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        //leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return MessageFormat.format("{0}%", value);
            }
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mCardView.addView(mChart);
    }

    @Override
    protected String getRequestUrl() {
        return Urls.LINE_CHART_API;
    }

    private List<Entry> parseLineChartResponseData(List<Object> trendInfoList) {

        List<Entry> list = new ArrayList<>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (int i = 0; i < trendInfoList.size(); i++) {
            ArrayList<Object> arrayList = (ArrayList<Object>) trendInfoList.get(i);
            Object dateStr = arrayList.get(0);
            Object value = arrayList.get(1);
            long x;
            try {
                x = mFormat.parse(dateStr.toString()).getTime();
                list.add(new Entry(x, Float.parseFloat(String.valueOf(value))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
