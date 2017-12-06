package com.example.dotoan.musicrecommendation.IntentService;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.SQLite.DBShow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ShowMIntentService extends IntentService {
    ArrayList<MusicC> li = new ArrayList<MusicC>();
    DBShow dbShow ;
    int count = 0;


    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

    static public String RECOM_RESULT = "com.REQUEST_LISTVIEW";
    static public String RECOM_MESSAGE = "com.MSG.LIST";
    static public String message = "com.MSG.DONE";

    public void sendResult() {
        Intent intent = new Intent(RECOM_RESULT);
        if(message != null) {
            intent.putExtra(RECOM_MESSAGE, message);
            broadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        dbShow = new DBShow(getApplicationContext());

        dbShow.DropTB();
        dbShow.CreateTB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ShowMIntentService() {
        super("ShowMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String user = intent.getStringExtra("user");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("udata").child(user).child("listen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int c = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot sinSnap: dataSnapshot.getChildren()){
                    count++;
                    int fiKey = Integer.parseInt(sinSnap.getKey().toString());
                    for (DataSnapshot sin: sinSnap.getChildren()){
                        String seKey = sin.getKey().toString();
                        Log.e("seKey",seKey+" "+count);
                        Query query = databaseReference.child("musics").orderByChild("mid").equalTo(seKey).limitToFirst(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot single : dataSnapshot.getChildren()) {
                                        MusicC m = single.getValue(MusicC.class);
//                                        li.add(m);
                                        //Log.e("T",m.getMid());
                                        dbShow.addNode(m);
                                    }
                                    if (count == c) sendResult();
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

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
