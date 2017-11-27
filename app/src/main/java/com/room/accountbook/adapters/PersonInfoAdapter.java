package com.room.accountbook.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.room.accountbook.R;
import com.room.accountbook.pojo.Person;

import java.util.List;

/**
 * Created by subash on 7/11/17.
 */

public class PersonInfoAdapter extends RecyclerView.Adapter<PersonInfoAdapter.PersonHolder> {
    private Context context;
    private List<Person> consumersList;

    public PersonInfoAdapter(Context context, List<Person> consumersList) {
        this.context = context;
        this.consumersList = consumersList;
        notifyDataSetChanged();

    }

    @Override
    public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_consumer_list, parent, false);
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonHolder holder, int position) {
        holder.tvConsumer.setText(consumersList.get(position).getName());
        holder.tvMoneyIn.setText(String.format("%s ₹", consumersList.get(position).getMoneyIn()));
        holder.tvMoneyOut.setText(String.format("%s ₹", consumersList.get(position).getMoneyOut()));
    }

    @Override
    public int getItemCount() {
        return consumersList != null ? consumersList.size() : 0;
    }

    protected class PersonHolder extends RecyclerView.ViewHolder {
        private TextView tvConsumer;
        private TextView tvMoneyIn;
        private TextView tvMoneyOut;

        public PersonHolder(View itemView) {
            super(itemView);
            tvMoneyOut = itemView.findViewById(R.id.tvMoneyOut);
            tvConsumer = itemView.findViewById(R.id.tvConsumer);
            tvMoneyIn = itemView.findViewById(R.id.tvMoneyIn);
        }
    }

}
