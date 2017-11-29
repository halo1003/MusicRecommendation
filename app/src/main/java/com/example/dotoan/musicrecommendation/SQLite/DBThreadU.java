package com.example.dotoan.musicrecommendation.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.Contruct.Mdetail;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.Contruct.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/29/2017.
 */

public class DBThreadU extends SQLiteOpenHelper{
    static String DBname = "ThreadU";

    public static String cenTBi = "cenTB_initial";
    public static String cenTB = "cenTB";
    public static String objTBi = "objTB_initial";
    public static String objTB = "objTB";
    public static String simTB = "SimTB";

    static String id_Col = "idC";
    static String mid_Col = "midC";
    static String order_Col = "oderC";

    static String id = "id";
    static String obj = "obj";
    static String distance = "dis";

    public DBThreadU(Context context) {
        super(context, DBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void CreateTB(String TBname){
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id_Col+" INTEGER PRIMARY KEY AUTOINCREMENT,"+mid_Col+" STRING,"+order_Col+" STRING)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuerry);
        //Log.e("DBmanager","create "+TBname+" success");
    }

    public void CreateTB_forObj(String TBname){
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id_Col+" INTEGER,"+mid_Col+" STRING,"+order_Col+" STRING)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuerry);
        //Log.e("DBmanager","create "+TBname+" success");
    }

    public void CreateTB_forSim(String TBname){
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+obj+" INTEGER,"+distance+" DOUBLE)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuerry);
        //Log.e("DBmanager","create "+TBname+" success");
    }

    public void DropTB(String TBname){
        String sqlDrop = "DROP TABLE IF EXISTS "+TBname;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDrop);
        //Log.e("DropTB","Drop "+TBname+" success");
    }

    public void addNode(String TBname, String mid, String order){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(mid_Col, mid);
        values.put(order_Col,order);

        db.insert(TBname,null,values);
        db.close();
    }

    public void addNode_forObj(String TBname,int pos, String mid, String order){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(id_Col,pos);
        values.put(mid_Col, mid);
        values.put(order_Col,order);

        db.insert(TBname,null,values);
        db.close();
    }

    public void addNode_forSim(String TBname, int o, double d){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(obj, o);
        values.put(distance,d);

        db.insert(TBname,null,values);
        db.close();
    }

    public List<Item> getAllNode(String TBname) {
        List<Item> listStudent = new ArrayList<Item>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TBname;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setMid(cursor.getString(1));
                item.setOrder(cursor.getString(2));
                listStudent.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listStudent;
    }

    public List<Item> getAllNode_forSim(String TBname) {
        List<Item> listStudent = new ArrayList<Item>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TBname;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(1));
                item.setOrder(cursor.getDouble(2)+"");
                listStudent.add(item);
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
}
