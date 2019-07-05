package com.luciferhacker.letuschat;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegisterNewAccountButton;
    private Button mLoginAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegisterNewAccountButton = (Button) findViewById(R.id.start_register_button);
        mLoginAccountButton = (Button) findViewById(R.id.start_login_button);

        mLoginAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        mRegisterNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
