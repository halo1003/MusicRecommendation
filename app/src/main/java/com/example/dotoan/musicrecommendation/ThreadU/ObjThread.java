package com.example.dotoan.musicrecommendation.ThreadU;

import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.GroupUserC;
import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.SQLite.DBThreadU;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance.context;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.cenTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.objTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.objTBi;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.simTB;

/**
 * Created by DOTOAN on 11/29/2017.
 */

public class ObjThread extends Thread {
    int id;
    private static String Threadname = "ThreadU.ObjThread.";
    public int current;

    public ObjThread(int id) {
        this.id = id;
    }

    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    static Object lockObject = new Object();
    public static Object lockTrans = new Object();
    static Object lockObj = new Object();

    @Override
    public void run() {
        super.run();
        List<String> centroids = new ArrayList<String>();
        List<String> keys = new ArrayList<String>();
        List<Integer> cluster = new ArrayList<Integer>();

        databaseReference.child("Relative Group").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                    String keyid = singleSnap.getKey().toString();
                    centroids.add(keyid);
                }
                notifyObject();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized (lockObject){
            try {
                //Log.e("lock","lock");
                lockObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.e(Threadname+"cen",centroids+"");

        for (String centroid: centroids){
            DatabaseReference db = databaseReference.child("Relative Group").child(centroid).child("Array").child("Value");
            Query query = db.orderByChild("user").equalTo(id).limitToFirst(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Log.e(Threadname,dataSnapshot+"");
                        current = Integer.parseInt(centroid);
                    }
                    if (centroid == centroids.get(centroids.size()-1)) notifyObject();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        synchronized (lockObject){
            try {
                //Log.e("lock","lock");
                lockObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.e(Threadname+"current",current+"");

        DatabaseReference db = databaseReference.child("Relative Group").child(String.valueOf(current)).child("Array").child("Value");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int c = (int) dataSnapshot.getChildrenCount();
                int i=0;
                for (DataSnapshot sinSnap : dataSnapshot.getChildren()) {
                    i++;
                    GroupUserC groupUserC = sinSnap.getValue(GroupUserC.class);
                    cluster.add(groupUserC.getUser());
                    if (i == c) notifyObject();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized (lockObject){
            try {
                //Log.e("lock","lock");
                lockObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Log.e(Threadname+"cluster",cluster+"");
        DBThreadU dbThreadU = new DBThreadU(context);
        dbThreadU.DropTB(simTB);
        dbThreadU.CreateTB_forSim(simTB);

        for (int clu: cluster){
            dbThreadU.DropTB(objTBi);
            dbThreadU.CreateTB_forObj(objTBi);

            dbThreadU.DropTB(objTB);
            dbThreadU.CreateTB_forObj(objTB);

            db = databaseReference.child("udata").child(String.valueOf(clu)).child("listen");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleDatasnap: dataSnapshot.getChildren()){
                        for(DataSnapshot sin : singleDatasnap.getChildren()) {
                            String sinKey = sin.getKey().toString();
                            String sinValue = sin.getValue().toString();
                            dbThreadU.addNode_forObj(objTBi, clu,sinKey,sinValue);
                        }
                    }

                    SubThread subThread = new SubThread(clu);
                    subThread.start();
                    try {
                        subThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    Log.e("--","---------------------------------------------");
//                    Log.e(Threadname+objTBi,dbThreadU.getNodesCount(objTBi)+"");
//                    Log.e(Threadname+objTB,dbThreadU.getNodesCount(objTB)+"");
//                    Log.e("--","---------------------------------------------");

                    SimilarityThread similarityThread = new SimilarityThread(clu);
                    similarityThread.start();
                    try {
                        similarityThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (lockObj){
                        //Log.e("lock---","unlock");
                        lockObj.notify();
                    }

                    //if (clu == cluster.get(cluster.size()-1)) notifyObject();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            synchronized (lockObj){
                try {
                    //Log.e("lock---","lock");
                    lockObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

//        synchronized (lockObject){
//            try {
//                Log.e("lock","lock");
//                lockObject.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        Log.e(Threadname+objTBi,dbThreadU.getNodesCount(objTBi)+"");
        // End.function
    }

    private void notifyObject(){
        synchronized (lockObject){
            //Log.e("lock","unlock");
            lockObject.notify();
        }
    }
}
