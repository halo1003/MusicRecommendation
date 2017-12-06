package com.example.dotoan.musicrecommendation.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.Contruct.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/10/2017.
 */

public class DBShow extends SQLiteOpenHelper {
    static String DBname = "Show";
    static public String TBname = "ShowTB";
    static String id = "id";
    static String name = "name";
    static String artic = "artic";
    static String mid = "mid";

    static String TAGNAME = "DBShow.";
    Context context;

    public DBShow(Context context) {
        super(context, DBname, null, 1);
    }

    public DBShow(Context context, Node node) {
        super(context,DBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TBname);
        onCreate(db);
    }

    public void CreateTB(){
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id+" INTEGER PRIMARY KEY,"+name+" STRING,"+artic+" STRING,"+mid+" STRING)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuerry);
        Log.e(TAGNAME,"create TB success");
    }


    public void DropTB(){
        String sqlDrop = "DROP TABLE IF EXISTS "+TBname;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDrop);
        Log.e(TAGNAME,"Drop success");
    }

    public List<MusicC> getAllNode(String TBname) {
        List<MusicC> listStudent = new ArrayList<MusicC>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TBname;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MusicC musicC = new MusicC();
                musicC.set_id(cursor.getInt(0));
                musicC.setMname(cursor.getString(1));
                musicC.setAname(cursor.getString(2));
                musicC.setMid(cursor.getString(3));
                listStudent.add(musicC);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listStudent;
    }

    public long getNodesCount(String TBname) {
        String countQuery = "SELECT  * FROM " + TBname;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        long c  = cursor.getCount();
        cursor.close();
        // return count
        return c;
    }

    public void addNode(MusicC node){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(id, node.get_id());
        values.put(name,node.getMname());
        values.put(artic,node.getAname());
        values.put(mid,node.getMid());

        db.insert(TBname,null,values);
        db.close();
    }
}
