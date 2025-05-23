package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SensorDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "sensor_data.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "sensor_data";
    public static final String COL_ID = "_id";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_X = "x";
    public static final String COL_GRADE = "earthquake_grade";
    public static final String COL_BUZZER = "buzzer_state";
    public static final String COL_AX = "ax_g";
    public static final String COL_AY = "ay_g";
    public static final String COL_AZ = "az_g";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_TIMESTAMP + " INTEGER," +
                    COL_X + " REAL," +
                    COL_GRADE + " INTEGER," +
                    COL_BUZZER + " INTEGER," +
                    COL_AX + " REAL," +
                    COL_AY + " REAL," +
                    COL_AZ + " REAL" +
                    ");";

    public SensorDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 可根据版本升级
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}