package com.example.traceme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    //    Database Name
    public static final String DATABASE_NAME = "TraceMe.db";

    //    Table Location
    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_NAME_ID = "KeyId";

    public static final String CreateLocation = "CREATE TABLE " + TABLE_LOCATION + " (" +
            COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "lattitude TEXT, longitude TEXT, timestamp TEXT, isSyncedLocation TEXT)";

    public static final String SQLDeleteLocation = "DROP TABLE IF EXISTS "+ TABLE_LOCATION;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("traced","OnCreateDb Called") ;
        db.execSQL(CreateLocation);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQLDeleteLocation);

    }

    //   Location Start

    public boolean insertLoc(double lat, double lon, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("lattitude", lat);
        contentValues.put("longitude", lon);
        contentValues.put("timestamp", date);
        Log.i("LtLng", String.valueOf(lat));
        long result = db.insertWithOnConflict(TABLE_LOCATION, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }


    public Cursor getLoc(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_LOCATION,null);
        return res;
    }

    public Cursor getNewLocation(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from  "+TABLE_LOCATION+" where isSyncedLocation isNull",null);
        return res;
    }


    public void locationUpdated(){
        SQLiteDatabase db = this.getWritableDatabase();
        String qr = "update location set isSyncedLocation  = '1' where isSyncedLocation isNull";
        db.execSQL(qr);
//        ContentValues updated = new ContentValues();
//        updated.put("isSyncedContact",1);
//        db.update(TABLE_CONTACTS,updated,"isSyncedContact isNull" , null);
//        db.rawQuery("update "+TABLE_CONTACTS+" set isSyncedContact = '1' where isSyncedContact = ?",null);
        Log.i("Updated","LocationSynced");
    }

    public void deleteLoc(){
        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_LOCATION, COLUMN_NAME_ID + " <= ?" , new String[]{String.valueOf(rowId)});
        String qr = "delete from "+TABLE_LOCATION+" where isSyncedLocation = '1'";
        db.execSQL(qr);
    }
}
