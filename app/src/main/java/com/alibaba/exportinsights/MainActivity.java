package com.alibaba.exportinsights;

import static com.alibaba.exportinsights.repository.Urls.POPULAR_SEARCH;
import static com.alibaba.exportinsights.repository.Urls.SEARCH_NAME;
import static com.alibaba.base.constants.IntentKey.SEARCH_CACHE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.base.App;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.SearchItem;
import com.alibaba.base.net.Volley;
import com.alibaba.exportinsights.bean.SearchName;
import com.alibaba.base.cache.Cache;
import com.alibaba.base.net.Image;
import com.alibaba.base.constants.IntentKey;
import com.alibaba.exportinsights.adapter.SimpleInfoAdapter;
import com.alibaba.imagesearch.ImageSearchActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int EDIT_OK = 1;
    private SearchBar searchBar;
    private SearchView searchView;
    private LinearProgressIndicator searchProgress;
    private RecyclerView searchNameList;
    private LinearLayout suggestionContainer;

    public void initImageSearchMenu() {
        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }
                        String keyword = intent.getStringExtra(IntentKey.KEYWORD);
                        if (TextUtils.isEmpty(keyword)) {
                            return;
                        }
                        new Handler().postDelayed(() -> {
                            searchView.show();
                            searchView.clearText();
                            searchView.setText(keyword);
                        }, 250);
                    }
                });
        searchView.inflateMenu(R.menu.menu_searchview);
        searchView.setOnMenuItemClickListener(
                menuItem -> {
                    resultLauncher.launch(new Intent(this, ImageSearchActivity.class));
                    return true;
                });
        searchBar.inflateMenu(R.menu.menu_searchview);
        searchBar.setOnMenuItemClickListener(
                menuItem -> {
                    resultLauncher.launch(new Intent(this, ImageSearchActivity.class));
                    return true;
                });
    }

    public void setUpSearchView() {
        listenSearchEditor(searchView);
        OnBackPressedCallback onBackPressedCallback =
                new OnBackPressedCallback(false) {
                    @Override
                    public void handleOnBackPressed() {
                        searchView.hide();
                    }
                };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        searchView.addTransitionListener(
                (searchView1, previousState, newState) ->
                        onBackPressedCallback.setEnabled(newState == SearchView.TransitionState.SHOWN));
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (EDIT_OK == msg.what) {
                String searchText = searchView.getText().toString();
                if (TextUtils.isEmpty(searchText)) {
                    return;
                }
                requestQueryList(searchText);
            }
        }
    };
    private Runnable mRunnable = () -> mHandler.sendEmptyMessage(EDIT_OK);

    private void listenSearchEditor(SearchView searchView) {
        searchProgress = findViewById(R.id.linear_indicator);
        searchProgress.hide();
        EditText editText = searchView.getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void requestQueryList(String searchText) {
        TextView tipsInfo = findViewById(R.id.tip_info);
        searchNameList = findViewById(R.id.suggestion_list);
        searchNameList.setVisibility(View.GONE);

        searchProgress.show();
        tipsInfo.setVisibility(View.GONE);

        Volley.obtain().get(SEARCH_NAME + "?name=" + searchText,
                SearchName.class, response -> {
                    SearchName.ModelBean model = response.getModel();
                    if (model == null) {
                        return;
                    }
                    SimpleInfoAdapter<SearchName.ModelBean.NameListBean> adapter = initSearchNameList();
                    List<SearchName.ModelBean.NameListBean> nameList = model.getNameList();

                    adapter.setGetInfo(position -> nameList.get(position).getEnName())
                            .setData(nameList);
                    searchProgress.hide();

                    tipsInfo.setVisibility(View.VISIBLE);
                    tipsInfo.setText(getString(R.string.relate_search_tip, nameList.size(), searchText));
                });
    }

    private SimpleInfoAdapter<SearchName.ModelBean.NameListBean> initSearchNameList() {
        searchNameList.setVisibility(View.VISIBLE);
        searchNameList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchNameList.setLayoutManager(layoutManager);
        SimpleInfoAdapter<SearchName.ModelBean.NameListBean> adapter = new SimpleInfoAdapter<>(
                (view, info) -> view.getContext().
                        startActivity(new Intent(view.getContext(), ElementSelectActivity.class)
                                .putExtra(IntentKey.KEYWORD, new Keyword(info.getEnName(), info.getName()))));
        searchNameList.setAdapter(adapter);
        return adapter;
    }

    public void addRecentList() {
        List<SearchItem.ModelBean> beanList = Cache.obtain().read(SEARCH_CACHE, SearchItem.ModelBean.class);
        if (beanList == null || beanList.isEmpty()) {
            return;
        }
        Collections.reverse(beanList);
        addSearchItem(suggestionContainer, R.string.recent_searches);
        addSuggestionItemViews(beanList, true);
    }

    public void addPopularList(ViewGroup suggestionContainer,
                               List<SearchItem.ModelBean> beanList) {
        addSearchItem(suggestionContainer, R.string.popular_searches);
        addSuggestionItemViews(beanList, false);
    }

    private void addSearchItem(ViewGroup parent, @StringRes int titleResId) {
        TextView titleView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_title, parent, false);
        titleView.setText(titleResId);
        parent.addView(titleView);
    }

    private void addSuggestionItemViews(
            List<SearchItem.ModelBean> productItems,
            boolean isSimple) {
        for (SearchItem.ModelBean productItem : productItems) {
            addSuggestionItemView(productItem, isSimple);
        }
    }

    private void addSuggestionItemView(
            SearchItem.ModelBean productItem, boolean isSimple) {
        View view =
                LayoutInflater.from(suggestionContainer.getContext())
                        .inflate(R.layout.layout_search_item, suggestionContainer, false);

        ImageView searchIcon = view.findViewById(R.id.search_icon);
        TextView searchName = view.findViewById(R.id.search_name);
        TextView searchDesc = view.findViewById(R.id.search_desc);
        TextView searchHsCode = view.findViewById(R.id.search_hs_code);
        if (isSimple) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    Image.dp2px(32), Image.dp2px(32));
            params.gravity = Gravity.CENTER_VERTICAL;
            searchIcon.setLayoutParams(params);
            searchIcon.setImageResource(R.drawable.search_icon);
            searchDesc.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    Image.dp2px(60), Image.dp2px(60));
            searchIcon.setLayoutParams(params);
            params.gravity = Gravity.TOP;
            searchDesc.setVisibility(View.VISIBLE);
            searchDesc.setText(productItem.getDesc());
            Image.obtain().load(searchIcon, productItem.getImageUrl(), 8);
        }
        searchName.setText(productItem.getName());
        searchHsCode.setText(getString(R.string.hs_code_value, productItem.getHsCode()));

        view.setOnClickListener(v -> gotoDetail(searchBar, searchView, productItem.getName(), productItem.getHsCodeCN()));

        suggestionContainer.addView(view);
    }

    private void gotoDetail(SearchBar searchBar, SearchView searchView, String query, String hsCode) {
        searchBar.getContext().startActivity(
                new Intent(searchBar.getContext(), TradeAnalysisActivity.class)
                        .putStringArrayListExtra(IntentKey.HS_CODE, new ArrayList<>(
                                Collections.singletonList(hsCode)))
                        .putExtra(IntentKey.KEYWORD, new Keyword(query, query)));
        searchView.hide();
    }

    public void startOnLoadAnimation(@NonNull SearchBar searchBar, @Nullable Bundle bundle) {
        if (bundle == null) {
            searchBar.startOnLoadAnimation();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_main);
        searchBar = findViewById(R.id.cat_search_bar);
        searchView = findViewById(R.id.cat_search_view);

        initCountrySelector();
        suggestionContainer = findViewById(R.id.cat_search_view_suggestion_container);

        setUpSearchView();
        initImageSearchMenu();

        startOnLoadAnimation(searchBar, bundle);

        requestRecommends();
    }

    private void initCountrySelector() {
        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }
                        String isoCode = intent.getStringExtra(IntentKey.ISO_CODE);
                        if (TextUtils.isEmpty(isoCode)) {
                            return;
                        }
                        TextView countryInfo = findViewById(R.id.country_info);
                        countryInfo.setText(isoCode);
                        App.setIsoCode(isoCode);
                    }
                });
        String localCountryCode = new Locale("",
                App.context().getResources().getConfiguration()
                        .locale.getCountry()).getISO3Country();
        if (!TextUtils.isEmpty(localCountryCode)) {
            App.setIsoCode(localCountryCode);
            TextView countryInfo = findViewById(R.id.country_info);
            countryInfo.setText(localCountryCode);
        }
        findViewById(R.id.country_selector).setOnClickListener(v -> mStartForResult.launch(
                new Intent(MainActivity.this, CountrySelectorActivity.class)
        ));
    }

    private void requestRecommends() {
        addRecentList();
        ProgressBar recommendProgress = findViewById(R.id.recommend_progress);
        Volley.obtain().get(POPULAR_SEARCH, SearchItem.class, response -> {
            recommendProgress.setVisibility(View.GONE);
            List<SearchItem.ModelBean> beanList = response.getModel();
            addPopularList(suggestionContainer, beanList);
        });
    }
}
