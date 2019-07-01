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

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout mDisplayName;
    TextInputLayout mEmail;
    TextInputLayout mPassword;
    Button mCreateBtn;

    private Toolbar mToolbar;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mregProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // In the onCreate() method, initialize the FirebaseAuth instance.

        mToolbar = (Toolbar)findViewById(R.id.register_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create account");
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mregProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout)findViewById(R.id.reg_email);
        mPassword = (TextInputLayout)findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);




        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    mregProgress.setTitle("Registering User");
                    mregProgress.setMessage("please wait while we create your account");
                    mregProgress.setCanceledOnTouchOutside(false);
                    mregProgress.show();

                    register_user(display_name, email, password);
                }


            }
        });

    }

    // Create a new createAccount method which takes in an email address and password,
    // validates them and then creates a new user with the createUserWithEmailAndPassword method.
    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("status", "Hi there I'm using LetUsChat.");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token",deviceToken);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mregProgress.dismiss();
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });

                        } else {

                            mregProgress.hide();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
