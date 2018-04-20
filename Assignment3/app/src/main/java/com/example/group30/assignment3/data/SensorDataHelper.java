package com.example.group30.assignment3.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Dishank on 3/5/2018.
 */

public class SensorDataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Group30.db";

    public static final int DATABASE_VERSION = 3;

    public SensorDataHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "Android"
                + File.separator + "data"
                + "/CSE535_ASSIGNMENT3"
                + File.separator
                + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1;i<=50;i++){
            stringBuilder.append("X"+i+" REAL,");
            stringBuilder.append("Y"+i+" REAL,");
            stringBuilder.append("Z"+i+" REAL,");
        }

        String CREATE_TABLE = "CREATE TABLE " + SensorDataContract.SensorDataTable.TABLE_NAME + "(" +
                SensorDataContract.SensorDataTable.COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                stringBuilder +
                SensorDataContract.SensorDataTable.COLUMN_ACTIVITY_LABEL + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SensorDataContract.SensorDataTable.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void storeValues(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(SensorDataContract.SensorDataTable.TABLE_NAME, null, values);
        Log.d("Data", "store");
        db.close();
    }


}
