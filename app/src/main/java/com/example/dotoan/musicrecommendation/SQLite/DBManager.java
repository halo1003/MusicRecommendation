package com.example.dotoan.musicrecommendation.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.internal.ListenerClass;

/**
 * Created by DOTOAN on 11/10/2017.
 */

public class DBManager extends SQLiteOpenHelper {
    static String DBname = "parameter";
    static String TBname = "min_max";
    static String TBname_temp = "min_max_temp";
    static String TBname_music = "Musics_tb";
    static String id = "id";
    static String user_1 = "user_1";
    static String user_2 = "user_2";
    static String distance = "distance";
    Node node;

    Context context;

    public DBManager(Context context) {
        super(context, DBname, null, 1);
    }

    public DBManager(Context context, Node node) {
        super(context,DBname, null, 1);
        this.node = node;
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
        String sqlQuerry = "CREATE TABLE "+ TBname+"("+id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+user_1+" INT,"+user_2+" INT,"+distance+" DOUBLE)";
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

    public List<Node> getAllNode(String TBname) {
        List<Node> listStudent = new ArrayList<Node>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TBname;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = new Node();
                node.setID(cursor.getInt(0));
                node.setUser_1(cursor.getInt(1));
                node.setUser_2(cursor.getInt(2));
                node.setDistance(cursor.getDouble(3));
                listStudent.add(node);
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

    public void addNode(Node node){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(user_1, node.getUser_1());
        values.put(user_2, node.getUser_2());
        values.put(distance, node.getDistance());

        db.insert(TBname,null,values);
        Log.i("addNode",node.getUser_1()+" | "+node.getUser_2()+" | "+node.getDistance());
        db.close();
    }

    public void copyTable(){
        String sqlDrop = "DROP TABLE IF EXISTS "+TBname_temp;
        String sqlQuerry = "create table "+TBname_temp+" as select * from "+TBname;
        String sqlDelete = "DELETE FROM "+TBname_temp+" WHERE "+ user_1+"="+user_2;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlDrop);
        Log.e("DropTB","Drop success");

        db.execSQL(sqlQuerry);
        Log.e("DBmanager","Duplicate TB success");

        db.execSQL(sqlDelete);
        Log.e("DBmanager","Delete Zero success");
        db.close();
    }

    public void Delete_user2(int temp){
        String deleteQuery = "DELETE FROM "+TBname_temp+" WHERE "+user_2+"="+temp+" OR "+user_1+" = "+temp;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public int Min (String temp){
        String selectQuery = "SELECT MIN(NULLIF("+distance+",0)) FROM "+TBname_temp;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        db.close();
        return cursor.getInt(0);
    }

    public List<Node> cQuerry(){
        List<Node> li = new ArrayList<Node>();
        String selectQuery = "SELECT "+user_1+" FROM "+TBname_temp+" GROUP BY "+user_1;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = new Node();
                node.setUser_1(cursor.getInt(0));
                li.add(node);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }

    public List<Node> Querry(int temp){
        List<Node> li = new ArrayList<Node>();
        String selectQuery = "SELECT * FROM "+TBname_temp+" WHERE "+user_1+"="+temp;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = new Node();
                node.setID(cursor.getInt(0));
                node.setUser_1(cursor.getInt(1));
                node.setUser_2(cursor.getInt(2));
                node.setDistance(cursor.getDouble(3));
                li.add(node);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return li;
    }
}
