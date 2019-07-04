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

public class MainActivity<sectionsPageAdapter> extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mMainToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mMainToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setTitle(strLetUsChat);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_tabpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // When initializing your Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        // If user no login re-direct to StartActivity
        if (currentUser == null) {
            sendToStart();
        }
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

        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.main_setting_btn) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        }
        if (item.getItemId() == R.id.main_all_btn) {
            Intent userIntent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(userIntent);
        }
        return true;
    }
}