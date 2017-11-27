package com.room.accountbook;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.room.accountbook.db.DbManager;
import com.room.accountbook.fragments.AddMoney;
import com.room.accountbook.fragments.AddPerson;
import com.room.accountbook.fragments.BaseFragment;
import com.room.accountbook.fragments.ManageBook;
import com.room.accountbook.fragments.MoneySpentList;
import com.room.accountbook.fragments.PersonInfo;
import com.room.accountbook.utils.Helper;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.TitleChangeListerner {

    private TextView tvTitle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findValues();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigateScreen(MoneySpentList.newInstance(), MoneySpentList.class.getSimpleName(), getString(R.string.money_spent));

    }

    private void findValues() {
        drawer = findViewById(R.id.drawer_layout);
        tvTitle = findViewById(R.id.tvTitle);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Helper.hideKeyboard(MainMenu.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Helper.confirmDialog(this, getString(R.string.want_to_exit), getString(android.R.string.yes), getString(android.R.string.no), new Helper.IL() {
                @Override
                public void onSuccess() {
                    MainMenu.super.onBackPressed();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    private int lastId = 0;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        lastId = id;

        if (id == R.id.navSpendMoneyList) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            navigateScreen(MoneySpentList.newInstance(), MoneySpentList.class.getSimpleName(), getString(R.string.money_spent));
        } else if (id == R.id.navAddMoney) {
            if (getPersonsCount() < 2) {
                Helper.ting(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_atleast_2_persons));
                lastId = 0;
                return false;
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            navigateScreen(AddMoney.newInstance(), AddMoney.class.getSimpleName(), getString(R.string.add_money));
        } else if (id == R.id.navAddPerson) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            navigateScreen(AddPerson.newInstance(), AddPerson.class.getSimpleName(), getString(R.string.add_person));
        } else if (id == R.id.navManage) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            navigateScreen(ManageBook.newInstance(), ManageBook.class.getSimpleName(), getString(R.string.manage_book));
        } else if (id == R.id.navPersonInfo) {
            if (getPersonsCount() < 2) {
                Helper.ting(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.add_atleast_2_persons));
                lastId = 0;
                return false;
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            navigateScreen(PersonInfo.newInstance(), PersonInfo.class.getSimpleName(), getString(R.string.person_info));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateScreen(Fragment fragment, String tag, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();

//        tvTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Helper.hideKeyboard(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public long getPersonsCount() {
        SQLiteDatabase db = DbManager.getInstance(this).getReadableDatabase();
        long val = DatabaseUtils.queryNumEntries(db, DbManager.Person.TABLE_NAME);
        db.close();
        return val;
    }


    @Override
    public void onTitleChanged(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }
}
