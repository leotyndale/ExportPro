package com.alibaba.exportinsights;

import static com.alibaba.exportinsights.repository.Urls.MEMBER_ECONOMIES;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.base.constants.IntentKey;
import com.alibaba.base.net.Volley;
import com.alibaba.exportinsights.bean.MemberEconomies;
import com.alibaba.exportinsights.adapter.SimpleInfoAdapter;
import com.gyf.immersionbar.ImmersionBar;

public class CountrySelectorActivity extends AppCompatActivity {

    private final SimpleInfoAdapter.onItemClickListener<MemberEconomies.ModelBean> onItemClickListener =
            (view, info) -> {
                setResult(RESULT_OK, new Intent().putExtra(IntentKey.ISO_CODE, info.getIsoCode()));
                finish();
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shipping_from);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ImmersionBar.with(this).statusBarDarkFont(true).init();

        SimpleInfoAdapter<MemberEconomies.ModelBean> adapter = initList();

        Volley.obtain().get(MEMBER_ECONOMIES, MemberEconomies.class,
                response -> adapter.setGetInfo(position -> {
                    if (response == null || response.getModel() == null
                            || response.getModel().size() <= position) {
                        return "";
                    }
                    MemberEconomies.ModelBean modelBean = response.getModel().get(position);
                    return modelBean.getName() + " (" + modelBean.getIsoCode() + ")";
                }).setData(response.getModel())
        );
    }

    private SimpleInfoAdapter<MemberEconomies.ModelBean> initList() {
        RecyclerView emailList = findViewById(R.id.email_list);
        emailList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        emailList.setLayoutManager(layoutManager);
        SimpleInfoAdapter<MemberEconomies.ModelBean> adapter = new SimpleInfoAdapter<>(onItemClickListener);
        emailList.setAdapter(adapter);
        return adapter;
    }

}
