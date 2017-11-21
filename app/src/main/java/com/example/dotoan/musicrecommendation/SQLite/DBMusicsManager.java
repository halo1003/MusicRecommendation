package com.example.dotoan.musicrecommendation.SQLite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class DBMusicsManager extends SQLiteOpenHelper {

    static String DBname = "parameter";
    static String TBname = "music_tb";
    static String id = "id";
    static String mid = "mid";

    public DBMusicsManager(Context context) {
        super(context, DBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void CreateTB(){
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id+" INTEGER PRIMARY KEY,"+mid+" INT)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuerry);
        Log.e("DBmanager","create TB success");
    }

    public void DropTB(){
        String sqlDrop = "DROP TABLE IF EXISTS "+TBname;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDrop);
        Log.e("DropTB","Drop success");
    }
}
