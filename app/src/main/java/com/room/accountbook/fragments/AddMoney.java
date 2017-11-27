package com.room.accountbook.fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.room.accountbook.R;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link AddMoney#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMoney extends BaseFragment implements View.OnClickListener {

    private EditText etDate;
    private Calendar myCalendar;
    private Spinner spSponsar;
    private LinearLayout vPersonList;
    private FloatingActionButton bvSave;
    private View parent;
    private EditText etMoney;
    private Spinner spTime;

    public AddMoney() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneySpentList.
     */
    public static AddMoney newInstance() {
        AddMoney fragment = new AddMoney();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.add_money));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_money, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = view;
        myCalendar = Calendar.getInstance();
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
        spSponsar.setAdapter(spinnerArrayAdapter);

        for (int i = 1; i < persons.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setText(persons.get(i));
            checkBox.setLayoutParams(params);
            vPersonList.addView(checkBox);
        }

    }

    private void findValues(View view) {
        etMoney = view.findViewById(R.id.etMoney);
        bvSave = view.findViewById(R.id.bvSave);
        etDate = view.findViewById(R.id.etDate);
        spSponsar = view.findViewById(R.id.spSponsar);
        vPersonList = view.findViewById(R.id.vPersonList);
        spTime = view.findViewById(R.id.spTime);
        etDate.setOnClickListener(this);
        bvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etDate:
                showPicker();
                break;
            case R.id.bvSave:
                if (isValidated()) {
                    saveData();
                }
                break;
        }
    }

    private void saveData() {
        List<String> selectedNames = new ArrayList<>();
        for (int i = 0; i < vPersonList.getChildCount(); i++) {
            if (((CheckBox) vPersonList.getChildAt(i)).isChecked()) {
                selectedNames.add(personIds.get(i));
            }
        }

        double money = getDouble(etMoney.getText().toString()) / selectedNames.size();

        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbManager.AddMoney.SPONSOR_ID, personIds.get(spSponsar.getSelectedItemPosition() - 1));
        values.put(DbManager.AddMoney.MONEY_SPEND, getDouble(etMoney.getText().toString()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = sdf.format(myCalendar.getTime());
        values.put(DbManager.AddMoney.DATE, date);
        values.put(DbManager.AddMoney.TIME, spTime.getSelectedItem().toString());

        for (String name : selectedNames) {
            values.put("P" + name, String.format(Locale.ENGLISH, "%.2f", money));
        }

        long val = db.insert(DbManager.AddMoney.TABLE_NAME, null, values);
        db.close();

        if (val > 0) {
            bvSave.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    Helper.alert(activity, getString(R.string.money_adde_success), false, new Helper.IL() {
                        @Override
                        public void onSuccess() {
                            NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
                            navigationView.getMenu().getItem(0).setChecked(true);
                            Helper.hideKeyboard(activity);
                            navigateScreen(MoneySpentList.newInstance(), MoneySpentList.class.getSimpleName());
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            });
        } else {
            Helper.ting(parent, getString(R.string.error_occured));
        }
    }

    private Double getDouble(String val) {
        try {
            return Double.valueOf(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0D;
    }

    private void showPicker() {
        DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        long maxDate = System.currentTimeMillis();
        long minDate = maxDate - 13 * 86400000;
        dialog.getDatePicker().setMaxDate(maxDate);
        dialog.getDatePicker().setMinDate(minDate);
        dialog.show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etDate.setText(dayOfMonth + "/" + (monthOfYear + 1));
        }

    };

    public boolean isValidated() {
        if (spSponsar.getSelectedItemPosition() == 0) {
            Helper.ting(parent, getString(R.string.select_sponsor));
            return false;
        } else if (TextUtils.isEmpty(etMoney.getText())) {
            Helper.ting(parent, getString(R.string.enter_money_spend));
            return false;
        } else if (TextUtils.isEmpty(etDate.getText())) {
            Helper.ting(parent, getString(R.string.select_date));
            return false;
        } else if (!isAnyPersonSelected()) {
            Helper.ting(parent, getString(R.string.select_consumed_person));
            return false;
        }
        return true;
    }

    public boolean isAnyPersonSelected() {
        for (int i = 0; i < vPersonList.getChildCount(); i++) {
            if (((CheckBox) vPersonList.getChildAt(i)).isChecked()) {
                return true;
            }
        }
        return false;
    }
}
