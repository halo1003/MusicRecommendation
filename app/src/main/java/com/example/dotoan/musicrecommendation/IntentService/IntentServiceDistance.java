package com.example.dotoan.musicrecommendation.IntentService;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
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
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private final Object lockObj2 = new Object();

    public static Bus bus;

    public List<Double> double_list = new ArrayList<Double>();
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public IntentServiceDistance() {
        super("IntentServiceDistance");
    }

    public void sendList(ArrayList<Integer> message){
        Intent intent = new Intent(RECOM_RESULT);
        if(message != null) {
            intent.putIntegerArrayListExtra(RECOM_MESSAGE_LIST, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    public void sendArrayInt(ArrayList<Integer> message){
        Intent intent = new Intent(RECOM_RESULT);
        if(message.size()!=0) {
            intent.putIntegerArrayListExtra(RECOM_MESSAGE_ARRAY_INT, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    public void sendArrayDouble(ArrayList<Double> message){
        Intent intent = new Intent(RECOM_RESULT);
        if(message.size()!=0) {
            intent.putExtra(RECOM_MESSAGE_ARRAY_DOUBLE, message);
            broadcastManager.sendBroadcast(intent);
        }
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
        sendResultString("onDestroy()");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        SharedPreferences sp1= getSharedPreferences("Login", MODE_PRIVATE);
        String _id = sp1.getString("_id", null);
        final String ids = sp1.getString("id",null);
        final int id = Integer.parseInt(ids);

        Gson gson = new Gson();
        String arrayListString = sp1.getString("list", null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>() {}.getType();
        final ArrayList<HashMap<String,String>> arrayList = gson.fromJson(arrayListString, type);

        final ArrayList<Integer> cenPos = new ArrayList<Integer>();
        final int _cenPos[] = new int[arrayList.size()];

        final ArrayList<Double> cenVal = new ArrayList<Double>();
        final double _cenVal[] = new double[arrayList.size()];

        int i =0;
        int temp = 0;
        for (HashMap s: arrayList){
            temp++;
            for (Object o:s.keySet()){
                String mid = o.toString();
                double order = Double.parseDouble(s.get(o).toString());
                int pos = _Query(mid);

                if (pos!= -1){
                    cenPos.add(pos);
                    cenVal.add(order);
                    _cenPos[i] = pos;
                    _cenVal[i] = order;

                    sendResultString("MusicId "+pos+": "+order);

                    i++;
                    if (temp == arrayList.size()) {
                        sendArrayDouble(cenVal);
                        sendArrayInt(cenPos);
                    }
                }else {
                    if (temp == arrayList.size()) {
                        sendArrayDouble(cenVal);
                        sendArrayInt(cenPos);
                    }
                }
            }
        }
        final ArrayList<Integer> cluster = new ArrayList<Integer>();
        databaseReference.child("Relative Group").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot singleSnap: dataSnapshot.getChildren()){
                    String keyid = singleSnap.getKey().toString();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Relative Group/"+keyid);
                    Query query = db.child("Array").child("Value").orderByChild("user").equalTo(id).limitToFirst(1);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int c = (int) singleSnap.child("Array").child("Value").getChildrenCount();
                                for(DataSnapshot single: singleSnap.child("Array").child("Value").getChildren()){
                                    GroupUserC groupUserC = single.getValue(GroupUserC.class);
                                    cluster.add(groupUserC.getUser());
                                    if (cluster.size()==c) sendList(cluster);
                                }

                                lockObj = new Object();

                                for (final int i: cluster){
                                    SimilaritySyn(_cenPos,_cenVal,i);
                                    break;
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
    }

    public double SimilaritySyn(final int cenPos[], final double cenVal[], int singleUser){

        Log.e("TAG","SimilaritySyn()"+singleUser);
        final int[] j = {0};
        final double[] x_sim = {-1};
            databaseReference.child("udata").child(String.valueOf(singleUser)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    GenericTypeIndicator<List<HashMap<String,String>>> t = new GenericTypeIndicator<List<HashMap<String,String>>>() {};
                    final List<HashMap<String,String>> hash_list = dataSnapshot.child("listen").getValue(t);
                    final int objPos[] = new int[hash_list.size()];
                    final double objVal[] = new double[hash_list.size()];
                    Log.e("besic",hash_list+"");
                    final int[] temp = {0};
                    for (HashMap singleHash: hash_list){
                        for (Object mid_obj : singleHash.keySet()){
                            final String mid = mid_obj.toString();
                            Log.e("mid",mid);
                            final double order = Double.parseDouble(singleHash.get(mid_obj).toString());

                            final int[] _id = {-1};
                            Log.e("_QUERY","x");
                            databaseReference.child("musics").orderByChild("mid").equalTo(mid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        temp[0]++;
                                        for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                                            MusicC musicC = singleSnap.getValue(MusicC.class);
                                            _id[0] = musicC.get_id();
                                            sendResultString(_id[0] + " Founded");
                                            objPos[j[0]] = _id[0];
                                            objVal[j[0]] = order;
                                            Log.e("TAG","TAGTEST");
                                            j[0]++;
                                        }

                                        if (temp[0] == hash_list.size()) ObjlockNotify(lockObj);
                                    }else {
                                        temp[0]++;
                                        sendResultString(mid + "Not Founded");
                                        if (temp[0] == hash_list.size()) ObjlockNotify(lockObj);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    x_sim[0] = similarityDistance(cenVal,cenPos,objVal,objPos);
                    Log.i("obj",objPos.length+" "+ objVal.length);
                    Log.i("cen",cenPos.length+" "+cenVal.length);
                    Log.e("x_sim", x_sim[0] +"");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return x_sim[0];
    }

    public void ObjlockNotify(Object lock) {
        synchronized (lock) {
            lock.notify();
        }
    }

    private double similarityDistance(double centVal[], int centPos[], double objVal[], int objPos[]) {
        double tag = 0, cenX = 0, cenY = 0, sol = 0;

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


    public int _Query(final String q){
        final int[] _id = {-1};
        Log.e("_QUERY","x");
        databaseReference.child("musics").orderByChild("mid").equalTo(q).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("_QUERY","xx");
                if (dataSnapshot.exists()){
                    for(DataSnapshot singleSnap: dataSnapshot.getChildren()){
                        MusicC musicC = singleSnap.getValue(MusicC.class);
                        _id[0] =  musicC.get_id();
                        sendResultString(_id[0]+" Founded");
                        if (_id[0] != -1) ObjlockNotify(lockObj);
                    }
                }else{
                    ObjlockNotify(lockObj);
                    sendResultString(q+" Not Founded");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e("_QUERY","xxx");
        synchronized (lockObj) {
            try{
                lockObj.wait(5000);
            } catch(InterruptedException e){
                //Handle Exception
            }
        }
        return _id[0];
    }
}
