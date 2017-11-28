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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.dotoan.musicrecommendation.Contruct.Mdetail;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.Contruct.RPush;
import com.example.dotoan.musicrecommendation.Contruct.itemC;
import com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance;
import com.example.dotoan.musicrecommendation.InterfaceClass.OnGetDataListener;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.RecycleView.MusicsAdapter;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    @BindView(R.id.search_b) ActionProcessButton btn_search;
    @BindView(R.id.rvMusics) RecyclerView lv_m;

    private Unbinder unbinder;
    BroadcastReceiver broadcastReceiver;

    String po;
    String mi;

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(IntentServiceDistance.RECOM_RESULT)
        );

//        Intent i_ser = new Intent(getActivity(), IntentServiceDistance.class);
//        getActivity().startService(i_ser);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.navigate_frame_normal,container,false);
        unbinder = ButterKnife.bind(this, view);
        btn_search.setMode(ActionProcessButton.Mode.ENDLESS);

        final SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), Style.TYPE_PROGRESS_CIRCLE);
        superActivityToast.setIndeterminate(true);
        superActivityToast.setProgressIndeterminate(true);
        superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE));

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String s_string = intent.getStringExtra(IntentServiceDistance.RECOM_MESSAGE_STRING);
                if (s_string!= null && s_string.equals("onStart()")) {
                    superActivityToast.setText("Waiting for load data...");
                    superActivityToast.show();
                }else if (s_string!=null && isNumeric(s_string)) {
                    superActivityToast.dismiss();
                    Log.e("GET",s_string);
                    Intent i = new Intent(getActivity(), RecommendActivity.class);
                    i.putExtra("pos",po);
                    i.putExtra("mid",mi);
                    i.putExtra("user",s_string);
                    startActivity(i);
                }

            }
        };

        SharedPreferences sp1=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        String _id = sp1.getString("_id", null);
        final String ids = sp1.getString("id",null);
        //final int id = Integer.parseInt(ids);

        Gson gson = new Gson();
        String arrayListString = sp1.getString("list", null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>() {}.getType();
        ArrayList<HashMap<String,String>> arrayList = gson.fromJson(arrayListString, type);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.setProgress(1);

                String mid = edt_search.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("musics").orderByChild("mid").startAt(mid).endAt(mid + "\uf8ff").limitToFirst(100);

                readData(query, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        btn_search.setProgress(0);
                        final List<MusicC> arrayOfMusics = new ArrayList<MusicC>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                MusicC musicC = singleSnapshot.getValue(MusicC.class);
                                arrayOfMusics.add(musicC);

                            }

                            MusicsAdapter adapter = new MusicsAdapter(arrayOfMusics, getActivity());
                            adapter.notifyDataSetChanged();

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            lv_m.setLayoutManager(layoutManager);
                            lv_m.setAdapter(adapter);

                            adapter.setOnItemClickedListener(new MusicsAdapter.OnItemClickedListener() {
                                public void onItemClick(itemC itm) {
                                    Log.e("MUDIC", itm.getId() + " " + itm.getMid());
//                                    Intent pass = new Intent(getActivity(), RecommendActivity.class);
//                                    pass.putExtra("_id", musicC.get_id());
//                                    pass.putExtra("mid",musicC.getMid());
//                                    startActivity(pass);

                                databaseReference.child("udata").child(ids).child("listen").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int i = 0;
                                        boolean b = true;
                                        for (DataSnapshot singleSnap: dataSnapshot.getChildren()){
                                            i = Integer.parseInt(singleSnap.getKey().toString());
                                            for (DataSnapshot single: singleSnap.getChildren()){
                                                String key = single.getKey().toString();
                                                if (key!=null && key.equals(itm.getMid())){
                                                    po = String.valueOf(itm.getId());
                                                    mi = itm.getMid();
                                                    int value = Integer.parseInt(single.getValue().toString())+1;
                                                    b = false;
                                                    databaseReference.child("udata").child(ids).child("listen").child(String.valueOf(i)).child(key).setValue(value+"", new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            Intent i_ser = new Intent(getActivity(), IntentServiceDistance.class);
                                                            getActivity().startService(i_ser);
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                        if (b) databaseReference.child("udata").child(ids).child("listen").child(String.valueOf(i+1)).child(itm.getMid()).setValue(1+"", new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Intent i_ser = new Intent(getActivity(), IntentServiceDistance.class);
                                                getActivity().startService(i_ser);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                }
                            });
                        }else {
                            MusicsAdapter adapter = new MusicsAdapter(arrayOfMusics, getActivity());
                            adapter.notifyDataSetChanged();

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            lv_m.setLayoutManager(layoutManager);
                            lv_m.setAdapter(adapter);
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
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
}
