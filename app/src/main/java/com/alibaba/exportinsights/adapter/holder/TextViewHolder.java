package com.alibaba.exportinsights.adapter.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.exportinsights.R;

public class TextViewHolder extends RecyclerView.ViewHolder {

    public final TextView mCountryInfo;

    public TextViewHolder(@NonNull View view) {
        super(view);
        mCountryInfo = view.findViewById(R.id.item_tv);
    }
}
