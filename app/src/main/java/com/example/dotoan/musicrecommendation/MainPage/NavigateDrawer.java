package com.example.dotoan.musicrecommendation.MainPage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.MainActivity;
import com.example.dotoan.musicrecommendation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class NavigateDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);
        String _id = sp1.getString("_id", null);
        String id = sp1.getString("id",null);

        Gson gson = new Gson();
        String arrayListString = sp1.getString("list", null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>() {}.getType();
        ArrayList<HashMap<String,String>> arrayList = gson.fromJson(arrayListString, type);

        Log.e("ARRAY",arrayList+"");



        TextView name = (TextView)header.findViewById(R.id.name);
        TextView email = (TextView)header.findViewById(R.id.email);

        if (_id!=null){
            email.setText(_id);
            name.setText(id);
        }else if (fAuth!=null){
            name.setText(fAuth.getCurrentUser().getDisplayName());
            email.setText(fAuth.getCurrentUser().getEmail());
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigate_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_user) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new AdminFragment()).commit();

        } else if (id == R.id.nav_normal) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new NormalFragment()).commit();

        } else if (id == R.id.nav_signout) {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);
            String _id = sp1.getString("_id",null);
            if (fAuth!=null) fAuth.signOut();
            if (_id!=null) sp1.edit().clear().apply();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_database_fix) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new DatabasefixFragment()).commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
