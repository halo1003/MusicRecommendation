package com.example.dotoan.musicrecommendation.IntentService;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.GroupUserC;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class IntentServiceDistance extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * Used to name the worker thread, important only for debugging.
     */

    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    int n = 25;
    int nMusic = 13369;
    int nUser = 1259;

    static final public String RECOM_RESULT = "com.REQUEST_PROCESSED_DISTANCE";
    static final public String RECOM_MESSAGE_LIST = "com.MSG.LIST";
    static final public String RECOM_MESSAGE_ARRAY_INT = "com.MSG.ARRAY.INT";
    static final public String RECOM_MESSAGE_ARRAY_DOUBLE = "com.MSG.ARRAY.DOUBLE";
    static final public String RECOM_MESSAGE_STRING = "com.MSG.STRING";

    private Object lockObj = new Object();
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public IntentServiceDistance() {
        super("IntentServiceDistance");
    }

    public void sendResultString(String message) {
        Intent intent = new Intent(RECOM_RESULT);
        if(message != null) {
            intent.putExtra(RECOM_MESSAGE_STRING, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        sendResultString("onStart()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        SharedPreferences sp1= getSharedPreferences("Login", MODE_PRIVATE);
        String _id = sp1.getString("_id", null);
        final String ids = sp1.getString("id",null);
        final int id = Integer.parseInt(ids);

//        Gson gson = new Gson();
//        String arrayListString = sp1.getString("list", null);
//        Type type = new TypeToken<ArrayList<HashMap<String,String>>>() {}.getType();

        final ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String,String>>();

        databaseReference.child("udata").child(String.valueOf(id)).child("listen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnap: dataSnapshot.getChildren()){
                    HashMap<String,String> hm = new HashMap<String,String>();
                    for (DataSnapshot sin: singleSnap.getChildren()){
                        hm.put(sin.getKey().toString(),sin.getValue().toString());
                        arrayList.add(hm);
                    }
                }
                Log.e("addlist","Done");
                ObjlockNotify();
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

        Log.e("ArrayList_size()",arrayList.size()+"");

        final ArrayList<Integer> cenPos = new ArrayList<Integer>();
        final ArrayList<Double> cenVal = new ArrayList<Double>();

        for (HashMap s: arrayList){
            for (Object o:s.keySet()){
                String mid = o.toString();
                double order = Double.parseDouble(s.get(o).toString());

                databaseReference.child("musics").orderByChild("mid").equalTo(mid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for(DataSnapshot singleSnap: dataSnapshot.getChildren()){
                                MusicC musicC = singleSnap.getValue(MusicC.class);
                                int pos =  musicC.get_id();
                                cenPos.add(pos);
                                cenVal.add(order);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO:
               //////////////////////////////////////
                List<String> keys = new ArrayList<String>();

                final ArrayList<Integer> cluster = new ArrayList<Integer>();
                databaseReference.child("Relative Group").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot singleSnap: dataSnapshot.getChildren()){
                            String keyid = singleSnap.getKey().toString();

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Relative Group/" + keyid);
                            Query query = db.child("Array").child("Value").orderByChild("user").equalTo(id).limitToFirst(1);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot single : singleSnap.child("Array").child("Value").getChildren()) {
                                            GroupUserC groupUserC = single.getValue(GroupUserC.class);
                                            cluster.add(groupUserC.getUser());
                                        }
                                        Log.e("cluster", cluster+"");

                                        List<GroupUserC> distance = new ArrayList<GroupUserC>();

                                        for (final int i : cluster) {
                                            final int[] j = {0};
                                            final double[] x_sim = {-1};
                                            databaseReference.child("udata").child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    GenericTypeIndicator<List<HashMap<String, String>>> t = new GenericTypeIndicator<List<HashMap<String, String>>>() {
                                                    };
                                                    final List<HashMap<String, String>> hash_list = dataSnapshot.child("listen").getValue(t);

                                                    final List<Integer> objPos = new ArrayList<Integer>();

                                                    final List<Double> objVal = new ArrayList<Double>();

                                                    final int[] temp = {0};
                                                    int te = 0;
                                                    for (HashMap singleHash : hash_list) {
                                                        te++;
                                                        Log.e("OKe", te +" "+hash_list.size());

                                                        for (Object mid_obj : singleHash.keySet()) {
                                                            final String mid = mid_obj.toString();
                                                            final double order = Double.parseDouble(singleHash.get(mid_obj).toString());

                                                            int finalTe = te;
                                                            databaseReference.child("musics").orderByChild("mid").equalTo(mid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        temp[0]++;
                                                                        for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                                                                            MusicC musicC = singleSnap.getValue(MusicC.class);
                                                                            int pos = musicC.get_id();
                                                                            objPos.add(pos);
                                                                            objVal.add(order);

                                                                            if (temp[0] == hash_list.size()) {
                                                                                x_sim[0] = similarityDistance(toDoubleArray(cenVal), toIntArray(cenPos), toDoubleArray(objVal), toIntArray(objPos));
                                                                                GroupUserC groupUserC = new GroupUserC();
                                                                                groupUserC.setDistance(x_sim[0]);
                                                                                groupUserC.setUser(i);

                                                                                distance.add(groupUserC);

                                                                                Log.e("TAG1", i+" "+cluster.get(cluster.size() - 1));
                                                                                Log.e("TAG1", finalTe +" "+hash_list.size());

                                                                                if (singleHash == hash_list.get(hash_list.size() - 1) && i == cluster.get(cluster.size() - 1)) {
                                                                                    Log.e("SEND1","OK");
                                                                                    sendResultString(GetSmallestDistance(distance) + "");
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        temp[0]++;
                                                                        if (temp[0] == hash_list.size()) {
                                                                            x_sim[0] = similarityDistance(toDoubleArray(cenVal), toIntArray(cenPos), toDoubleArray(objVal), toIntArray(objPos));
                                                                            GroupUserC groupUserC = new GroupUserC();
                                                                            groupUserC.setDistance(x_sim[0]);
                                                                            groupUserC.setUser(i);

                                                                            distance.add(groupUserC);

                                                                        }

                                                                        Log.e("TAG2", i+" "+cluster.get(cluster.size() - 1));
                                                                        Log.e("TAG2", finalTe +" "+hash_list.size());

                                                                        if (singleHash == hash_list.get(hash_list.size() - 1) && i == cluster.get(cluster.size() - 1)) {
                                                                            Log.e("SEND2","OK");
                                                                            sendResultString(GetSmallestDistance(distance) + "");
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

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                for (String keyid: keys) {

                }
            ////////////////////////////////////////////
            }
        }, 3000);
    }

    public void ObjlockNotify() {
        synchronized (lockObj) {
            lockObj.notify();
        }
    }

    public int[] toIntArray(List<Integer> list) {
        int[] ret = new int[ list.size() ];
        int i = 0;
        for(Iterator<Integer> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    public double[] toDoubleArray(List<Double> list) {
        double[] ret = new double[ list.size() ];
        int i = 0;
        for(Iterator<Double> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    public int GetSmallestDistance(List<GroupUserC> li){
        double smallest = li.get(1).getDistance();
        int pos = li.get(1).getUser();
        for (GroupUserC groupUserC: li){
            if(groupUserC.getDistance()!= 0 && groupUserC.getDistance() < smallest){
                smallest = groupUserC.getDistance();
                pos = groupUserC.getUser();
            }
        }
        return pos;
    }

    private synchronized double similarityDistance(double centVal[], int centPos[], double objVal[], int objPos[]) {
        double tag = 0.0, cenX = 0.0, cenY = 0.0, sol = 0.0;

        if (centPos.length >= objPos.length) {
            for (int i = 0; i < centPos.length; i++) {
                if (i < objPos.length) {
                    for (int j = 0; j < objPos.length; j++) {
                        if (centPos[i] == objPos[j]) {
                            tag = tag + (centVal[i] - objVal[j]) * (centVal[i] - objVal[j]);
                            break;
                        }

                        if (j == objPos.length - 1) {
                            cenX = cenX + centVal[i] * centVal[i];
                            cenY = cenY + objVal[i] * objVal[i];
                        }
                    }
                } else {
                    cenX = cenX + centVal[i] * centVal[i];
                }
                sol = tag + cenX + cenY;
            }
        } else {
            for (int i = 0; i < objPos.length; i++) {
                if (i < centPos.length) {
                    for (int j = 0; j < centPos.length; j++) {
                        if (centPos[j] == objPos[i]) {
                            tag = tag + (centVal[j] - objVal[i]) * (centVal[j] - objVal[i]);

                            break;
                        }

                        if (j == centPos.length - 1) {
                            cenX = cenX + centVal[i] * centVal[i];
                            cenY = cenY + objVal[i] * objVal[i];
                        }
                    }
                } else {
                    cenY = cenY + objVal[i] * objVal[i];
                }
                sol = tag + cenX + cenY;
            }
        }

        double s = Math.sqrt(sol);
        return (double) Math.round(s * 10000) / 10000;
    }
}
