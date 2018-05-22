package com.room.accountbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by subash on 3/11/17.
 */

public class DbManager extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "Accounts";

    public DbManager(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Person.CREATE);
        db.execSQL(AddMoney.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DbManager dbManager;

    //stock
    public interface Person {
        String ID = "id";
        String NAME = "name";
        String TABLE_NAME = "person";
        String MOBILE = "mobile";

        String CREATE = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT NOT NULL DEFAULT '',"
                + MOBILE + " TEXT NOT NULL DEFAULT '');";
    }

    public interface AddMoney {
        String TABLE_NAME = "AddMoney";
        String ID = "id";
        String TIME = "time";
        String DATE = "spent_date";
        String SPONSOR_ID = "sponsor_id";
        String MONEY_SPEND = "money_spend";
        String SPEND_FOR = "spend_for";

        String CREATE = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY,"
                + SPONSOR_ID + " TEXT NOT NULL DEFAULT '',"
                + MONEY_SPEND + " FLOAT NOT NULL DEFAULT '0',"
                + TIME + " TEXT NOT NULL DEFAULT '',"
                + SPEND_FOR + " TEXT NOT NULL DEFAULT '',"
                + DATE + " Date);";
    }

    public static DbManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DbManager(context);
        }
        return dbManager;
    }

}
