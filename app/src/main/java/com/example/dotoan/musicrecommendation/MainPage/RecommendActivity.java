package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance;
import com.example.dotoan.musicrecommendation.IntentService.ShowMIntentService;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.RecycleView.MusicsAdapter;
import com.example.dotoan.musicrecommendation.SQLite.DBShow;
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

import static com.example.dotoan.musicrecommendation.IntentService.ShowMIntentService.RECOM_MESSAGE;
import static java.util.stream.Collectors.toList;

public class RecommendActivity extends Activity {

    @BindView(R.id.rdMusics) RecyclerView recyclerView;
    @BindView(R.id.mid_txtv) TextView txtv_mid;
    @BindView(R.id.art_txtv) TextView txtv_id;
    @BindView(R.id.name_txtv) TextView txttv_name;

    Object lockObj = new Object();
    BroadcastReceiver broadcastReceiver;
    List<MusicC> li = new ArrayList<MusicC>();

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver((broadcastReceiver),
                new IntentFilter(ShowMIntentService.RECOM_RESULT)
        );
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        ButterKnife.bind(this);

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String mess = intent.getStringExtra(RECOM_MESSAGE);
                DBShow dbShow = new DBShow(getApplicationContext());
                if (mess!=null){
                    li = dbShow.getAllNode(DBShow.TBname);
                    Log.e("LI",li+"");
                    MusicsAdapter adapter = new MusicsAdapter(li, getApplicationContext());
                    adapter.notifyDataSetChanged();

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            }
        };

        Bundle bundle = getIntent().getExtras();
        String musicId = bundle.getString("artic");
        String music_mid = bundle.getString("mid");
        String user = bundle.getString("user");
        String name = bundle.getString("name");

        txtv_mid.setText(music_mid);
        txtv_id.setText(musicId);
        txttv_name.setText(name);

        Intent i = new Intent(getApplicationContext(), ShowMIntentService.class);
        i.putExtra("user",user);
        startService(i);
    }

    public void ObjlockNotify() {
        synchronized (lockObj) {
            Log.e("l","unlock");
            lockObj.notify();
        }
    }
}
