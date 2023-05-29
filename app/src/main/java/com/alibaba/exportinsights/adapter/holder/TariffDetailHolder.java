package com.alibaba.exportinsights.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.bean.PartnerInfo;
import com.alibaba.exportinsights.bean.TariffInfo;
import com.alibaba.exportinsights.bean.TradeDetail;
import com.alibaba.base.net.Image;

public class TariffDetailHolder extends BaseHolder<TradeDetail> {

    public TariffDetailHolder(ViewGroup v) {
        super(v, R.layout.layout_tariff_detail);
    }

    @Override
    public void onBindViewHolder(TradeDetail detail, int position) {
        TariffInfo tariffInfo = detail.getInfo();
        ImageView image = itemView.findViewById(R.id.product_img);
        TextView title = itemView.findViewById(R.id.product_title);
        TextView hsCode = itemView.findViewById(R.id.search_hs_code);

        Image.obtain().load(image, tariffInfo.imageUrl, 4);
        hsCode.setText(tariffInfo.hsCode);
        title.setText(tariffInfo.title);

        PartnerInfo partnerInfo = detail.getPartnerInfo();
        TextView share = itemView.findViewById(R.id.share_rate);
        TextView duty = itemView.findViewById(R.id.duty_rate_info);
        TextView importValue = itemView.findViewById(R.id.import_value_info);
        share.setText(partnerInfo.shareRate);
        duty.setText(partnerInfo.dutyRate);
        importValue.setText(partnerInfo.importValue);
    }
}
