package com.alibaba.exportinsights;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.exportinsights.adapter.OptionItemAdapter;
import com.alibaba.exportinsights.bean.ElementBean;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.Property;
import com.alibaba.exportinsights.bean.BaseProperty;
import com.alibaba.exportinsights.repository.TagViewModel;
import com.alibaba.exportinsights.databinding.LayoutElementSelectBinding;
import com.alibaba.exportinsights.repository.SearchElementViewModel;
import com.alibaba.base.utils.DensityUtil;
import com.alibaba.base.constants.IntentKey;
import com.alibaba.exportinsights.view.QuestionnaireView;
import com.alibaba.exportinsights.view.SpacesItemDecoration;
import com.alibaba.exportinsights.view.TagViewListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ElementSelectActivity extends AppCompatActivity implements
        QuestionnaireView.OptionCheckedListener {

    private OptionItemAdapter mOptionItemAdapter;
    private LayoutElementSelectBinding mBinding;
    private TagViewModel mTagViewModel;
    private SearchElementViewModel mSearchElementViewModel;
    private Keyword mKeyword;
    private ArrayList<BaseProperty> mBaseProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = LayoutElementSelectBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initViewModel();
        initEvent();
        initRecyclerView();
        mSearchElementViewModel.request(mKeyword.getZh(),  null);
    }

    private void initView() {
        Toolbar toolbar = mBinding.elementSelectToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mKeyword = (Keyword) getIntent().getSerializableExtra(IntentKey.KEYWORD);
        mBinding.tagTips.setText(getString(R.string.tag_tips, mKeyword.getEn()));
    }

    private void initViewModel() {
        mTagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        mSearchElementViewModel = new ViewModelProvider(this).get(SearchElementViewModel.class);
        mTagViewModel.getTags().observe(this, properties -> {
            List<String> tagList = new ArrayList<>();
            if (mBaseProperties == null) {
                mBaseProperties = new ArrayList<>();
            } else {
                mBaseProperties.clear();
            }
            for (Property property : properties) {
                tagList.add(property.value);
                BaseProperty baseProperty = new BaseProperty();
                baseProperty.name = property.name;
                baseProperty.value = property.zhValue;
                mBaseProperties.add(baseProperty);
            }
            mBinding.tagContainerLayout.setTags(tagList);
            mSearchElementViewModel.getProgressBarShow().setValue(true);
            mSearchElementViewModel.request(mKeyword.getZh(), mBaseProperties);
        });
        mSearchElementViewModel.getElementBeanLiveData().observe(this,
                elementBean -> {
                    // change ui state when receive serve data
                    mSearchElementViewModel.getProgressBarShow().setValue(false);
                    mSearchElementViewModel.getConfirmEnabled().postValue(elementBean != null && Objects.isNull(elementBean.propertyWithMultiValues));
                    mOptionItemAdapter.notifyDataSetChanged();
                });
        mSearchElementViewModel.getConfirmEnabled().observe(this, aBoolean -> mBinding.confirmButton.setEnabled(aBoolean));
        mSearchElementViewModel.getProgressBarShow().observe(this, isShow -> {
            mBinding.transparentView.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mBinding.transparentView.setFocusable(isShow);
            mBinding.transparentView.setClickable(isShow);
            mBinding.circleProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });
    }

    private void initRecyclerView() {
        mOptionItemAdapter = new OptionItemAdapter(mSearchElementViewModel, this);
        RecyclerView recyclerView = mBinding.questionnaireRecyclerView;
        recyclerView.setAdapter(mOptionItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(this, 32)));
    }

    private void initEvent() {
        mBinding.tagContainerLayout.setOnTagClickListener(new TagViewListener() {
            @Override
            public void onTagCrossClick(int position) {
                List<Property> values = mTagViewModel.getTags().getValue();
                if (values == null) {
                    values = new ArrayList<>();
                }
                values.remove(position);
                mTagViewModel.setTags(values);
            }
        });
        mBinding.confirmButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ElementSelectActivity.this, TradeAnalysisActivity.class);
                    intent.putExtra(IntentKey.KEYWORD, mKeyword);
                    ElementBean elementBean = mSearchElementViewModel.getElementBeanLiveData().getValue();
                    if (elementBean != null) {
                        intent.putStringArrayListExtra(IntentKey.HS_CODE, new ArrayList<>(elementBean.hsCodes));
                    }
                    intent.putParcelableArrayListExtra(IntentKey.PROPERTY_VALUES, mBaseProperties);
                    startActivity(intent);
                });
    }

    @Override
    public void onChecked(String title, String enValue, String zhValue) {
        List<Property> values = mTagViewModel.getTags().getValue();
        if (values == null) {
            values = new ArrayList<>();
        }
        Property property = new Property();
        property.name = title;
        property.value = enValue;
        property.zhValue = zhValue;
        values.add(property);
        mTagViewModel.setTags(values);
    }
}
