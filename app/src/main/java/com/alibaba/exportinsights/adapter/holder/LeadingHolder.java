package com.alibaba.exportinsights.adapter.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.bean.LeadingInfo;
import com.alibaba.exportinsights.bean.TradeDetail;
import com.alibaba.base.net.Image;

import java.util.List;

public class LeadingHolder extends BaseHolder<TradeDetail> {

    private final Context mContext;

    public LeadingHolder(@NonNull ViewGroup parent) {
        super(parent, R.layout.layout_leading_info);
        mContext = parent.getContext();
    }

    @Override
    public void onBindViewHolder(TradeDetail detail, int position) {
        List<LeadingInfo> leadingInfoList = detail.getLeadingInfo();
        if (leadingInfoList == null || leadingInfoList.size() == 0) {
            return;
        }
        TextView mainTitle = itemView.findViewById(R.id.main_title);
        mainTitle.setVisibility(
                isFirstLeading(position) ? View.VISIBLE : View.GONE
        );
        mainTitle.setText(mContext.getString(R.string.top_importer_region, detail.partnerInfo.region));

        LeadingInfo leadingInfo = leadingInfoList.get(getLeadingIndex(position));
        if (leadingInfo == null) {
            itemView.findViewById(R.id.leading_content).setVisibility(View.GONE);
            return;
        }
        itemView.findViewById(R.id.leading_content).setVisibility(View.VISIBLE);
        ImageView icon = itemView.findViewById(R.id.icon);
        Image.obtain().load(icon, leadingInfo.imageUrl, 4);
        TextView title = itemView.findViewById(R.id.title);
        title.setText(leadingInfo.title);
        TextView type = itemView.findViewById(R.id.type);
        type.setText(leadingInfo.type);
        TextView transaction = itemView.findViewById(R.id.transaction_info);
        transaction.setText(leadingInfo.transaction);
        TextView imports = itemView.findViewById(R.id.imports_info);
        imports.setText(leadingInfo.imports);
    }

    private boolean isFirstLeading(int position) {
        return getLeadingIndex(position) == 0;
    }

    private int getLeadingIndex(int position) {
        return position - 3;
    }
}
