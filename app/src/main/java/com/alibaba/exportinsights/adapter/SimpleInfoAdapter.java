package com.alibaba.exportinsights.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.adapter.holder.TextViewHolder;

import java.util.List;

public final class SimpleInfoAdapter<T> extends RecyclerView.Adapter<TextViewHolder> {

    private List<T> memberEconomies;
    private GetInfo getInfo;

    public SimpleInfoAdapter<T> setGetInfo(GetInfo getInfo) {
        this.getInfo = getInfo;
        return this;
    }

    public SimpleInfoAdapter<T> setData(List<T> infoList) {
        this.memberEconomies = infoList;
        notifyDataSetChanged();
        return this;
    }

    public interface onItemClickListener<T> {
        void onClick(@NonNull View view, T info);
    }

    private final onItemClickListener<T> listener;

    public SimpleInfoAdapter(@NonNull onItemClickListener<T> listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        if (memberEconomies == null) {
            return;
        }
        String info = getInfo.onGetInfo(position);
        holder.mCountryInfo.setText(info);
        holder.mCountryInfo.setOnClickListener(view -> {
            if (listener == null) {
                return;
            }
            listener.onClick(view, memberEconomies.get(position));
        });
    }


    @Override
    public int getItemCount() {
        return memberEconomies != null ? memberEconomies.size() : 0;
    }

    public interface GetInfo {
        String onGetInfo(int position);
    }
}
