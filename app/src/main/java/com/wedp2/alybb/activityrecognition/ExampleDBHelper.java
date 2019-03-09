package com.wedp2.alybb.activityrecognition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

// This is a classe to use ExampleDBHelper to manipulate SQLite database
public class ExampleDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteExample.db";
    /* use Database version to control the database schema*/
    private static final int DATABASE_VERSION = 1;
    public static final String INPUT_TABLE_NAME = "ActivityAndTime";
    public static final String INPUT_COLUMN_ID = "_id";
    public static final String INPUT_COLUMN_Time = "time";
    public static final String INPUT_COLUMN_Activity = "activity";

    public ExampleDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + INPUT_TABLE_NAME + "(" +
                INPUT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                INPUT_COLUMN_Time + " TEXT, " +
                INPUT_COLUMN_Activity + " TEXT)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + INPUT_TABLE_NAME);
        onCreate(db);
    }




    public boolean insertRecord(String time,String activity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INPUT_COLUMN_Time, time);
        contentValues.put(INPUT_COLUMN_Activity, activity);
        db.insert(INPUT_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }


    public Cursor getAllRecord() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + INPUT_TABLE_NAME, null );
//        if (res.moveToFirst()) {
//            while (!res.isAfterLast()) {
//
//                HashMap<String, String> map = new HashMap<>();
//                map.put(INPUT_COLUMN_ID,
//                        res.getString(res.getColumnIndex(INPUT_COLUMN_ID)));
//                map.put(INPUT_COLUMN_Time,
//                        res.getString(res.getColumnIndex(INPUT_COLUMN_Time)));
//                map.put(INPUT_COLUMN_Activity,
//                        res.getString(res.getColumnIndex(INPUT_COLUMN_Activity)));
//                res.moveToNext();
//            }
//        }
        return res;
    }


    public Cursor getLastRecord(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + INPUT_TABLE_NAME + " WHERE " +
                INPUT_COLUMN_ID + "=?", new String[] { id } );
        return res;
    }


    public void delete(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INPUT_TABLE_NAME, null,null);
    }


}