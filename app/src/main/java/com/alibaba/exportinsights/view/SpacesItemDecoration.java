package com.alibaba.exportinsights.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
            RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = mSpace;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = mSpace;
        }
        if (parent.getAdapter() != null) {
            if (parent.getChildLayoutPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = (int)(mSpace * 2.5);
            }
        }
    }
}