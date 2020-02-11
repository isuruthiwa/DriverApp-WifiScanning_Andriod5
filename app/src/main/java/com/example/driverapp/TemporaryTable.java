package com.example.driverapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TemporaryTable extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TempTable.db";
    public static final String TABLEA = "tempTableA";
    public static final String TABLEB = "tempTableB";
    public static final String COL_1 = "TABLE_ID";
    public static final String COL_2 = "ROAD_SIGN";
    public static final String COL_3 = "LONGI";
    public static final String COL_4 = "LATI";
    public static final String COL_5 = "DISTANCE";
    public static final String COL_6 = "Flag";        //heading
    public TemporaryTable(Context context) {
        super(context, DATABASE_NAME, null, 2);
    } //Create Database

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLEA +" (TABLE_ID INTEGER PRIMARY KEY ,ROAD_SIGN INTEGER,LONGI LONG," +
                "LATI LONG,DISTANCE LONG,Flag LONG)");
        db.execSQL("create table " + TABLEB +" (TABLE_ID INTEGER PRIMARY KEY ,ROAD_SIGN INTEGER,LONGI LONG," +
                "LATI LONG,DISTANCE LONG,Flag LONG)");
    }// Create Table  //ID is automatically increment

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLEA);
        db.execSQL("DROP TABLE IF EXISTS "+TABLEB);
        onCreate(db);
    }

    public boolean insertData(Integer table1id,String roadsignt1,String longit1,String latit1,Integer distance,Double flag, String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,roadsignt1);
        contentValues.put(COL_3,longit1);
        contentValues.put(COL_4,latit1);
        contentValues.put(COL_5,distance);
        contentValues.put(COL_6,flag);


        long result = db.insert(TABLE_NAME,null ,contentValues); //*******************************************
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String table1id,String roadsignt1,String longit1,String latit1,Float distance,Double flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,table1id);
        contentValues.put(COL_2,roadsignt1);
        contentValues.put(COL_3,longit1);
        contentValues.put(COL_4,latit1);
        contentValues.put(COL_5,distance);
        contentValues.put(COL_6,flag);
        db.update("tempTableA", contentValues, "TABLE_ID = ?",new String[] { table1id });
        return true;
    }

    public Integer deleteData (String id, String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "TABLE_ID = ?",new String[] {id});
    }

    public Cursor fetch(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + "tempTableA" +" where TABLE_ID = ?", new String[]{id});
            //return cursor;
        return cursor;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLEA);
        db.execSQL("delete from "+ TABLEB);
        db.close();
    }

    public long getRoadSignCount() {
        String TABLE_NAME = "tempTableA";
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        System.out.println("Count "+count);
        return count;

    }
}
