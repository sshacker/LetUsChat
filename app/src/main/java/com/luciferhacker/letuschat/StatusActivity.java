package com.luciferhacker.letuschat;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mStatusToolbar;
    private TextInputLayout mStatus;
    private Button mSaveStatusButton;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mStatusProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE).child(currentUserId);

        mStatusToolbar = (Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(mStatusToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusValue = getIntent().getStringExtra(strSTATUS_VALUE);

        mStatus = (TextInputLayout)findViewById(R.id.status_input);
        mSaveStatusButton = (Button)findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(statusValue);

        mSaveStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStatusProgressDialog = new ProgressDialog(StatusActivity.this);
                mStatusProgressDialog.setTitle("Saving Changes");
                mStatusProgressDialog.setMessage("please wait !");
                mStatusProgressDialog.show();

                String status = mStatus.getEditText().getText().toString();
                mStatusDatabase.child(strSTATUS).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mStatusProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();

                            Intent settingIntent = new Intent(StatusActivity.this, SettingActivity.class);
                            startActivity(settingIntent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            mStatusProgressDialog.dismiss();
                        }
                    }
                });
            }
        });


    }
}
