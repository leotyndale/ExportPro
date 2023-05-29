package com.alibaba.exportinsights.adapter.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.adapter.holder.base.BaseHolder;
import com.alibaba.exportinsights.bean.ImportPartnersBean;
import com.alibaba.exportinsights.bean.TradeDetail;

import java.util.ArrayList;
import java.util.List;

public class ImportDetailHolder extends BaseHolder<TradeDetail> {

    private final List<String> mRowHeader = new ArrayList<>();
    private final List<String> mColumnHeader = new ArrayList<>();
    private final List<List<String>> mCellList = new ArrayList<>();
    private final Context mContext;

    public ImportDetailHolder(@NonNull ViewGroup itemView) {
        super(itemView, R.layout.layout_import_detail);
        mContext = itemView.getContext();
        initRowHeader();
    }

    private void initRowHeader() {
        mColumnHeader.clear();
        mColumnHeader.add(mContext.getString(R.string.economies));
        mColumnHeader.add(mContext.getString(R.string.local_market_shares));
        mColumnHeader.add(mContext.getString(R.string.annual_imports_title));
    }

    @Override
    public void onBindViewHolder(TradeDetail detail, int position) {
        makeData(detail);
        TextView descInfo = itemView.findViewById(R.id.trade_desc_info);
        descInfo.setText(detail.getTradeAnalysis());
        TextView mainTitle = itemView.findViewById(R.id.main_title);
        mainTitle.setText(mContext.getString(R.string.major_export_region, detail.partnerInfo.region));
        TableLayout tableView = itemView.findViewById(R.id.table_view);
        addItemsToTableView(tableView);
    }

    private void addItemsToTableView(TableLayout tableView) {
        if (mRowHeader.isEmpty()) {
            return;
        }
        if (mColumnHeader.size() < 3) {
            return;
        }
        addTableRowItem(true, tableView, mColumnHeader.get(0), mColumnHeader.get(1), mColumnHeader.get(2));
        for (int i = 0; i < mRowHeader.size(); i++) {
            if (mCellList.size() < i || mCellList.get(i).size() < 2) {
                continue;
            }
            addTableRowItem(false, tableView, mRowHeader.get(i), mCellList.get(i).get(0), mCellList.get(i).get(1));
        }
    }

    private void addTableRowItem(boolean firstRow, TableLayout tableView, String titleText, String sharesText, String valueText) {
        TableRow tableRow = (TableRow) LayoutInflater.from(tableView.getContext()).inflate(R.layout.layout_economies_exporting_item,
                tableView, false);
        TextView titleView = tableRow.findViewById(R.id.row_title);
        titleView.setText(titleText);
        TextView sharesView = tableRow.findViewById(R.id.row_shares);
        TextView valueView = tableRow.findViewById(R.id.row_value);
        sharesView.setText(sharesText);
        valueView.setText(valueText);
        tableView.addView(tableRow);
        if (firstRow) {
            tableRow.findViewById(R.id.column_divide_line).setBackgroundColor(Color.parseColor("#979797"));
            tableRow.findViewById(R.id.row_divide_line).setBackgroundColor(Color.parseColor("#979797"));
            titleView.setBackgroundColor(Color.parseColor("#CFE0BF"));
            sharesView.setBackgroundColor(Color.parseColor("#CFE0BF"));
            valueView.setBackgroundColor(Color.parseColor("#CFE0BF"));
        }
        View divideLine = LayoutInflater.from(tableView.getContext()).inflate(R.layout.layout_view_divide_line_d8,
                tableView, false);
        tableView.addView(divideLine);
    }

    private void makeData(TradeDetail detail) {
        mRowHeader.clear();
        mCellList.clear();
        List<ImportPartnersBean> importDetail = detail.getImportDetail();
        for (ImportPartnersBean partnersBean : importDetail) {
            mRowHeader.add(partnersBean.getRegion());
        }
        int rowSize = mRowHeader.size();
        mCellList.clear();
        for (int i = 0; i < rowSize; i++) {
            List<String> cellList = new ArrayList<>();
            ImportPartnersBean partnersBean = importDetail.get(i);
            cellList.add(partnersBean.getShareRate());
            cellList.add(partnersBean.getImportValue());
            mCellList.add(cellList);
        }
    }
}
