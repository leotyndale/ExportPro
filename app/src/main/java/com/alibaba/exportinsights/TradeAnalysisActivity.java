package com.alibaba.exportinsights;


import static com.alibaba.base.constants.IntentKey.SEARCH_CACHE;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.base.cache.Cache;
import com.alibaba.base.constants.IntentKey;
import com.alibaba.base.utils.DensityUtil;
import com.alibaba.exportinsights.adapter.TradeAnalysisAdapter;
import com.alibaba.exportinsights.bean.BaseProperty;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.SearchItem;
import com.alibaba.exportinsights.bean.TariffBean;
import com.alibaba.exportinsights.bean.TariffInfo;
import com.alibaba.exportinsights.databinding.LayoutTradeAnalysisBinding;
import com.alibaba.exportinsights.repository.QueryTariffViewModel;
import com.alibaba.exportinsights.view.SpacesItemDecoration;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TradeAnalysisActivity extends AppCompatActivity {

    private LayoutTradeAnalysisBinding mBinding;
    private TradeAnalysisAdapter mTradeAnalysisAdapter;
    private QueryTariffViewModel mQueryTariffViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = LayoutTradeAnalysisBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initToolbar();
        initViewModel();
        initRecyclerView();
    }

    private void initToolbar() {
        Toolbar toolbar = mBinding.tradeAnalysisToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }

    private void initViewModel() {
        mQueryTariffViewModel = new ViewModelProvider(this).get(QueryTariffViewModel.class);
        Keyword keyword = (Keyword) getIntent().getSerializableExtra(IntentKey.KEYWORD);
        ArrayList<String> hsCodes = getIntent().getStringArrayListExtra(IntentKey.HS_CODE);
        ArrayList<BaseProperty> baseProperties = getIntent().getParcelableArrayListExtra(IntentKey.PROPERTY_VALUES);
        mQueryTariffViewModel.getKeywordLiveData().postValue(keyword);
        mQueryTariffViewModel.request(keyword.getEn(), baseProperties, hsCodes);
        mQueryTariffViewModel.getTariffBeanLiveData().observe(this, tariffBean -> {
            mBinding.linearIndicator.setVisibility(View.GONE);
            mQueryTariffViewModel.getFlatTariff().setValue(buildFlatData(tariffBean));
            mTradeAnalysisAdapter.notifyDataSetChanged();
            // 只保存从select来的数据
            if (baseProperties != null && tariffBean != null) {
                save(tariffBean.tariffInfo);
            }
        });
    }

    private void initRecyclerView() {
        mTradeAnalysisAdapter = new TradeAnalysisAdapter(mQueryTariffViewModel);
        RecyclerView recyclerView = mBinding.productAnalysisRecyclerView;
        recyclerView.setAdapter(mTradeAnalysisAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this, 16)));
    }

    private List<Object> buildFlatData(TariffBean tariffBean) {
        if (tariffBean == null) {
            return new ArrayList<>();
        }
        List<Object> list = new ArrayList<>();
        TariffInfo tariffInfo = tariffBean.tariffInfo;
        list.add(tariffInfo);
        list.addAll(tariffBean.partnerList);
        list.add(getString(R.string.data_updated_time));

        return list;
    }

    private void save(TariffInfo tariffInfo) {
        SearchItem.ModelBean bean = makeSaveData(tariffInfo);
        List<SearchItem.ModelBean> modelBeans = Cache.obtain().read(SEARCH_CACHE, SearchItem.ModelBean.class);
        CircularQueue<SearchItem.ModelBean> queue = new CircularQueue<>(3);
        if (modelBeans != null) {
            queue.addAll(modelBeans);
        }
        queue.offer(bean);
        Cache.obtain().write(SEARCH_CACHE, Arrays.asList(queue.toArray()));
    }

    public static class CircularQueue<E> extends LinkedList<E> {
        private final int capacity;

        public CircularQueue(int capacity){
            this.capacity = capacity;
        }

        @Override
        public boolean add(E e) {
            if(size() >= capacity)
                removeFirst();
            return super.add(e);
        }
    }

    private SearchItem.ModelBean makeSaveData(TariffInfo tariffInfo) {
        SearchItem.ModelBean modelBean = new SearchItem.ModelBean();
        modelBean.setDesc(tariffInfo.desc);
        modelBean.setName(tariffInfo.title);
        modelBean.setImageUrl(tariffInfo.imageUrl);
        modelBean.setHsCode(tariffInfo.hsCode);
        modelBean.setHsCodeCN(tariffInfo.hsCodeCN);
        return modelBean;
    }
}
