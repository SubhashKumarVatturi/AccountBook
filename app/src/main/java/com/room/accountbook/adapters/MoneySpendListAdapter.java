package com.room.accountbook.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.room.accountbook.R;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.fragments.MoneySpentList;
import com.room.accountbook.pojo.Bill;
import com.room.accountbook.utils.Helper;

import java.util.List;

/**
 * Created by subash on 7/11/17.
 */

public class MoneySpendListAdapter extends RecyclerView.Adapter<MoneySpendListAdapter.MoneySpendBill> {
    private Context context;
    private List<Bill> bills;
    private MoneySpentList fragment;

    public MoneySpendListAdapter(MoneySpentList fragment, List<Bill> bills) {
        this.context = fragment.getActivity();
        this.bills = bills;
        this.fragment = fragment;
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
        holder.tvDate.setText(String.format("On %s %s, spend for %s", bill.getDate(), bill.getTime(), bill.getSpendFor()));
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

        public MoneySpendBill(final View itemView) {
            super(itemView);
            vConsumers = itemView.findViewById(R.id.vConsumers);
            tvMoney = itemView.findViewById(R.id.tvMoneyIn);
            tvSponsorName = itemView.findViewById(R.id.tvSponsorName);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Helper.confirmDialog(context, context.getString(R.string.sure_want_to_remove_record),
                            context.getString(android.R.string.yes), context.getString(android.R.string.no), new Helper.IL() {
                                @Override
                                public void onSuccess() {
                                    DbManager manager = DbManager.getInstance(context);
                                    SQLiteDatabase db = manager.getReadableDatabase();
                                    int rows = db.delete(DbManager.AddMoney.TABLE_NAME,
                                            String.format("%s=?", DbManager.AddMoney.ID),
                                            new String[]{bills.get(getAdapterPosition()).getId()});
                                    if (rows > 0) {
                                        bills.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        Helper.ting(itemView, context.getString(R.string.record_deleted_success));
                                        if (bills == null || bills.size() == 0)
                                            fragment.onAllItemsRemoved();
                                    } else {
                                        Helper.ting(itemView, context.getString(R.string.error_occured));
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                    return true;
                }
            });

        }
    }
}
