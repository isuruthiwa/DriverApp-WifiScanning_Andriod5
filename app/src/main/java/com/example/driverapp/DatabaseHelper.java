package com.example.driverapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Area.db";
    public static final String TABLE_NAME = "Area_table";
    public static final String COL_1 = "AREA_ID";
    public static final String COL_2 = "LONGI1";
    public static final String COL_3 = "LONGI2";
    public static final String COL_4 = "LATI1";
    public static final String COL_5 = "LATI2";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    } //Create Database

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (AREA_ID INTEGER PRIMARY KEY ,LONGI1 DOUBLE,LONGI2 DOUBLE,LATI1 DOUBLE,LATI2 DOUBLE)");
    }// Create Table

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String areaid,String longi1,String longi2,String lati1,String lati2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,areaid);
        contentValues.put(COL_2,longi1);
        contentValues.put(COL_3,longi2);
        contentValues.put(COL_4,lati1);
        contentValues.put(COL_5,lati2);

        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,String longi1,String longi2,String lati1,String lati2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,longi1);
        contentValues.put(COL_3,longi2);
        contentValues.put(COL_4,lati1);
        contentValues.put(COL_5,lati2);
        db.update(TABLE_NAME, contentValues, "AREA_ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "AREA_ID = ?",new String[] {id});
    }
}