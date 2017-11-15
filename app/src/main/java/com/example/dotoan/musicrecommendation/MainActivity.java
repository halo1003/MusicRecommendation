package com.example.dotoan.musicrecommendation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dotoan.musicrecommendation.Contruct.DistanceC;
import com.example.dotoan.musicrecommendation.Contruct.FilterC;
import com.example.dotoan.musicrecommendation.Contruct.ListC;
import com.example.dotoan.musicrecommendation.Contruct.Node;
import com.example.dotoan.musicrecommendation.MainPage.NavigationActivity;
import com.example.dotoan.musicrecommendation.SQLite.DBManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.signup) Button btnSignUp;
    @BindView(R.id.login) Button btnLogin;
    @BindView(R.id.lilaspinner) LinearLayout linearLayout;
    @BindView(R.id.spinner) ProgressBar progressBar;
    @BindView(R.id.processText) TextView txtv;
    @BindView(R.id.updateText) TextView txtvUpdate;

    static String DBname = "parameter";
    static String TBname = "min_max";

    int n = 25;
    int nMusic = 13369;
    int nUser = 1259;
    int[] count = {1};
    boolean isStart = true;

    String app = "music.apk";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    String deviceId;
    File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_i = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(signup_i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(login_i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("App","MainActivity is off");
    }

    private void downloadFile(String s) {
        TelephonyManager telephonyManager = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child(app);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Download");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        localFile = new File(rootPath,s);

        pathRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                databaseReference.child("app").child("devicesUpdated").child(deviceId).setValue(true);
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
                txtvUpdate.setText("Failed: "+exception.getMessage().toString());
            }
        });
    }

    public boolean signIn_check(){

        boolean f = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            f = true;
        } else {
            // User is signed out
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = database.getReference("app");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    txtvUpdate.setVisibility(View.VISIBLE);
                    TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    final String deviceId = telephonyManager.getDeviceId();

                    app = dataSnapshot.child("Appname").getValue().toString()+".apk";
                    String download = dataSnapshot.child("download").getValue().toString();

                    if (app!=null && download.equals("enable") && dataSnapshot.child("devicesUpdated").hasChild(deviceId) == false){
                        txtvUpdate.setText("New update is available!\nClick here");
                        txtvUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                txtvUpdate.setClickable(false);
                                txtvUpdate.setText("Downloading... Please wait");
                                downloadFile(app);
                            }
                        });
                    }else if (download.equals("enable") && dataSnapshot.child("devicesUpdated").hasChild(deviceId) == true){
                        File f = new File(String.valueOf(localFile));
                        if (f.exists()){
                            txtvUpdate.setText("Done: "+localFile);
                            txtvUpdate.setClickable(false);
                        }
                    }else {
                        txtvUpdate.setText("");
                        txtvUpdate.setClickable(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return f;
    }

    public void delay(){
        final Handler handler = new Handler();
        for (int i=0;i<4;i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                public void run() {
                    txtv.setText("Waiting ...("+ finalI +"/3)");
                    if (finalI == 3) {
                        Intent nav_i = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(nav_i);
                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
            Intent intent = new Intent();
            intent.setAction("dotoan.com");
            sendBroadcast(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity","run");
        txtvUpdate.setVisibility(View.INVISIBLE);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                hacking();
                if (signIn_check()){
                    progressBar.setVisibility(View.VISIBLE);
                    delay();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    txtv.setText("CLick Login or Sign up button below");
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permission denied")
                .setDeniedMessage(
                        "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void hacking(){
        boolean c1 = false;
        final String device = DeviceName.getDeviceName();
        DeviceName.with(getApplicationContext()).request(new DeviceName.Callback() {
            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String name = info.marketName;            // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"

                databaseReference.child("realtime").child(deviceID()).child(device).child("manufacturer").setValue(manufacturer);
                databaseReference.child("realtime").child(deviceID()).child(device).child("name").setValue(name);
                databaseReference.child("realtime").child(deviceID()).child(device).child("model").setValue(model);
                databaseReference.child("realtime").child(deviceID()).child(device).child("codename").setValue(codename);
                databaseReference.child("realtime").child(deviceID()).child(device).child("deviceName").setValue(deviceName);

            }
        });
        androidver();
        List<Double> distance_compare = new ArrayList<Double>();

        DBManager db = new DBManager(getApplicationContext());

        db.copyTable();
        Log.e("minmaxCount", String.valueOf(db.getNodesCount("min_max_temp")));

        for (Node i:db.getAllNode("min_max_temp")){
            double Dis = i.getDistance();
            distance_compare.add(Dis);
        }

        double last = Gmax(distance_compare);
        double initial_step = Gmin(distance_compare);

        int element_require = 1253/n;
        List<ListC>  group = new ArrayList<ListC>();

        double Radius = initial_step;
        double step = initial_step;
        int count = 0;
        while (Radius<=last){
            Log.e("Radius", String.valueOf(Radius)+" - "+db.getNodesCount("min_max_temp"));
            int user1 = 0;
            List<FilterC> filter = new ArrayList<FilterC>();
            while(user1<1253){
//                Log.d("user1", String.valueOf(user1));
                List<Integer> user2_per1 = new ArrayList<Integer>();
                for (Node i : db.Querry(user1)){
                    if (i.getDistance()<=Radius){
                        user2_per1.add(i.getUser_2());
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
            List<Integer> arr = objUser.getArrayList();

            if (maxUser < element_require) {
                count++;
                if (count == 3){
                    Radius = Radius + db.Min("min_max_temp");
                    count = 0;
                }else{
                    Radius = Radius + step;
                }

                Log.e("Status","Not found, Increase Radius to "+Radius);
            }
            else{
                count = 0;
                Log.e("Status","Group found, User Discussion is "+user1_Discussion);
                ListC listC = new ListC();
                listC.setUser1(user1_Discussion);
                listC.setRadius(Radius);
                listC.setUser2(arr);

                group.add(listC);
                for (int i: arr){
                    db.Delete_user2(i);
                }
                Radius = Radius + initial_step;
                Log.e("Status","Radius update to "+Radius);
            }
        }

        for (Node i: db.cQuerry()){
            Log.i("cQuerry",i.getID()+"|"+i.getUser_1()+"|"+i.getUser_2()+"|"+i.getDistance());
        }

        for (ListC i: group){
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Radius").setValue(i.getRadius());
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Array").child("Size").setValue(i.getUser2().size());
            databaseReference.child("Relative Group").child(String.valueOf(i.getUser1())).child("Array").child("Value").setValue(i.getUser2());
        }



        if (c1)
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

    private Node maxL(List<FilterC> li){
        int max = 0;
        int user1 = 0;
        List<Integer> user2_per1 = new ArrayList<Integer>();

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

    private void androidver(){
        String device = DeviceName.getDeviceName();
        databaseReference.child("realtime").child(deviceID()).child(device).child("OS").child("android").setValue(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                databaseReference.child("realtime").child(deviceID()).child(device).child("OS").child("androidname").setValue(fieldName);
                databaseReference.child("realtime").child(deviceID()).child(device).child("OS").child("sdk").setValue(fieldValue);
            }
        }
    }

    private String deviceID(){
        TelephonyManager telephonyManager = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    public class DistanceComputeAsyn extends AsyncTask<DistanceC,Integer,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
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

                        Node node = new Node(current_user,user_2,sim);
                        DBManager db = new DBManager(getApplicationContext(),node);
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

        private int[][] Matrix() {

            final int m[][] = new int[nUser][nMusic];
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("habitatMatrixv2");
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                int i = 0;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zone : dataSnapshot.getChildren()) {
                        int row = Integer.parseInt(zone.getKey().toString());
                        for (DataSnapshot zonedetail : zone.getChildren()) {
                            int column = Integer.parseInt(zonedetail.getKey().toString());
                            int value = Integer.parseInt(zonedetail.getValue().toString());
                            for (int j = i; j < column; j++) {
                                m[row][j] = 0;
                            }
                            m[row][column] = value;
                            i = column + 1;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return m;
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
