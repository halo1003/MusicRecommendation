package com.example.dotoan.musicrecommendation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.example.dotoan.musicrecommendation.Contruct.UserC;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends FragmentActivity {

    @BindView(R.id.name) EditText edtName;
    @BindView(R.id.password) EditText edtPassword;
    @BindView(R.id.submit) MorphingButton btnSubmit;
    @BindView(R.id.sign_in_button) SignInButton btnGoogleSignIn;
    @BindView(R.id.txtvError) TextView txtvError;

    private FirebaseAuth mAuth;
    String TAG = "DT_TAG";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private int h,w;

    private int duration = 500;
    private boolean signIn = true;
    private String email = null;
    private String password = null;
    private boolean btnActived = true;

    SuperActivityToast superActivityToast;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        superActivityToast = new SuperActivityToast(LoginActivity.this, Style.TYPE_PROGRESS_CIRCLE);
        superActivityToast.setText("Loading...");
        superActivityToast.setIndeterminate(true);
        superActivityToast.setProgressIndeterminate(true);
        superActivityToast.setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ViewTreeObserver vto = btnSubmit.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                btnSubmit.getViewTreeObserver().removeOnPreDrawListener(this);
                h = btnSubmit.getMeasuredHeight();
                w = btnSubmit.getMeasuredWidth();
                Log.d("hw",h+" "+w+"");
                return true;
            }
        });

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = edtName.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = edtPassword.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signIn) {
                    if ( email == null || password == null){
                        txtvError.setText("Email and Password are required");
                        txtvError.setVisibility(View.VISIBLE);
                    }else{

                        superActivityToast.show();

                        signInNormal(email,password);
                        txtvError.setVisibility(View.INVISIBLE);
                    }
                }else{
                    signIn = true;
                    morphToSquare(btnSubmit);
                }
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signInNormal(final String email, final String password){
        final boolean[] f = {true};
        Query query = databaseReference.child("udata").orderByChild("_id").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    superActivityToast.dismiss();
                    for (DataSnapshot singleSnap: dataSnapshot.getChildren()){
                        GenericTypeIndicator<List<HashMap<String,String>>> t = new GenericTypeIndicator<List<HashMap<String,String>>>() {};
                        List<HashMap<String,String>> hash = singleSnap.child("listen").getValue(t);
                        //7701a7b264c5f73ef3895d6dab2660f265c6c0e2
                        UserC userC  = singleSnap.getValue(UserC.class);

                        Gson gson = new Gson();
                        String hashMapString = gson.toJson(hash);

                        SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                        editor.putString("_id",userC.get_id());
                        editor.putString("id",userC.getId());
                        editor.putString("list", hashMapString);
                        editor.apply();
                    }

                    btnSubmit.setClickable(false);

                    txtvError.setTextColor(getResources().getColor(R.color.colorGreen));
                    txtvError.setText(getString(R.string.strSuccess)+" "+ email);
                    txtvError.setVisibility(View.VISIBLE);
                    morphToSuccess(btnSubmit);
                }else {
                    signIn_action(email,password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signIn_action(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                superActivityToast.dismiss();
                                btnSubmit.setClickable(false);
                                final String name = user.getDisplayName();
                                txtvError.setTextColor(getResources().getColor(R.color.colorGreen));
                                txtvError.setText(getString(R.string.strSuccess)+" "+ name);
                                txtvError.setVisibility(View.VISIBLE);
                            }
                            morphToSuccess(btnSubmit);
                        } else {
                            morphToFail(btnSubmit);
                            signIn = false;
                            txtvError.setText(task.getException().getMessage());
                            txtvError.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        btnActived = false;
        btnSubmit.setClickable(btnActived);
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(100)
                .width(h)
                .height(h)
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    private void morphToFail(final MorphingButton btnMorph) {
        btnActived = false;
        btnSubmit.setClickable(btnActived);
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(100)
                .width(h)
                .height(h)
                .icon(R.drawable.ic_fail);
        btnMorph.morph(circle);

        if (!btnActived) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    btnSubmit.setClickable(true);
                    btnActived = true;
                }
            }, duration+100);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            morphToSuccess(btnSubmit);
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            txtvError.setTextColor(getResources().getColor(R.color.colorGreen));
                            txtvError.setText("Success: Wellcome "+firebaseUser.getDisplayName());
                            txtvError.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void morphToSquare(final MorphingButton btnMorph){
        btnActived = false;
        btnSubmit.setClickable(btnActived);
        MorphingButton.Params square = MorphingButton.Params.create()
                .cornerRadius(100)
                .width(w)
                .height(h)
                .strokeColor(getColor(R.color.colorBlack))
                .color(getColor(R.color.colorOrange))
                .colorPressed(getColor(R.color.colorGoogle))
                .duration(duration)
                .text(getString(R.string.mb_login));
        btnMorph.morph(square);

        if (!btnActived) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    btnSubmit.setClickable(true);
                    btnActived = true;
                }
            }, duration+100);
        }
    }
}
