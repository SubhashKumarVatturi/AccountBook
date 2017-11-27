package com.room.accountbook.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.room.accountbook.R;
import com.room.accountbook.pojo.Bill;

import java.util.List;

/**
 * Created by subash on 7/11/17.
 */

public class MoneySpendListAdapter extends RecyclerView.Adapter<MoneySpendListAdapter.MoneySpendBill> {
    private Context context;
    private List<Bill> bills;

    public MoneySpendListAdapter(Context context, List<Bill> bills) {
        this.context = context;
        this.bills = bills;
    }

    @Override
    public MoneySpendBill onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.inflate_spend_item, parent, false);
        return new MoneySpendBill(layout);
    }

    @Override
    public void onBindViewHolder(MoneySpendBill holder, int position) {
        Bill bill = bills.get(position);
        holder.tvMoney.setText(bill.getMoneySpend());
        holder.tvSponsorName.setText(bill.getSponsorName());
        holder.tvMoney.setText(String.format("%s ₹", bill.getMoneySpend()));
        holder.tvDate.setText(String.format("%s %s", bill.getDate(), bill.getTime()));
        holder.vConsumers.setText(bill.getConsumers());
    }

    @Override
    public int getItemCount() {
        return bills == null ? 0 : bills.size();
    }

    protected class MoneySpendBill extends RecyclerView.ViewHolder {

        private TextView tvMoney;
        private TextView tvSponsorName;
        private TextView vConsumers;
        private TextView tvDate;

        public MoneySpendBill(View itemView) {
            super(itemView);
            vConsumers = itemView.findViewById(R.id.vConsumers);
            tvMoney = itemView.findViewById(R.id.tvMoneyIn);
            tvSponsorName = itemView.findViewById(R.id.tvSponsorName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
