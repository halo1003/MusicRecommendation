package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dotoan.musicrecommendation.Contruct.Mdetail;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.SQLite.DBMusicsManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by DOTOAN on 11/20/2017.
 */

public class DatabasefixFragment extends Fragment {
    View view;

    @BindView(R.id.btn_fix) Button btn_fix;
    @BindView(R.id.btn_fix_add) Button btn_music;
    private Unbinder unbinder;
    int i = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.database_frame_fix,container,false);
        unbinder = ButterKnife.bind(this, view);

        btn_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("udata").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnap: dataSnapshot.getChildren()){
                            final String key = singleSnap.getKey().toString();
                            databaseReference.child("udata").child(key).child("id").setValue(key, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Log.e("TAG",key+"/id: "+key);
                                    Log.i("JSON", String.valueOf(databaseReference));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        btn_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBMusicsManager dbMusicsManager = new DBMusicsManager(getActivity());
                dbMusicsManager.DropTB();
                dbMusicsManager.CreateTB();

               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
               databaseReference.child("Musicdetail").child("detail").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       Log.e("TAG",dataSnapshot.toString());
                       for(DataSnapshot single: dataSnapshot.getChildren()){
                           Mdetail m = single.getValue(Mdetail.class);
                           dbMusicsManager.addNode(m);
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
            }
        });
        return view;
    }

    public class A{
        int id;
        String mid;
        String name;
        String artict;
        String tract;

        public A() {
        }

        public A(int id, String mid, String name, String artict, String tract) {
            this.id = id;
            this.mid = mid;
            this.name = name;
            this.artict = artict;
            this.tract = tract;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtict() {
            return artict;
        }

        public void setArtict(String artict) {
            this.artict = artict;
        }

        public String getTract() {
            return tract;
        }

        public void setTract(String tract) {
            this.tract = tract;
        }
    }

}
