package com.alibaba.exportinsights.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.adapter.holder.LeadingHolder;
import com.alibaba.exportinsights.adapter.holder.ImportDetailHolder;
import com.alibaba.exportinsights.adapter.holder.TariffDetailHolder;
import com.alibaba.exportinsights.adapter.holder.TrendsHolder;
import com.alibaba.exportinsights.bean.LeadingInfo;
import com.alibaba.exportinsights.bean.TradeDetail;

import java.util.List;

public class TradeDetailAdapter extends RecyclerView.Adapter<BaseHolder<TradeDetail>> {

    private static final int TYPE_INFO = 0;
    private static final int TYPE_IMPORT = 1;
    private static final int TYPE_TREND = 2;
    private static final int TYPE_LEADING = 3;
    private TradeDetail mTradeDetail;

    @NonNull
    @Override
    public BaseHolder<TradeDetail> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_INFO) {
            return new TariffDetailHolder(parent);
        } else if (viewType == TYPE_IMPORT) {
            return new ImportDetailHolder(parent);
        } else if (viewType == TYPE_TREND) {
            return new TrendsHolder(parent);
        } else if (viewType == TYPE_LEADING) {
            return new LeadingHolder(parent);
        } else {
            return new TariffDetailHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<TradeDetail> holder, int position) {
        if (mTradeDetail == null) {
            return;
        }
        holder.onBindViewHolder(mTradeDetail, position);
    }

    @Override
    public int getItemCount() {
        if (mTradeDetail == null) {
            return 0;
        }
        int count = 3;
        List<LeadingInfo> leadingInfo = mTradeDetail.getLeadingInfo();
        if (leadingInfo != null && leadingInfo.size() > 0) {
            count += leadingInfo.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return Math.min(position, TYPE_LEADING);
    }

    public TradeDetailAdapter setData(TradeDetail tradeDetail) {
        this.mTradeDetail = tradeDetail;
        notifyDataSetChanged();
        return this;
    }

}
