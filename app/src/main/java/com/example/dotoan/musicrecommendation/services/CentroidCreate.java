package com.example.dotoan.musicrecommendation.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.dotoan.musicrecommendation.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CentroidCreate extends Service {
    int n = 25;
    int nMusic =  13369;
    int nUser = 1259;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = db.getReference();

    public CentroidCreate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cenSelect();
    }

    private boolean cenSelect (){
        Log.e("cenSelect","Running...");
        final boolean[] kt = {false};
        final boolean[] kt2 = {false};
        final boolean[] k = {false};
        databaseReference.child("Kmean").child("centroid").removeValue();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                int j = 0;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zone : dataSnapshot.child("habitatMatrix").getChildren()) {
                        int j2=0;
                            for (DataSnapshot zoneDetail : zone.getChildren()) {
                                kt2[0] = false;
                                databaseReference.child("Kmean").child("centroid").child(zone.getKey()).child(zoneDetail.getKey()).setValue(zoneDetail.getValue());
                                //Log.i("System.out",zone.getKey()+" - "+zoneDetail.getKey()+" : "+zoneDetail.getValue());
                                if (j2++ == zone.getChildrenCount()-1) kt2[0] = true;
                            }
                            j++;
                            if (j==n) {
                                databaseReference.child("app").child("control").setValue(1);
                                break;
                            }
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        return kt[0];
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("CentroidCreate","Stop");
    }
}
