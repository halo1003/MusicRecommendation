package com.example.dotoan.musicrecommendation.ThreadU;

import android.content.Context;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance;
import com.example.dotoan.musicrecommendation.SQLite.DBThreadU;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance.context;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.cenTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.cenTBi;

/**
 * Created by DOTOAN on 11/29/2017.
 */

public class CenThread extends Thread {
    int id;
    public static String Threadname = "ThreadU.CenThread";
    final ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String,String>>();
    public Object lockObj = new Object();

    public CenThread(int id) {
        this.id = id;
    }

    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public synchronized void start() {
        super.start();
        Log.e(Threadname,"start()");
    }

    @Override
    public void destroy() {
        super.destroy();
        Log.e(Threadname,"destroy()");
    }



    @Override
    public void run() {
        super.run();
        DBThreadU dbThreadU = new DBThreadU(context);
        dbThreadU.DropTB(cenTBi);
        dbThreadU.CreateTB(cenTBi);

        databaseReference.child("udata").child(String.valueOf(id)).child("listen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnap: dataSnapshot.getChildren()){
                    HashMap<String,String> hm = new HashMap<String,String>();
                    for (DataSnapshot sin: singleSnap.getChildren()){
                        hm.put(sin.getKey().toString(),sin.getValue().toString());
                        dbThreadU.addNode(cenTBi,sin.getKey().toString(),sin.getValue().toString());
                        arrayList.add(hm);
                    }
                }
                objectNotify();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized (lockObj){
            try {
                lockObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        dbThreadU.DropTB(cenTB);
        dbThreadU.CreateTB(cenTB);

        List<Item> list_item = dbThreadU.getAllNode(cenTBi);

        for (Item i: list_item) {
            String mid = i.getMid();
            double order = Double.parseDouble(i.getOrder());
            databaseReference.child("musics").orderByChild("mid").equalTo(mid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                            MusicC musicC = singleSnap.getValue(MusicC.class);
                            int pos = musicC.get_id();
                            dbThreadU.addNode(cenTB,pos+"",order+"");
                        }
                    }
                    if (i == list_item.get(list_item.size()-1)) objectNotify();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        synchronized (lockObj){
            try {
                lockObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //End.function
    }

    public void objectNotify(){
        synchronized (lockObj){
            lockObj.notify();
        }
    }
}
