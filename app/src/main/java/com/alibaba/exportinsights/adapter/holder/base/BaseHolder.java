package com.alibaba.exportinsights.adapter.holder.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseHolder<Data> extends RecyclerView.ViewHolder {

    public BaseHolder(@NonNull View itemView) {
        super(itemView);
    }
    public BaseHolder(@NonNull ViewGroup parent, int resource) {
        super(getView(parent, resource));
    }

    public static View getView(ViewGroup parent, int resource) {
        return LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
    }

    public abstract void onBindViewHolder(Data object, int position);
}
