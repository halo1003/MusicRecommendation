package com.example.dotoan.musicrecommendation.OldKmean;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dotoan.musicrecommendation.LoginActivity;
import com.example.dotoan.musicrecommendation.R;
import com.example.dotoan.musicrecommendation.SignUpActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.signup) Button btnSignUp;
    @BindView(R.id.login) Button btnLogin;
    @BindView(R.id.lilaspinner) LinearLayout linearLayout;
    @BindView(R.id.spinner) ProgressBar progressBar;
    @BindView(R.id.processText) TextView txtv;
    @BindView(R.id.updateText) TextView txtvUpdate;

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
                        //Intent nav_i = new Intent(getApplicationContext(), Na.class);
                        //startActivity(nav_i);
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
                //TODO: Kmean process

//                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//                database.child("app").child("control").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        int c = Integer.parseInt(dataSnapshot.getValue().toString());
//                        if (c == 0) {
//                            database.child("Kmean").child("00group").setValue(0);
//                            Log.e("App","Automate set value 00group = 0");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                database.child("app").child("control").setValue(0);
//                database.child("Kmean").child("centroid").removeValue();
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
        final int[] lastu = {0};
        //TODO: Kmean process
//        Intent listen = new Intent(MainActivity.this, FirebaseEventListener.class);
//        startService(listen);

//        final DatabaseReference d1 = FirebaseDatabase.getInstance().getReference("Kmean");
////        d1.child("00group").setValue(0);
//        d1.child("01process").setValue(false);
//        d1.child("02lastuser").setValue(0);
//        d1.child("balance group").removeValue();
//
//        databaseReference.child("balance").setValue(false);
//        databaseReference.child("habitatMatrix").child(String.valueOf(n-1)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int temp =0;
//                int num = (int) dataSnapshot.getChildrenCount();
//                for (DataSnapshot zone: dataSnapshot.getChildren()){
//                    temp++;
//                    if (temp == num){
//                        databaseReference.child("Kmean").child("02lastuser").setValue(zone.getKey());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        final DatabaseReference data = FirebaseDatabase.getInstance().getReference("app");
//        data.child("controlv2").setValue(false);
//        data.child("control").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int c = Integer.parseInt(dataSnapshot.getValue().toString());
//                switch (c){
//                    case 0:
//                        Log.e("switch",0+"");
//                        Intent Centroid_i = new Intent(getApplicationContext(), CentroidCreate.class);
//                        startService(Centroid_i);
//
//                        break;
//                    case 1:
//                        Log.e("switch",1+"");
//                        Intent i = new Intent(getApplicationContext(), CentroidCreate.class);
//                        stopService(i);
//
//                        DatabaseReference d2 = FirebaseDatabase.getInstance().getReference();
//                        d2.child("app").child("control").setValue(2);
//                        d2.child("Kmean").child("00group").setValue(++count[0]);
//                        isStart = false;
//
//                        Log.e("00group",count[0]+"");
//
//                        new DistanceComputeAsyn().execute();
//                        break;
//                    case 2:
//                        Log.e("switch",2+"... not thing");
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        data.child("controlv2").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue().toString().equals("true")){
//                    data.child("control").setValue(1);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

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

    public class DistanceComputeAsyn extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("DistanceComputeAsyn","DONE");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("DistanceComputeAsyn","Running");
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                int cenPos[];
                double cenVal[] ;

                int objPos[] ;
                double objVal[] ;

                double simCombine[][];

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int nx = (int) dataSnapshot.child("Kmean/centroid").getChildrenCount();
                    int ny = (int) dataSnapshot.child("habitatMatrix").getChildrenCount();

                    simCombine = new double [nx][ny];
                    int x =0;
                    for (DataSnapshot zone: dataSnapshot.child("Kmean/centroid").getChildren()){
                        int size = (int) zone.getChildrenCount();
                        //Log.e("Kmean/centroid", String.valueOf(size));
                        cenPos = new int[size];
                        cenVal = new double[size];
                        int i =0, y=0;
                        for(DataSnapshot zoneDetail: zone.getChildren()){
                            cenPos[i] = Integer.parseInt(zoneDetail.getKey().toString());
                            cenVal[i++] = Double.parseDouble(zoneDetail.getValue().toString());
                        }

                        for (DataSnapshot zone1: dataSnapshot.child("habitatMatrix").getChildren()){
                            int size1 = (int) zone1.getChildrenCount();
                            //Log.i("habitatMatrix", String.valueOf(size1));
                            objPos = new int[size1];
                            objVal = new double[size1];
                            int j =0;
                            for (DataSnapshot zoneDetail: zone1.getChildren()){
                                objPos[j] = Integer.parseInt(zoneDetail.getKey().toString());
                                objVal[j++] = Double.parseDouble(zoneDetail.getValue().toString());
                            }

                            if (cenVal != null && cenPos !=null){
                                double sim = similarityDistance(cenVal,cenPos,objVal,objPos);
                                simCombine[x][y++] = sim;
                            }
                        }
                        x++;
                    }
                    groupCen(simCombine);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Error",databaseError+"");
                }
            });
            return null;
        }

        private int[][] Matrix(){

            final int m[][] = new int[nUser][nMusic];
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("habitatMatrix");
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                int i =0;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zone: dataSnapshot.getChildren()){
                        int row = Integer.parseInt(zone.getKey().toString());
                        for (DataSnapshot zonedetail: zone.getChildren()){
                            int column = Integer.parseInt(zonedetail.getKey().toString());
                            int value = Integer.parseInt(zonedetail.getValue().toString());
                            for (int j = i; j<column; j++){
                                m[row][j] = 0;
                            }
                            m[row][column] = value;
                            //Log.i("Matrix","m["+row+"]["+column+"] = "+value);
                            i = column+1;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return m;
        }

        private void patchValue(int m[],String s){
            List<Integer> h = new ArrayList<Integer>();
            for (int i = 0;i<m.length;i++ )
                h.add(m[i]);
            Log.e("pathValue",s+" | size ="+h.size());
            Log.d("m", String.valueOf(h));
        }


        private double similarityDistance(double centVal[],int centPos[], double objVal[],int objPos[]){
            double tag =0, cenX=0,cenY=0,sol =0;
            List <Double> cenval = new ArrayList<Double>();
            List <Integer> cenpos = new ArrayList<Integer>();
            List <Double> objval = new ArrayList<Double>();
            List <Integer> objpos = new ArrayList<Integer>();
            //Log.e("end","____________________________________________________________________");
//            patchValue(centPos,"cenpos");
//            patchValue(objPos,"objpos");
//            patchValue(centVal,"cenval");
//            patchValue(objVal,"objval");

            if (centPos.length>=objPos.length) {
                for (int i = 0; i < centPos.length; i++) {
                    if (i<objPos.length) {
                        for (int j =0;j< objPos.length;j++) {
                            if (centPos[i] == objPos[j]) {
//                                Log.d("tag1", "cenval[" + i + "]-objval[" + j + "]");
                                tag = tag + (centVal[i] - objVal[j])*(centVal[i] - objVal[j]);
                                break;
                            }

                            if (j == objPos.length-1){
                                cenX = cenX + centVal[i]*centVal[i];
                                cenY = cenY + objVal[i]*objVal[i];
                                //Log.d("tag11", i+ " cenX = "+cenX+", cenY = "+cenY);
                            }
                        }
                    } else {
                        cenX = cenX + centVal[i]*centVal[i];
                        //cenY = objVal[i];
                        //Log.d("tag1", "cenX = "+cenX+", cenY = "+cenY);
                    }
                    //Log.e("sol", sol+" + "+tag+" + "+cenX+" + "+cenY);
                    sol = tag + cenX + cenY ;
                    //Log.e("sol",sol+"");
                }
            }else{
                for (int i = 0; i < objPos.length; i++) {
                    if (i< centPos.length) {
                        for (int j =0;j< centPos.length;j++) {
                            if (centPos[j] == objPos[i]) {
//                                Log.d("tag2", "cenval[" + j + "]-objval[" + i + "]");
                                tag = tag + (centVal[j] - objVal[i])*(centVal[j] - objVal[i]);

                                break;
                            }

                            if (j == centPos.length-1){
                                cenX = cenX + centVal[i]*centVal[i];
                                cenY = cenY + objVal[i]*objVal[i];
                                //Log.d("tag21", i+" cenX = "+cenX+", cenY = "+cenY);
                            }
                        }
                    } else {
                        //cenX = centVal[i];
                        cenY = cenY + objVal[i]*objVal[i];
                        //Log.d("tag2", i+" cenX = "+cenX+", cenY = "+cenY);
                    }
                    //Log.e("sol", sol+" + "+tag+" + "+cenX+" + "+cenY);
                    sol = tag + cenX + cenY ;
                    //Log.e("sol",sol+"");
                }
            }

            double s = Math.sqrt(sol);
            return (double)Math.round(s*10000)/10000;
        }

        private void groupCen(double m[][]){
            Log.e("groupCen","Running...");
            final int combine[][] = new int [n][nUser];
            double[] checkm;
            for (int i =0; i < n;i++ ) {
                for (int j = 0; j < nUser; j++) {
                    combine[i][j] = 0;
                }
            }

            for (int i =0 ; i< m[0].length;i++){
                checkm = new double[n];
                for (int j =0; j< n; j++){
                    checkm[j] = m[j][i];
                }
                int x = smallestVal(checkm);
                combine[x][i] = 1;
                //Log.i("System.out","combine["+x+"]["+i+"])");
            }
            boolean bal = balance(combine);

            Log.e("Balance: ",bal+"");

            databaseReference.child("Kmean").child("balance").setValue(bal);

                if (!bal) {
                    int habitat[][] = Matrix();

                    Timer timer = new Timer();
                    final int[][] finalHabitat = habitat;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            databaseReference.child("Kmean").child("centroid").removeValue();
                            databaseReference.child("app").child("controlv2").setValue(false);
                            for (int i =0; i<n;i++) {
                                final List<Integer> sumMatrix = new ArrayList<Integer>();
                                for (int j = 0; j < nUser; j++){
                                    if (combine[i][j] == 1){
                                        sumMatrix.add(j);
                                    }
                                }
                                Log.e("sumMatrix",i+": "+sumMatrix+"");
                                ParameterContructor p = new ParameterContructor(i,sumMatrix,finalHabitat);
                                new PushParameterAsyn().execute(p);
                            }
                        }
                    },5000);
                }
        }

        private boolean balance(int m[][]){
            Log.e("balance","Running...");
            boolean k = false;
            int temp[] = new int [nUser];
            for (int i =0; i < n; i++){
                int sum =0;
                for (int j = 0;j < nUser;j++){
                    sum += m[i][j];
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Kmean");
                ref.child("balance group").child("00group").setValue(count[0]);
                ref.child("balance group").child(String.valueOf(i)).setValue(sum);
                //Log.d("Balance number",i+" -> "+sum);
                temp[i]= sum;
            }

            int min = temp[0], max = temp[0];

            for (int i =0; i< temp.length;i++){
                if (temp[i]<min) {
                    min = temp[i];
                    break;
                }

                if (temp[i]>max){
                    max = temp[i];
                    break;
                }

                if (i == temp.length-1 && max == min){
                    k = true;
                }
            }
            return k;
        }

        private int smallestVal(double m[]){
            Log.i("smallestVal","Running...");
            double sma = m[0];
            int pos =0;
            for (int i =1;i<m.length;i++){
                if (m[i] <= sma) {
                    sma = m[i];
                    pos = i;
                }
            }
            return pos;
        }
    }

    public class ParameterContructor{
        int i;
        List<Integer> sumMatrix = new ArrayList<Integer>();
        int[][] finalHabitat = new int[nUser][nMusic];

        public ParameterContructor(int i, List<Integer> sumMatrix, int[][] finalHabitat) {
            this.i = i;
            this.sumMatrix = sumMatrix;
            this.finalHabitat = finalHabitat;
        }
    }

    public class PushParameterAsyn extends AsyncTask<ParameterContructor,Integer,String>{

        @Override
        protected String doInBackground(ParameterContructor... params) {
            int i = params[0].i;
            List<Integer> sumMatrix = params[0].sumMatrix;
            int[][]finalHabitat = params[0].finalHabitat;

            Timer timer = new Timer();
            timer.schedule(new PushParameter(i,sumMatrix,finalHabitat),1000);
            return null;
        }
    }

    class PushParameter extends TimerTask {

        int n = 25;
        int nMusic =  13369;
        int nUser = 1259;

        private final int i;
        private List<Integer> sumMatrix = new ArrayList<Integer>();
        private int[][] finalHabitat = new int[nUser][nMusic];

        PushParameter ( int i ,List<Integer> sumMatrix, int[][] finalHabitat) {
            this.i = i;
            this.sumMatrix = sumMatrix;
            this.finalHabitat = finalHabitat;
        }

        public void run() {
            //Do stuff
            if (sumMatrix.size()>1){
                System.out.println("sumMatrix.size()>1");
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final double arr[] = new double [nMusic];

                        for (int temp = 0; temp< nMusic; temp++){
                            arr[temp] = 0;
                        }
                        int k =0;
                        int count_zero = 0;
                        for (int j : sumMatrix){
                            k++;
//                          Log.e("List sumMatrix",j+"");
                            for (int temp = 0; temp< finalHabitat[j].length; temp++){
                                arr[temp] = arr[temp] + (double) finalHabitat[j][temp];
                            }

                            for(int temp =0; temp< finalHabitat[j].length; temp++)
                                if (arr[temp]!=0) {
                                    count_zero++;
                                }

                            for(int temp =0; temp< finalHabitat[j].length; temp++)
                                if (arr[temp]!=0) {
                                    arr[temp] = arr[temp]/count_zero;
                                }

                            for(int temp =0; temp< finalHabitat[j].length; temp++)
                            if (arr[temp]!=0) {
                                if (temp == finalHabitat[j].length-1 && k == sumMatrix.size()){
                                    databaseReference.child("Kmean").child("02lastuser").setValue(temp);
                                }
                            }
                        }

                        Map<String,Double> time = new HashMap<>();

                        for (int temp =0; temp < nMusic; temp++){
                            if (arr[temp]!=0) time.put(String.valueOf(temp),arr[temp]);
                        }

                        databaseReference.child("Kmean").child("centroid").child(String.valueOf(i)).setValue(time);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }else {
                System.out.println("sumMatrix.size()<=1");
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    int k =0;
                    int k1 =0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (int j : sumMatrix){
                            k++;
                            for (DataSnapshot zone: dataSnapshot.child("habitatMatrix").child(String.valueOf(j)).getChildren()){
                                databaseReference.child("Kmean").child("centroid").child(String.valueOf(i)).child(zone.getKey()).setValue(zone.getValue());
                                //Log.i("ELse",finalI1+" : "+zone.getKey()+" / "+zone.getValue());
                                if (k == sumMatrix.size()){
                                    int num = (int) dataSnapshot.child("habitatMatrix").child(String.valueOf(j)).getChildrenCount();
                                    k1++;
                                    if (num == k1) databaseReference.child("Kmean").child("02lastuser").setValue(zone.getKey());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

}
