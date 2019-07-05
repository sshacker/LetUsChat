package com.luciferhacker.letuschat;

import android.content.Intent;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity<sectionsPageAdapter> extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mMainToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser != null ){

            mUserReference = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE).child(currentUser.getUid());
        }

        mMainToolbar = (Toolbar) findViewById(R.id.main_appbar_toolbar);
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setTitle(strLetUsChat);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_tabs_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // When initializing your Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);

        // If user no login re-direct to StartActivity
        if (currentUser == null) {
            sendToStart();

        } else {
            /* ONLINE USER */
            //mUserReference.child(strONLINE).setValue(true);
        }
    }

    protected void onStop(){
        super.onStop();

        /* OFFLINE USER */
        // mUserReference.child(strONLINE).setValue(false);
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_menu_log_out_button) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.main_menu_about_us_button) {
            Intent aboutUsIntent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(aboutUsIntent);
        }
        if (item.getItemId() == R.id.main_menu_account_settings_button) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        }
        if (item.getItemId() == R.id.main_menu_all_users_button) {
            Intent userIntent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(userIntent);
        }

        return true;
    }
}