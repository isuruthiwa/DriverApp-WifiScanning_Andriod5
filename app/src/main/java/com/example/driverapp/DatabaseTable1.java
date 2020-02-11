package com.example.driverapp;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseTable1 extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Table_1.db";
    public static final String TABLE_NAME = "Table_1";
    public static final String AREAPREV = "areaPrev";
    public static final String AVALAREA = "avalArea";
    public static final String COL_1 = "TABLE_1_ID";
    public static final String COL_2 = "AREA_ID";
    public static final String COL_3 = "ROAD_SIGN_T1";
    public static final String COL_4 = "LONGI_T1";
    public static final String COL_5 = "LATI1_T2";
    public static final String COL_6 = "HEADING_T1";


    public DatabaseTable1(Context context) {
        super(context, DATABASE_NAME, null, 4);
    } //Create Database

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Road sign data table
        db.execSQL("create table " + TABLE_NAME +" (TABLE_1_ID INTEGER PRIMARY KEY AUTOINCREMENT,AREA_ID INTEGER,ROAD_SIGN_T1 INTEGER," +
                "LONGI_T1 DOUBLE, LATI1_T2 DOUBLE, HEADING_T1 DOUBLE)");

        //Parameter data table
        // 1 . previous area
        // 2 . Last entry of the road sign
        db.execSQL("create table " + AREAPREV +" (TABLE_1_ID INTEGER PRIMARY KEY ,AREA_ID INTEGER)");

        //Area availability table
        db.execSQL("create table " + AVALAREA +" (TABLE_1_ID INTEGER PRIMARY KEY ,AREA_ID INTEGER)");
    }// Create Table  //ID is automatically increment

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+AREAPREV);
        db.execSQL("DROP TABLE IF EXISTS "+AVALAREA);
        onCreate(db);
    }

    public boolean insertData(String areaid, String roadsignt1,String longit1,String latit1,String headingt1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,areaid);
        contentValues.put(COL_3,roadsignt1);
        contentValues.put(COL_4,longit1);
        contentValues.put(COL_5,latit1);
        contentValues.put(COL_6,headingt1);

        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


    public boolean insertPrev(String table1id,String areaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,areaid);

        long result = db.insert(AREAPREV,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertAvalarea(String table1id,String areaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,areaid);

        long result = db.insert(AREAPREV,null ,contentValues);
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

    public boolean updateData(String table1id,String areaid, String roadsignt1,String longit1,String latit1,String headingt1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,areaid);
        contentValues.put(COL_3,roadsignt1);
        contentValues.put(COL_4,longit1);
        contentValues.put(COL_5,latit1);
        contentValues.put(COL_6,headingt1);
        db.update(TABLE_NAME, contentValues, "TABLE_1_ID = ?",new String[] { table1id });
        return true;
    }

    public boolean updateprev(String table1id,String areaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,areaid);

        db.update(AREAPREV, contentValues, "TABLE_1_ID = ?",new String[] { table1id });
        return true;
    }

    public boolean updateAvalarea(String table1id,String areaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,areaid);

        db.update(AREAPREV, contentValues, "TABLE_1_ID = ?",new String[] { table1id });
        return true;
    }



    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "TABLE_1_ID = ?",new String[] {id});
    }

    public Cursor fetch(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Table_1 where AREA_ID = ?", new String[]{id});
        return cursor;
    }

    public Cursor fetchprev(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from areaPrev where TABLE_1_ID = ?", new String[]{id});
        return cursor;
    }

    public Cursor fetchAvalarea(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from areaPrev where TABLE_1_ID = ?", new String[]{id});
        return cursor;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    public long getRoadSignCount() {


        String TABLE_NAME = "Table_1";
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;

    }
}
