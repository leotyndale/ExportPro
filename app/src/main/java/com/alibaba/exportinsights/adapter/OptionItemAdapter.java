package com.alibaba.exportinsights.adapter;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.exportinsights.bean.ElementBean;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.exportinsights.repository.SearchElementViewModel;
import com.alibaba.exportinsights.view.QuestionnaireView;

import java.util.Objects;

public class OptionItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SearchElementViewModel mSearchElementViewModel;
    private final QuestionnaireView.OptionCheckedListener mOptionCheckedListener;

    public OptionItemAdapter(SearchElementViewModel searchElementViewModel, QuestionnaireView.OptionCheckedListener listener) {
        this.mSearchElementViewModel = searchElementViewModel;
        this.mOptionCheckedListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuestionnaireView questionnaireView = new QuestionnaireView(parent.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionnaireView.setLayoutParams(lp);
        return new ItemViewHolder(questionnaireView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.questionnaireView.initByData(
                Objects.requireNonNull(mSearchElementViewModel.getElementBeanLiveData().getValue())
                        .propertyWithMultiValues.get(position));
        itemViewHolder.questionnaireView.setOptionCheckedListener(mOptionCheckedListener);
    }

    @Override
    public int getItemCount() {
        ElementBean elementBean = mSearchElementViewModel.getElementBeanLiveData().getValue();
        if (elementBean == null || elementBean.propertyWithMultiValues == null) {
            return 0;
        } else {
            return elementBean.propertyWithMultiValues.size();
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final QuestionnaireView questionnaireView;
        public ItemViewHolder(QuestionnaireView v) {
            super(v);
            this.questionnaireView = v;
        }
    }
}
