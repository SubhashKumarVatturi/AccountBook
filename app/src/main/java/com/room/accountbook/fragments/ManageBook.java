package com.room.accountbook.fragments;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.room.accountbook.R;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.utils.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link ManageBook#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageBook extends BaseFragment implements View.OnClickListener {


    private ImageButton bvClear;
    private View parent;
    private Spinner spPersons;
    private ImageButton bvDeletePerson;

    public ManageBook() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneySpentList.
     */
    public static ManageBook newInstance() {
        ManageBook fragment = new ManageBook();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.manage_book));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_book, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.parent = view;
        findValues(view);
        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        loadPersons(db);
        db.close();
    }

    List<String> persons = new ArrayList<>();
    List<String> personIds = new ArrayList<>();

    private void loadPersons(SQLiteDatabase db) {
        Cursor cursor = db.query(DbManager.Person.TABLE_NAME,
                new String[]{DbManager.Person.NAME, DbManager.Person.ID},
                null, null, null, null, DbManager.Person.NAME);

        persons.clear();
        personIds.clear();
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
        spPersons = view.findViewById(R.id.spPersons);
        bvClear = view.findViewById(R.id.bvClear);
        bvDeletePerson = view.findViewById(R.id.bvDeletePerson);
        bvClear.setOnClickListener(this);
        bvDeletePerson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bvClear && isMoneyAvailable()) {
            Helper.confirmDialog(activity, getString(R.string.want_to_clear_spend_data), getString(android.R.string.yes), getString(android.R.string.no), new Helper.IL() {
                @Override
                public void onSuccess() {
                    clearSpendData();
                }

                @Override
                public void onCancel() {

                }
            });
        } else if (v.getId() == R.id.bvDeletePerson) {
            if (spPersons.getSelectedItemPosition() == 0) {
                Helper.ting(parent, getString(R.string.select_person));
                return;
            }
            Helper.confirmDialog(activity, getString(R.string.want_to_delete, spPersons.getSelectedItem().toString()),
                    getString(android.R.string.yes), getString(android.R.string.no), new Helper.IL() {
                        @Override
                        public void onSuccess() {
                            deletePerson();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }
    }

    private void deletePerson() {
        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        db.delete(DbManager.AddMoney.TABLE_NAME, DbManager.AddMoney.SPONSOR_ID + "=?",
                new String[]{personIds.get(spPersons.getSelectedItemPosition() - 1)});

        //delete person and delete person spent data
        String backupTable = "backupTable";
        //create back table
        db.execSQL(getBackupQuery(backupTable, true));

        //copy data from original table
        db.execSQL(getInsertQuery(DbManager.AddMoney.TABLE_NAME, backupTable));
        //DROP TABLE addmoney;
        db.execSQL("DROP TABLE IF EXISTS " + DbManager.AddMoney.TABLE_NAME);
        //Create addmoney
        db.execSQL(getBackupQuery(DbManager.AddMoney.TABLE_NAME, false));
        //Add values
        db.execSQL(getInsertQuery(backupTable, DbManager.AddMoney.TABLE_NAME));

        if (personIds.size() > 1) {
            StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE " + DbManager.AddMoney.TABLE_NAME
                    + " SET " + DbManager.AddMoney.MONEY_SPEND + " = ");

            for (int p = 0; p < personIds.size(); p++) {
                if (p != spPersons.getSelectedItemPosition() - 1) {
                    if (p != spPersons.getSelectedItemPosition() - 1) {

                        updateQuery.append("P" + personIds.get(p));
                        if (spPersons.getSelectedItemPosition() == personIds.size()) {
                            if (p < personIds.size() - 2) {
                                updateQuery.append("+");
                            }
                        } else {
                            if (p < personIds.size() - 1) {
                                updateQuery.append("+");
                            }
                        }

                    }
                }
            }

            db.execSQL(updateQuery.toString());
        }
        int val = db.delete(DbManager.Person.TABLE_NAME, DbManager.Person.ID + "=?",
                new String[]{personIds.get(spPersons.getSelectedItemPosition() - 1)});

        if (val > 0) {
            loadPersons(db);
            Helper.alert(activity, getString(R.string.person_delete_success, spPersons.getSelectedItem().toString()));
        } else {
            Helper.ting(parent, getString(R.string.error_occured));
        }

        db.close();
    }

    private String getInsertQuery(String fromTable, String toTable) {
        StringBuilder copyTable = new StringBuilder();
        copyTable.append("insert into " + toTable + " select "
                + DbManager.AddMoney.ID + ","
                + DbManager.AddMoney.SPONSOR_ID + ","
                + DbManager.AddMoney.MONEY_SPEND + ","
                + DbManager.AddMoney.TIME + ","
                + DbManager.AddMoney.DATE);
        for (int p = 0; p < personIds.size(); p++) {
            if (p != spPersons.getSelectedItemPosition() - 1) {
                copyTable.append(",");
                copyTable.append("P" + personIds.get(p));
            }
        }
        copyTable.append(" from " + fromTable);
        return copyTable.toString();
    }

    private String getBackupQuery(String backupTable, boolean isTemp) {
        StringBuilder backupTableQuery = new StringBuilder();
        backupTableQuery.append("CREATE ");
        if (isTemp)
            backupTableQuery.append(" TEMPORARY ");
        backupTableQuery.append(" TABLE ");
        backupTableQuery.append(backupTable + " ("
                + DbManager.AddMoney.ID + " INTEGER PRIMARY KEY,"
                + DbManager.AddMoney.SPONSOR_ID + " TEXT NOT NULL DEFAULT '',"
                + DbManager.AddMoney.MONEY_SPEND + " FLOAT NOT NULL DEFAULT '0',"
                + DbManager.AddMoney.TIME + " TEXT NOT NULL DEFAULT '',"
                + DbManager.AddMoney.DATE + " Date");
        for (int p = 0; p < personIds.size(); p++) {
            if (p != spPersons.getSelectedItemPosition() - 1) {
                backupTableQuery.append(",");
                backupTableQuery.append("P" + personIds.get(p) + " DOUBLE NOT NULL DEFAULT '0'");
            }
        }
        backupTableQuery.append(")");
        return backupTableQuery.toString();
    }

    private void clearSpendData() {
        SQLiteDatabase db = DbManager.getInstance(activity).getReadableDatabase();
        long val = db.delete(DbManager.AddMoney.TABLE_NAME, "1", null);
        db.close();
        if (val > 0) {
            Helper.alert(activity, getString(R.string.spend_muney_cleared_success));
        } else {
            Helper.ting(parent, getString(R.string.error_occured));
        }
    }

    public boolean isMoneyAvailable() {
        SQLiteDatabase db = DbManager.getInstance(activity).getReadableDatabase();
        long val = DatabaseUtils.queryNumEntries(db, DbManager.AddMoney.TABLE_NAME);
        db.close();

        if (val == 0) {
            Helper.ting(parent, getString(R.string.data_no_available));
        }

        return val > 0;
    }
}
