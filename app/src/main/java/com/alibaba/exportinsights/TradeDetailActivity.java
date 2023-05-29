package com.alibaba.exportinsights;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.base.App;
import com.alibaba.exportinsights.adapter.TradeDetailAdapter;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.PartnerInfo;
import com.alibaba.exportinsights.bean.TariffInfo;
import com.alibaba.exportinsights.bean.TradeDetail;
import com.alibaba.exportinsights.repository.Urls;
import com.alibaba.base.net.Volley;
import com.alibaba.base.utils.DensityUtil;
import com.alibaba.base.constants.IntentKey;
import com.alibaba.exportinsights.view.SpacesItemDecoration;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.gyf.immersionbar.ImmersionBar;

import java.util.HashMap;

public class TradeDetailActivity extends AppCompatActivity {

    private LinearProgressIndicator mProgress;
    private PartnerInfo mPartnerInfo;
    private TariffInfo mTariffInfo;
    private Keyword mKeyword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trade_analysis);
        initArgs();
        initToolBar();
        request();
    }

    private void request() {
        HashMap<String, String> body = new HashMap<>();
        body.put("isoCode", App.isoCode());
        body.put("hsCode", mTariffInfo.hsCode);
        body.put("importCountryName", mPartnerInfo.region);
        Volley.obtain().post(Urls.DETAIL, body,
                TradeDetail.class, this::initRecyclerView);
    }

    private void initRecyclerView(TradeDetail response) {
        if (response == null) {
            return;
        }
        TradeDetailAdapter adapter = new TradeDetailAdapter();

        response.setInfo(mTariffInfo);
        response.setKeyword(mKeyword);
        response.setPartnerInfo(mPartnerInfo);
        RecyclerView recyclerView = findViewById(R.id.product_analysis_recycler_view);
        recyclerView.setAdapter(adapter.setData(response));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this, 16)));

        mProgress.hide();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.trade_analysis_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.trade_insights));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ImmersionBar.with(this).statusBarDarkFont(true).init();
        mProgress = findViewById(R.id.linear_indicator);
    }

    private void initArgs() {
        if (getIntent() == null) {
            return;
        }
        Bundle bundle = getIntent().getBundleExtra(IntentKey.PARTNER_WITH_KEYWORD);
        mPartnerInfo = (PartnerInfo) bundle.get(IntentKey.PARTNER);
        mTariffInfo = (TariffInfo) bundle.get(IntentKey.TARIFF);
        mKeyword = (Keyword) bundle.get(IntentKey.KEYWORD);
    }
}
