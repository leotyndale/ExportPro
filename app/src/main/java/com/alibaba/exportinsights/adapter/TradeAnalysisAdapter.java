package com.alibaba.exportinsights.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.base.App;
import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.TradeDetailActivity;
import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.bean.Keyword;
import com.alibaba.exportinsights.bean.PartnerInfo;
import com.alibaba.exportinsights.bean.TariffBean;
import com.alibaba.exportinsights.bean.TariffInfo;
import com.alibaba.exportinsights.databinding.LayoutPartnerInfoBinding;
import com.alibaba.exportinsights.databinding.LayoutTariffInfoBinding;
import com.alibaba.exportinsights.databinding.LayoutTariffInfoHeaderBinding;
import com.alibaba.exportinsights.databinding.LayoutTariffInfoFooterBinding;
import com.alibaba.exportinsights.repository.QueryTariffViewModel;
import com.alibaba.base.net.Image;
import com.alibaba.base.constants.IntentKey;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TradeAnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = -1;
    private static final int TYPE_FOOTER = -2;

    private final QueryTariffViewModel mQueryTariffViewModel;

    public TradeAnalysisAdapter(QueryTariffViewModel queryTariffViewModel) {
        this.mQueryTariffViewModel = queryTariffViewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            LayoutTariffInfoBinding tariffInfoBinding = LayoutTariffInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TariffInfoViewHolder(tariffInfoBinding);
        } else if (viewType == TYPE_FOOTER) {
            LayoutTariffInfoFooterBinding tariffFooterBinding = LayoutTariffInfoFooterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TariffFooterViewHolder(tariffFooterBinding);
        } else {
            LayoutPartnerInfoBinding partnerInfoBinding = LayoutPartnerInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            TariffBean tariffBean = mQueryTariffViewModel.getTariffBeanLiveData().getValue();
            Keyword keyword = mQueryTariffViewModel.getKeywordLiveData().getValue();
            return new PartnerInfoViewHolder(partnerInfoBinding)
                    .initClickListener(tariffBean != null ? tariffBean.tariffInfo : null, keyword);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        List<Object> items = mQueryTariffViewModel.getFlatTariff().getValue();
        if (items != null) {
            Object object = items.get(position);
            if (holder instanceof TariffInfoViewHolder) {
                TariffInfo tariff = (TariffInfo) object;
                TariffInfoViewHolder tiHolder = ((TariffInfoViewHolder) holder);

                tiHolder.mBinding.customsControlConditions.setText(tariff.condition);
                tiHolder.mBinding.productDescription.setText(tariff.desc);
                tiHolder.mBinding.exportTaxRebateRate.setText(tariff.exportTaxRate);
                tiHolder.mBinding.productUnit.setText(tariff.unit);
                tiHolder.mHeaderBinding.searchHsCode.setText(tariff.hsCode);
                tiHolder.mHeaderBinding.productTitle.setText(tariff.title);
                load(tariff.imageUrl, tiHolder.mHeaderBinding.productImg);
            } else if (holder instanceof PartnerInfoViewHolder) {
                ((PartnerInfoViewHolder) holder).onBindViewHolder(
                        (PartnerInfo) object, position);
            }
        }
    }

    private void load(String imageUrl, ImageView view) {
        Image.obtain().load(view, imageUrl, 4);
    }

    @Override
    public int getItemCount() {
        return mQueryTariffViewModel.getFlatTariff().getValue()== null ? 0
                : mQueryTariffViewModel.getFlatTariff().getValue().size();
    }

    @Override
    public int getItemViewType(int position) {
        List<Object> items = mQueryTariffViewModel.getFlatTariff().getValue();
        if (items != null) {
            Object object = items.get(position);
            if (object instanceof TariffInfo) {
                return TYPE_HEADER;
            } else if (object instanceof String) {
                return TYPE_FOOTER;
            } else {
                return position;
            }
        } else {
            return 0;
        }
    }

    public static class TariffInfoViewHolder extends RecyclerView.ViewHolder {
        private final LayoutTariffInfoBinding mBinding;
        private final LayoutTariffInfoHeaderBinding mHeaderBinding;
        public TariffInfoViewHolder(LayoutTariffInfoBinding v) {
            super(v.getRoot());
            mBinding = v;
            mHeaderBinding = LayoutTariffInfoHeaderBinding.bind(mBinding.getRoot());
        }
    }

    public static class PartnerInfoViewHolder extends BaseHolder<PartnerInfo> {
        private final LayoutPartnerInfoBinding mBinding;
        private PartnerInfo partner;
        public PartnerInfoViewHolder(LayoutPartnerInfoBinding v) {
            super(v.getRoot());
            this.mBinding = v;
        }

        private PartnerInfoViewHolder initClickListener(TariffInfo tariffInfo, Keyword keyword) {
            itemView.setOnClickListener(v1 -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentKey.KEYWORD, keyword);
                bundle.putSerializable(IntentKey.PARTNER, partner);
                bundle.putSerializable(IntentKey.TARIFF, tariffInfo);
                v1.getContext().startActivity(
                        new Intent(v1.getContext(), TradeDetailActivity.class)
                                .putExtra(IntentKey.PARTNER_WITH_KEYWORD, bundle)
                );
            });
            return this;
        }

        @Override
        public void onBindViewHolder(PartnerInfo partner, int position) {
            this.partner = partner;
            mBinding.customsDutyRate.setText(partner.dutyRate);
            mBinding.valueOfImports.setText(partner.importValue);
            mBinding.percentageGlobalImports.setText(partner.shareRate);
            mBinding.countryName.setText(partner.region);
            String text = App.context().getResources().getString(R.string.view_details);
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            mBinding.viewDetails.setText(content);
        }
    }

    public static class TariffFooterViewHolder extends RecyclerView.ViewHolder {

        public TariffFooterViewHolder(@NonNull LayoutTariffInfoFooterBinding binding) {
            super(binding.getRoot());
        }
    }
}
