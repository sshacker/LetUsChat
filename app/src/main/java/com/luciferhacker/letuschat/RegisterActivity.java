package com.luciferhacker.letuschat;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements MyStringsConstant {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateNewAccountButton;
    private Toolbar mRegisterToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mRegisterProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterToolbar = (Toolbar) findViewById(R.id.register_tool_bar);
        setSupportActionBar(mRegisterToolbar);
        getSupportActionBar().setTitle(strRegister);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRegisterProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout) findViewById(R.id.register_display_name_text_input_layout);
        mEmail = (TextInputLayout) findViewById(R.id.register_email_text_input_layout);
        mPassword = (TextInputLayout) findViewById(R.id.register_password_text_input_layout);
        mCreateNewAccountButton = (Button) findViewById(R.id.register_create_account_button);

        mCreateNewAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegisterProgressDialog.setTitle("Registering User");
                    mRegisterProgressDialog.setMessage("please wait while we create your account");
                    mRegisterProgressDialog.setCanceledOnTouchOutside(false);
                    mRegisterProgressDialog.show();

                    registerUser(displayName, email, password);
                }
            }
        });
    }

    // Create a new createAccount method which takes in an email address and password,
    // validates them and then creates a new user with the createUserWithEmailAndPassword method.
    private void registerUser(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String currentUserId = currentUser.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE).child(currentUserId);
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put(strName, displayName);
                    userMap.put(strSTATUS, "Hi there I'm using LetUsChat.");
                    userMap.put(strIMAGE, strDEFAULT);
                    userMap.put(strTHUMB_IMAGE, strDEFAULT);
                    userMap.put(strDEVICE_TOKEN, deviceToken);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRegisterProgressDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                } else {

                    mRegisterProgressDialog.hide();
                    // If sign in fails, display a message to the user.
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        });
    }
}
