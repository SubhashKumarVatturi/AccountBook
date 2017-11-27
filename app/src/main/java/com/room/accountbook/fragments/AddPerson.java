package com.room.accountbook.fragments;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.room.accountbook.R;
import com.room.accountbook.db.DbManager;
import com.room.accountbook.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link AddPerson#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPerson extends BaseFragment {

    private FloatingActionButton bvDone;
    private EditText etName;
    private EditText etMobileNo;
    private TextInputLayout vNameLayout;
    private TextInputLayout vNumber;
    private View parent;

    public AddPerson() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneySpentList.
     */
    public static AddPerson newInstance() {
        AddPerson fragment = new AddPerson();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setTitle( getString(R.string.add_person));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_person, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.parent = view;
        findValues(view);
    }

    private void findValues(View view) {
        bvDone = view.findViewById(R.id.bvDone);
        etName = view.findViewById(R.id.etMoney);
        etMobileNo = view.findViewById(R.id.etMobileNo);
        vNameLayout = view.findViewById(R.id.vNameLayout);
        vNumber = view.findViewById(R.id.vNumber);

        bvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidated()) {
                    saveData();
                }
            }
        });
    }

    private void saveData() {
        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbManager.Person.NAME, etName.getText().toString().trim());
        values.put(DbManager.Person.MOBILE, etMobileNo.getText().toString().trim());
        long val = db.insert(DbManager.Person.TABLE_NAME, null, values);

        if (val > 0)
            db.execSQL("ALTER TABLE " + DbManager.AddMoney.TABLE_NAME + " ADD COLUMN P" +
                    val + " DOUBLE NOT NULL DEFAULT '0'");

        db.close();
        if (val > 0) {
            bvDone.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    Helper.alert(activity, getString(R.string.person_added_success,
                            etName.getText().toString().trim()), false, new Helper.IL() {
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

    public boolean isValidated() {
        if (TextUtils.isEmpty(etName.getText())) {
            vNameLayout.setError(getString(R.string.enter_name));
            return false;
        } else if (isNameExist()) {
            vNameLayout.setError(getString(R.string.name_exist));
            return false;
        } else if (TextUtils.isEmpty(etMobileNo.getText())) {
            vNumber.setError(getString(R.string.enter_mobile_no));
            return false;
        } else if (etMobileNo.getText().length() != 10 ||
                !(etMobileNo.getText().toString().trim().startsWith("7") ||
                        etMobileNo.getText().toString().trim().startsWith("8") ||
                        etMobileNo.getText().toString().trim().startsWith("9"))) {
            vNumber.setError(getString(R.string.enter_valid_no));
            return false;
        } else if (isMobileExist()) {
            vNumber.setError(getString(R.string.number_exist));
            return false;
        }
        return true;
    }

    public boolean isMobileExist() {
        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        long count = DatabaseUtils.longForQuery(db, "SELECT COUNT (*) FROM "
                        + DbManager.Person.TABLE_NAME + " WHERE " + DbManager.Person.MOBILE + "=?",
                new String[]{etMobileNo.getText().toString().trim()});
        db.close();
        return count > 0;
    }

    public boolean isNameExist() {
        SQLiteDatabase db = DbManager.getInstance(activity).getWritableDatabase();
        long count = DatabaseUtils.longForQuery(db, "SELECT COUNT (*) FROM "
                        + DbManager.Person.TABLE_NAME + " WHERE " + DbManager.Person.NAME + "=?",
                new String[]{etName.getText().toString().trim()});
        db.close();
        return count > 0;
    }
}
