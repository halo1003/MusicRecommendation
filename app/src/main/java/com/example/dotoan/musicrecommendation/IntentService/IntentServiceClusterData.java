package com.example.dotoan.musicrecommendation.IntentService;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.FilterC;
import com.example.dotoan.musicrecommendation.Contruct.ListC;
import com.example.dotoan.musicrecommendation.Contruct.Node;
import com.example.dotoan.musicrecommendation.Contruct.ValueC;
import com.example.dotoan.musicrecommendation.SQLite.DBManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

public class IntentServiceClusterData extends IntentService{
    int n = 25;
    int nMusic = 13369;
    int nUser = 1259;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

    static final public String RECOM_RESULT = "com.REQUEST_PROCESSED";
    static final public String RECOM_MESSAGE_STRING = "com.MSG.STRING";
    static final public String RECOM_MESSAGE_NUME = "com.MSG.NUME";

    public IntentServiceClusterData() {
        super("IntentServiceClusterData");
    }

    public void sendResultString(String message) {
        Intent intent = new Intent(RECOM_RESULT);
        if(message != null) {
            intent.putExtra(RECOM_MESSAGE_STRING, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    public void sendResultNume(String message) {
        Intent intent = new Intent(RECOM_RESULT);
        if(message != null) {
            intent.putExtra(RECOM_MESSAGE_NUME, message);
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

        List<Double> distance_compare = new ArrayList<Double>();

        DBManager db = new DBManager(getApplicationContext());

        db.copyTable();
        Log.e("minmaxCount", String.valueOf(db.getNodesCount("min_max_temp")));

        sendResultNume(String.valueOf(db.getNodesCount("min_max_temp")));

        for (Node i:db.getAllNode("min_max_temp")){
            double Dis = i.getDistance();
            distance_compare.add(Dis);
        }

        double last = Gmax(distance_compare);
        double initial_step = Gmin(distance_compare);

        int element_require = (int) (nUser/Math.sqrt(nUser/2));
        List<ListC>  group = new ArrayList<ListC>();

        double Radius = initial_step;
        double step = initial_step;
        int count = 0;
        while (db.getNodesCount("min_max_temp")!=0 || Radius<=last){
            Log.e("Radius", String.valueOf(Radius)+" - "+db.getNodesCount("min_max_temp"));

            sendResultNume(String.valueOf(Radius)+" - "+db.getNodesCount("min_max_temp"));

            int user1 = 0;
            List<FilterC> filter = new ArrayList<FilterC>();
            while(user1<1253){
//                Log.d("user1", String.valueOf(user1));
                List<ValueC> user2_per1 = new ArrayList<ValueC>();
                for (Node i : db.Querry(user1)){
                    if (i.getDistance()<=Radius){
                        ValueC valueC = new ValueC();
                        valueC.setUser(i.getUser_2());
                        valueC.setDistance(i.getDistance());
                        user2_per1.add(valueC);
                    }
                }
                FilterC filterC = new FilterC();
                filterC.setUser1(user1);
                filterC.setSize(user2_per1.size());
                filterC.setUser2_per1(user2_per1);

                filter.add(filterC);
                user1++;
            }

            Node objUser = maxL(filter);
            int maxUser = objUser.getMax();
            int user1_Discussion = objUser.getUser_1();
            List<ValueC> arr = objUser.getArrayList();

            if (maxUser < element_require) {
                Radius = Radius + step;
                Log.e("Status","Not found, Increase Radius to "+Radius);
                sendResultString("Not found, Increase Radius to "+Radius);
            }
            else{
                ListC listC = new ListC();
                listC.setUser1(user1_Discussion);
                listC.setRadius(Radius);
                listC.setUser2(arr);

                group.add(listC);
                for (ValueC i: arr){
                    db.Delete_user2(i.getUser());
                }
                Log.e("Status","Group found, User centroid is "+user1_Discussion);
                sendResultString("Group found, User centroid is "+user1_Discussion);
            }
        }

        databaseReference.child("Relative Group").removeValue();
        List<Node> cquerry  = db.cQuerry();
        int number = 0;
        for (Node i: cquerry){
            databaseReference.child("Relative Group").child("Unknown").child("Array").child("Size").setValue(cquerry.size());
            databaseReference.child("Relative Group").child("Unknown").child("Array").child("Value").child(String.valueOf(number++)).setValue(i.getUser_1());
            databaseReference.child("Relative Group").child("Unknown").child("Radius").setValue(Radius);
        }

        for (ListC i: group){
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Radius").setValue(i.getRadius());
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Array").child("Size").setValue(i.getUser2().size());
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Array").child("Value").setValue(i.getUser2());
        }
    }

    private Node maxL(List<FilterC> li){
        int max = 0;
        int user1 = 0;
        List<ValueC> user2_per1 = new ArrayList<ValueC>();

        for (int i =0;i<li.size();i++){
            if (li.get(i).getSize()>max){
                max = li.get(i).getSize();
                user1 = li.get(i).getUser1();
                user2_per1 = li.get(i).getUser2_per1();
            }
        }

        Node node = new Node();
        node.setUser_1(user1);
        node.setMax(max);
        node.setArrayList(user2_per1);

        return node;
    }

    private double Gmin(List<Double> l){
        double min = 0;
        for (double i:l){
            if (i!=0){
                min = i;
                break;
            }
        }

        for(double i: l){
            if (i<min && i!=0){
                min = i;
            }
        }
        return min;
    }

    private double Gmax(List<Double> l){
        double max = 0;
        for (double i:l){
            if (i!=0){
                max = i;
                break;
            }
        }

        for(double i: l){
            if (i>max && i!=0){
                max = i;
            }
        }
        return max;
    }
}
