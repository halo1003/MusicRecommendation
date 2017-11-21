package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.Contruct.DistanceC;
import com.example.dotoan.musicrecommendation.Contruct.FilterC;
import com.example.dotoan.musicrecommendation.Contruct.ListC;
import com.example.dotoan.musicrecommendation.Contruct.Node;
import com.example.dotoan.musicrecommendation.Contruct.ValueC;
import com.example.dotoan.musicrecommendation.IntentService.IntentServiceClusterData;
import com.example.dotoan.musicrecommendation.MainActivity;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.SQLite.DBManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.android.device.DeviceName;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by DOTOAN on 11/18/2017.
 */

public class AdminFragment extends Fragment {
    View view;
    int n = 25;
    int nMusic = 13369;
    int nUser = 1259;

    @BindView(R.id.btn_data_exist) Button btn_fix;
    @BindView(R.id.stt_data_exist) TextView txtv_stt_data;
    @BindView(R.id.stt_cluster) TextView txtv_stt_cluster;
    @BindView(R.id.btn_cluster) Button btn_cluster;
    @BindView(R.id.stt_temp) TextView txtv_temp;

    private Unbinder unbinder;
    BroadcastReceiver broadcastReceiver;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(IntentServiceClusterData.RECOM_RESULT)
        );
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.navigate_frame_admin,container,false);
        unbinder = ButterKnife.bind(this, view);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s_string = intent.getStringExtra(IntentServiceClusterData.RECOM_MESSAGE_STRING);
                String s_nume = intent.getStringExtra(IntentServiceClusterData.RECOM_MESSAGE_NUME);
                if (s_string!=null) txtv_stt_cluster.setText(s_string);
                if (s_nume!=null) txtv_temp.setText(s_nume);

            }
        };

        final DBManager dbManager = new DBManager(getActivity());

        btn_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.DropTB();
                txtv_stt_data.setText("Drop TB success");

                dbManager.CreateTB();
                txtv_stt_data.setText("Create TB success");

                databaseReference.child("habitatMatrix").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot zone: dataSnapshot.getChildren()){
                            int current_user = Integer.parseInt(zone.getKey().toString());
                            int c = (int) zone.getChildrenCount();
                            int i = 0;
                            int cenPos[] = new int[c];
                            double cenVal[] = new double[c];
                            for (DataSnapshot zoneDetail: zone.getChildren()){
                                cenPos[i] = Integer.parseInt(zoneDetail.getKey().toString());
                                cenVal[i] = Double.parseDouble(zoneDetail.getValue().toString());
                            }

                            DistanceC distanceC= new DistanceC(current_user,cenPos,cenVal);
                            new DistanceComputeAsyn().execute(distanceC);
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });



        btn_cluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_ser = new Intent(getActivity(),IntentServiceClusterData.class);
                getActivity().startService(i_ser);
            }
        });


        return view;
    }

    public class DistanceComputeAsyn extends AsyncTask<DistanceC,Integer,String> {

        @Override
        protected void onPreExecute() {
            Log.e("Async","start");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Async","Done");
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(DistanceC... params) {
            final int current_user = params[0].getCurrent_user();
            final int cenPos[] = params[0].getCenPos();
            final double cenVal[] = params[0].getCenVal();

            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.addListenerForSingleValueEvent(new ValueEventListener() {

                int objPos[];
                double objVal[];

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zone : dataSnapshot.child("habitatMatrixv2").getChildren()) {
                        int user_2 = Integer.parseInt(zone.getKey().toString());
                        int size1 = (int) zone.getChildrenCount();
                        objPos = new int[size1];
                        objVal = new double[size1];
                        int j = 0;
                        for (DataSnapshot zoneDetail : zone.getChildren()) {
                            objPos[j] = Integer.parseInt(zoneDetail.getKey().toString());
                            objVal[j++] = Double.parseDouble(zoneDetail.getValue().toString());
                        }

                        double sim = similarityDistance(cenVal, cenPos, objVal, objPos);

                        Node node = new Node(current_user, user_2, sim);
                        DBManager db = new DBManager(getActivity(), node);
                        db.addNode(node);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Error", databaseError + "");
                }
            });
            return null;
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
    }
}
