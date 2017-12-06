package com.example.dotoan.musicrecommendation.IntentService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.GroupUserC;
import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.SQLite.DBThreadU;
import com.example.dotoan.musicrecommendation.ThreadU.CenThread;
import com.example.dotoan.musicrecommendation.ThreadU.ObjThread;
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

import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.cenTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.simTB;
import static com.example.dotoan.musicrecommendation.ThreadU.CenThread.Threadname;


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
    int temp = 0;
    double x_sim;
    List<GroupUserC> distance = new ArrayList<GroupUserC>();

    static final public String RECOM_RESULT = "com.REQUEST_PROCESSED_DISTANCE";
    static final public String RECOM_MESSAGE_LIST = "com.MSG.LIST";
    static final public String RECOM_MESSAGE_ARRAY_INT = "com.MSG.ARRAY.INT";
    static final public String RECOM_MESSAGE_ARRAY_DOUBLE = "com.MSG.ARRAY.DOUBLE";
    static final public String RECOM_MESSAGE_STRING = "com.MSG.STRING";

    public static boolean done = false;
    public static Object lockObj = new Object();
    public static String Iservice = "IntentServiceDis";
    public static Context context;

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
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        context = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(Iservice,"start()");
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

        Log.e("id",id+"");

        CenThread cenThread = new CenThread(id);
        cenThread.start();
        try {
            cenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DBThreadU dbThreadU = new DBThreadU(context);
        Log.e(Threadname+" cenTB",dbThreadU.getNodesCount(cenTB)+"");

        ObjThread objThread = new ObjThread(id);
        objThread.start();
        try {
            objThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e("The number of Simmer",dbThreadU.getNodesCount(simTB)+"");
        List<Item> sim = dbThreadU.getAllNode_forSim(simTB);
        int pos = GetSmallestDistance(sim);
        pos--;
//        Log.e("GET",sim.get(pos).getId()+"");
        sendResultString(sim.get(pos).getId()+"");
    }

    public double[] toDoubleArray(List<Double> list) {
        double[] ret = new double[ list.size() ];
        int i = 0;
        for(Iterator<Double> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    public int GetSmallestDistance(List<Item> li){
        double smallest = li.get(0).getDis();
        int pos = 0;
        if (smallest == 0) {
            smallest = li.get(1).getDis();
            pos = 1;
        }
        //int pos = li.get(0).getId();
        int j =0;
        for (Item i: li){
            if(i.getDis()!= 0 && i.getDis() < smallest){
                smallest = i.getDis();
                pos = j;
            }
            j++;
        }
        return pos;
    }

//    class ThreadB extends Thread {
//        int total;
//
//        @Override
//        public void run() {
//            for (int i = 0; i < 100; i++) {
//                total += i;
//            }
//            synchronized(ThreadA.latch) {
//                ThreadA.done = true;
//                ThreadA.latch.notify();
//            }
//        }
//    }
}
