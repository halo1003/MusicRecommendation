package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.RecycleView.MusicsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendActivity extends Activity {

    @BindView(R.id.rdMusics) RecyclerView recyclerView;
    @BindView(R.id.mid_txtv) TextView txtv_mid;
    @BindView(R.id.id_txtv) TextView txtv_id;

    Object lockObj = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String musicId = bundle.getString("pos");
        String music_mid = bundle.getString("mid");
        String user = bundle.getString("user");

        List<MusicC> li = new ArrayList<MusicC>();

        txtv_mid.setText(music_mid);
        txtv_id.setText(musicId);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("udata").child(user).child("listen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot sinSnap: dataSnapshot.getChildren()){
                    int fiKey = Integer.parseInt(sinSnap.getKey().toString());
                    for (DataSnapshot sin: sinSnap.getChildren()){
                        String seKey = sin.getKey().toString();
                        Query query = databaseReference.child("musics").orderByChild("mid").equalTo(seKey).limitToFirst(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot single : dataSnapshot.getChildren()) {
                                        MusicC m = single.getValue(MusicC.class);
                                        li.add(m);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                ObjlockNotify();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized (lockObj){
            try {
                Log.e("l","lock");
                lockObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        MusicsAdapter adapter = new MusicsAdapter(li, getApplicationContext());
        adapter.notifyDataSetChanged();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void ObjlockNotify() {
        synchronized (lockObj) {
            Log.e("l","unlock");
            lockObj.notify();
        }
    }
}
