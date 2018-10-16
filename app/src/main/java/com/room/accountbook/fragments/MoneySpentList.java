package com.room.accountbook.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.room.accountbook.R;
import com.room.accountbook.adapters.MoneySpendListAdapter;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.pojo.Bill;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link MoneySpentList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneySpentList extends BaseFragment {

    private View vNoData;
    private RecyclerView rvSpendList;


    public MoneySpentList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneySpentList.
     */
    public static MoneySpentList newInstance() {
        MoneySpentList fragment = new MoneySpentList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.money_spent));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_money_spent_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findValues();
    }

    private MoneySpendListAdapter adapter;
    private ArrayList<Bill> bills;

    private void findValues() {
        bills = new ArrayList<>();
        vNoData = activity.findViewById(R.id.vNoData);
        rvSpendList = activity.findViewById(R.id.rvSpendList);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        rvSpendList.setLayoutManager(manager);
        adapter = new MoneySpendListAdapter(this, bills);
        rvSpendList.setAdapter(adapter);

        loadBills();

    }

    private void loadBills() {
        SQLiteDatabase db = DbManager.getInstance(activity).getReadableDatabase();
        Cursor cursor = db.query(DbManager.AddMoney.TABLE_NAME,
                null, null, null, null,
                null, DbManager.AddMoney.DATE + " DESC");
        Cursor personsList = db.query(DbManager.Person.TABLE_NAME,
                new String[]{DbManager.Person.ID, DbManager.Person.NAME}, null,
                null, null, null, DbManager.Person.NAME);

        HashMap<String, String> names = new HashMap<>();
        if (personsList != null) {
            while (personsList.moveToNext()) {
                names.put(personsList.getString(personsList.getColumnIndex(DbManager.Person.ID)),
                        personsList.getString(personsList.getColumnIndex(DbManager.Person.NAME)));
            }
        }
        personsList.close();
        if (cursor != null) {
            String[] columnNames = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                Bill bill = new Bill();
                bill.setId(cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.ID)));
                bill.setSpendFor(cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.SPEND_FOR)));
                bill.setTime(cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.TIME)));
                String date = cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.DATE));
                bill.setDate(getFormattedDate(date));
                bill.setSponsorName(names.get(cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.SPONSOR_ID))));
                bill.setMoneySpend(cursor.getString(cursor.getColumnIndex(DbManager.AddMoney.MONEY_SPEND)));
                double value;
                StringBuilder builder = new StringBuilder();
                for (int i = 6; i < columnNames.length; i++) {
                    value = cursor.getDouble(cursor.getColumnIndex(columnNames[i]));
                    if (value > 0) {
                        builder.append(names.get(columnNames[i].substring(1)));
                        builder.append(", ");
                    }
                }
                try {
                    if (builder.toString().trim().length() > 0) {
                        builder.delete(builder.length() - 2, builder.length());
                    }
                } catch (Exception e) {
                }
                bill.setConsumers(builder.toString());
                bills.add(bill);
            }
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
        if (bills.size() > 0) {
            vNoData.setVisibility(View.GONE);
        }
    }

    private String getFormattedDate(String actualDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(actualDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(date);
    }

    public void onAllItemsRemoved() {
        vNoData.setVisibility(View.VISIBLE);
    }
}
