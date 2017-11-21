package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.dotoan.musicrecommendation.Contruct.GroupUserC;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance;
import com.example.dotoan.musicrecommendation.InterfaceClass.OnGetDataListener;
import com.example.dotoan.musicrecommendation.LoginActivity;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.RecycleView.MusicsAdapter;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.firebase.database.ChildEventListener;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class NormalFragment extends Fragment {
    View view;

    @BindView(R.id.search_m) EditText edt_search;
    @BindView(R.id.search_b) Button btn_search;
    @BindView(R.id.rvMusics) RecyclerView lv_m;
    private Unbinder unbinder;
    BroadcastReceiver broadcastReceiver;

    private final Object lockObj = new Object();
    private final Object lockObj2 = new Object();

    ArrayList<Integer> _arr = new ArrayList<Integer>();
    Integer _cenPos[];
    Double _cenVal[];

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(IntentServiceDistance.RECOM_RESULT)
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
        view = inflater.inflate(R.layout.navigate_frame_normal,container,false);
        unbinder = ButterKnife.bind(this, view);

        final SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), Style.TYPE_PROGRESS_CIRCLE);
        superActivityToast.setIndeterminate(true);
        superActivityToast.setProgressIndeterminate(true);
        superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE));

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String s_string = intent.getStringExtra(IntentServiceDistance.RECOM_MESSAGE_STRING);
                if (s_string!= null && !s_string.equals("onDestroy()")) {
                    superActivityToast.dismiss();
                    superActivityToast.setText(s_string);
                    superActivityToast.show();
                }else if (s_string!=null && s_string.equals("onDestroy()")) superActivityToast.dismiss();

                _arr = intent.getIntegerArrayListExtra(IntentServiceDistance.RECOM_MESSAGE_LIST);
                if (_arr!=null) Log.e("TAG",_arr+"");

                ArrayList<Integer> cenPos = intent.getIntegerArrayListExtra(IntentServiceDistance.RECOM_MESSAGE_ARRAY_INT);
                if (cenPos!=null) {
                    Log.e("TAGcp",cenPos+"");
                    _cenPos = new Integer[cenPos.size()];
                    _cenPos = cenPos.toArray(_cenPos);
                }

                ArrayList <Double> cenVal = (ArrayList<Double>) intent.getSerializableExtra(IntentServiceDistance.RECOM_MESSAGE_ARRAY_DOUBLE);
                if (cenVal!=null) {
                    Log.e("TAGcv",cenVal+"");
                    _cenVal = new Double[cenVal.size()];
                    _cenVal = cenVal.toArray(_cenVal);
                }

//                if (_cenPos!= null && _cenVal!=null && _arr!=null){
//                    for (int i: _arr){
//                        SimilaritySyn(_cenPos,_cenVal,i);
//                    }
//                }
            }
        };

        SharedPreferences sp1=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        String _id = sp1.getString("_id", null);
        final String ids = sp1.getString("id",null);
        final int id = Integer.parseInt(ids);

        Gson gson = new Gson();
        String arrayListString = sp1.getString("list", null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>() {}.getType();
        ArrayList<HashMap<String,String>> arrayList = gson.fromJson(arrayListString, type);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_ser = new Intent(getActivity(), IntentServiceDistance.class);
                getActivity().startService(i_ser);
            }
        });


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            private Timer timer=new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(Editable s) {
                final SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), Style.TYPE_PROGRESS_CIRCLE);
                superActivityToast.setText("Searching...");
                superActivityToast.setIndeterminate(true);
                superActivityToast.setProgressIndeterminate(true);
                superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE));
                superActivityToast.show();

                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                // TODO: do what you need here (refresh list)
                                final List<MusicC> arrayOfMusics = new ArrayList<MusicC>();

                                String t = edt_search.getText().toString();
                                Query query = databaseReference.child("musics").orderByChild("mid").startAt(t).endAt(t+"\uf8ff");

                                readData(query, new OnGetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                                int id;
                                                String mid;
                                                MusicC musicC = singleSnapshot.getValue(MusicC.class);
                                                arrayOfMusics.add(musicC);
                                            }

                                            MusicsAdapter adapter = new MusicsAdapter(arrayOfMusics,getActivity());
                                            adapter.notifyDataSetChanged();

                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                            lv_m.setLayoutManager(layoutManager);
                                            lv_m.setAdapter(adapter);
                                            superActivityToast.dismiss();

                                            adapter.setOnItemClickedListener(new MusicsAdapter.OnItemClickedListener() {
                                                @Override
                                                public void onItemClick(MusicC musicC) {
                                                    Log.e("MUDIC",musicC.get_id()+" "+musicC.getMid());
                                                }
                                            });
                                        }else {
                                            superActivityToast.dismiss();

                                            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), Style.TYPE_STANDARD);
                                            superActivityToast.setText("No item found");
                                            superActivityToast.setDuration(500);
                                            superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED));
                                            superActivityToast.show();
                                        }
                                    }
                                    @Override
                                    public void onStart() {
                                        //when starting
                                        Log.d("ONSTART", "Started");
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.d("onFailure", "Failed");
                                    }
                                });
                            }
                        },
                        DELAY
                );
            }
        });

        return view;
    }

    public void readData(Query ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public double SimilaritySyn(final Integer cenPos[], final Double cenVal[], int singleUser){
        Log.e("TAG","SimilaritySyn()"+singleUser);
        final int[] j = {0};
        final double[] x_sim = {-1};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("udata").child(String.valueOf(singleUser)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<HashMap<String,String>>> t = new GenericTypeIndicator<List<HashMap<String,String>>>() {};
                List<HashMap<String,String>> hash_list = dataSnapshot.child("listen").getValue(t);
                final Integer objPos[] = new Integer [hash_list.size()];
                final Double objVal[] = new Double[hash_list.size()];
                Log.e("besic","dddd");
                int temp = 0;
                for (HashMap singleHash: hash_list){
                    temp++;
                    for (Object mid_obj : singleHash.keySet()){
                        String mid = mid_obj.toString();
                        double order = Double.parseDouble(singleHash.get(mid_obj).toString());
                        int pos = _Query(mid);
                        if (pos!=-1){
                            objPos[j[0]] = pos;
                            objVal[j[0]] = order;
                            Log.e("TAG","TAGTEST");
                            j[0]++;
                        }
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

    public void ObjlockNotify(Object lockObj) {
        synchronized (lockObj) {
            lockObj.notify();
        }
    }

    private double similarityDistance(Double centVal[], Integer centPos[], Double objVal[], Integer objPos[]) {
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


    public synchronized int _Query(final String q){
        final int[] _id = {-1};
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("musics").orderByChild("mid").equalTo(q).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot singleSnap: dataSnapshot.getChildren()){
                        MusicC musicC = singleSnap.getValue(MusicC.class);
                        _id[0] =  musicC.get_id();
                        if (_id[0] != -1) ObjlockNotify(lockObj);
                        break;
                    }
                }else{
                    ObjlockNotify(lockObj);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        synchronized (lockObj) {
            try{
                lockObj.wait();
            } catch(InterruptedException e){
                //Handle Exception
            }
        }
        return _id[0];
    }
}
