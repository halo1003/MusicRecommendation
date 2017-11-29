package com.example.dotoan.musicrecommendation.ThreadU;

import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.SQLite.DBThreadU;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance.context;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.objTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.objTBi;
import static com.example.dotoan.musicrecommendation.ThreadU.ObjThread.lockTrans;

/**
 * Created by DOTOAN on 11/29/2017.
 */

public class SubThread extends Thread {
    int clus;
    private static String threadName = "ThreadU.SubThread.";

    public SubThread(int clus) {
        this.clus = clus;
    }

    @Override
    public synchronized void start() {
        super.start();
        Log.e(threadName,"start()");
    }

    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    public void run() {
        super.run();
        DBThreadU dbThreadU = new DBThreadU(context);
        List<Item> ListItem = dbThreadU.getAllNode(objTBi);
        for (Item i : ListItem){
            String mid = i.getMid();
            double order = Double.parseDouble(i.getOrder());
            databaseReference.child("musics").orderByChild("mid").equalTo(mid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                            MusicC musicC = singleSnap.getValue(MusicC.class);
                            int pos = musicC.get_id();
                            dbThreadU.addNode_forObj(objTB,clus,pos+"",order+"");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
