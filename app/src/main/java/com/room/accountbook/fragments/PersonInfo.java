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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.room.accountbook.R;
import com.room.accountbook.adapters.PersonInfoAdapter;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.pojo.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link PersonInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonInfo extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView rvConsumersList;
    private Spinner spPersons;
    private PersonInfoAdapter adapter;
    private List<Person> consumersList;
    private TextView tvMobileNumber;
    private TextView tvMoneySponsored;
    private View vMoney;
    private View vTitles;
    private View vNumber;

    public PersonInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneySpentList.
     */
    public static PersonInfo newInstance() {
        PersonInfo fragment = new PersonInfo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.person_info));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        consumersList = new ArrayList<>();
        adapter = new PersonInfoAdapter(activity, consumersList);
        findValues(view);
        loadPersons();
    }


    List<String> persons = new ArrayList<>();
    List<String> personIds = new ArrayList<>();

    private void loadPersons() {
        SQLiteDatabase db = DbManager.getInstance(activity).getReadableDatabase();
        Cursor cursor = db.query(DbManager.Person.TABLE_NAME,
                new String[]{DbManager.Person.NAME, DbManager.Person.ID},
                null, null, null, null, DbManager.Person.NAME);

        persons.add(getString(R.string.select_person));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                persons.add(cursor.getString(cursor.getColumnIndex(DbManager.Person.NAME)));
                personIds.add(cursor.getString(cursor.getColumnIndex(DbManager.Person.ID)));
            }
            cursor.close();
        }
        db.close();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_spinner_item, persons);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spPersons.setAdapter(spinnerArrayAdapter);

    }

    private void findValues(View view) {
        vNumber = view.findViewById(R.id.vNumber);
        tvMoneySponsored = view.findViewById(R.id.tvMoneySponsored);
        vTitles = view.findViewById(R.id.vTitles);
        vMoney = view.findViewById(R.id.vMoney);
        tvMobileNumber = view.findViewById(R.id.tvMobileNumber);
        spPersons = view.findViewById(R.id.spPersons);
        spPersons.setOnItemSelectedListener(this);
        rvConsumersList = view.findViewById(R.id.rvConsumersList);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        rvConsumersList.setLayoutManager(manager);
        rvConsumersList.setNestedScrollingEnabled(false);
        rvConsumersList.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spPersons) {
            if (position > 0) {
                loadConsumers(personIds.get(spPersons.getSelectedItemPosition() - 1));
                vNumber.setVisibility(View.VISIBLE);
                vMoney.setVisibility(View.VISIBLE);
                vTitles.setVisibility(View.VISIBLE);
            } else {
                vNumber.setVisibility(View.GONE);
                vMoney.setVisibility(View.GONE);
                vTitles.setVisibility(View.GONE);
                consumersList.clear();
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void loadConsumers(String personId) {
        consumersList.clear();
        SQLiteDatabase db = DbManager.getInstance(activity).getReadableDatabase();

        Cursor mobileNo = db.query(DbManager.Person.TABLE_NAME, new String[]{DbManager.Person.MOBILE},
                DbManager.Person.ID + "=?", new String[]{personId}, null, null, null);
        if (mobileNo.moveToNext())
            tvMobileNumber.setText(mobileNo.getString(0));
        mobileNo.close();

        //Get Total Money Spend
        String totalMoneySpend = "SELECT SUM(" + DbManager.AddMoney.MONEY_SPEND + ") FROM " + DbManager.AddMoney.TABLE_NAME
                + " where " + DbManager.AddMoney.SPONSOR_ID + "=?";
        Cursor totalMoneySpendCur = db.rawQuery(totalMoneySpend, new String[]{personId});

        if (totalMoneySpendCur.moveToNext()) {
            tvMoneySponsored.setText(String.format("%s ₹", totalMoneySpendCur.getDouble(0)));
        }
        totalMoneySpendCur.close();


        for (int i = 0; i < personIds.size(); i++) {
            if (personId.equals(personIds.get(i))) continue;

            String id = personIds.get(i);
            Person info = new Person();
            info.setPersonId(id);

            //Get Money Spend by selected person
            String query = "SELECT SUM(P" + id + ") FROM " + DbManager.AddMoney.TABLE_NAME
                    + " where " + DbManager.AddMoney.SPONSOR_ID + "=?";
            Cursor cur = db.rawQuery(query, new String[]{personId});
            info.setName(persons.get(i + 1));
            double value = 0;
            if (cur.moveToFirst()) {
                value = cur.getDouble(0);
            }
            cur.close();
            //Get Money Spend by other person
            String secondQuery = "SELECT SUM(P" + personId + ") FROM " + DbManager.AddMoney.TABLE_NAME
                    + " where " + DbManager.AddMoney.SPONSOR_ID + "=?";
            Cursor secondCursor = db.rawQuery(secondQuery, new String[]{id});
            if (secondCursor.moveToNext()) {
                value = value - secondCursor.getDouble(0);
            }
            if (value >= 0) {
                info.setMoneyIn(String.valueOf(String.format(Locale.ENGLISH, "%.2f", value)));
                info.setMoneyOut("0");
            } else {
                info.setMoneyIn("0");
                info.setMoneyOut(String.valueOf(String.format(Locale.ENGLISH, "%.2f", value * -1)));
            }

            consumersList.add(info);
            secondCursor.close();
        }
        db.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
