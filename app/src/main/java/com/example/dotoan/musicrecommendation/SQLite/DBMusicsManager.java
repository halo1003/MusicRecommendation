package com.example.dotoan.musicrecommendation.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Mdetail;
import com.example.dotoan.musicrecommendation.Contruct.Node;
import com.example.dotoan.musicrecommendation.Contruct.itemC;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class DBMusicsManager extends SQLiteOpenHelper {

    static String DBname = "parameter";
    static String TBname = "music_tb";
    static String id = "id";
    static String mid = "mid";
    static String track = "trackid";
    static String mname = "mname";
    static String aname = "aname";

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
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+mid+" STRING PRIMARY KEY,"+mname+" STRING,"+aname+" STRING)";
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

    public void addNode(Mdetail m){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(mid, m.getMid());
        values.put(mname, m.getMname());
        values.put(aname,m.getAname());

        db.insert(TBname,null,values);
        Log.i("addNode",m.getMid()+" | "+m.getMname()+" | "+m.getAname());
        db.close();
    }
}
