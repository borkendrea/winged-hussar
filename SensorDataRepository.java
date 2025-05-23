package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SensorDataRepository {
    private final SensorDatabaseHelper dbHelper;

    public SensorDataRepository(Context context) {
        dbHelper = new SensorDatabaseHelper(context);
    }

    public void insert(SensorData data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SensorDatabaseHelper.COL_TIMESTAMP, data.timestamp);
        values.put(SensorDatabaseHelper.COL_X, data.x);
        values.put(SensorDatabaseHelper.COL_GRADE, data.earthquakeGrade);
        values.put(SensorDatabaseHelper.COL_BUZZER, data.buzzerState);
        values.put(SensorDatabaseHelper.COL_AX, data.ax_g);
        values.put(SensorDatabaseHelper.COL_AY, data.ay_g);
        values.put(SensorDatabaseHelper.COL_AZ, data.az_g);
        db.insert(SensorDatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }
}