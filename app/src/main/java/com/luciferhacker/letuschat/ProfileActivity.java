package com.luciferhacker.letuschat;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements MyStringsConstant {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendRequestButton, mProfileDeclineRequestButton;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsRequestDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationsDatabase;
    private FirebaseUser mCurrentUser;
    private String mCurrentState;
    private ProgressDialog mProfileProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileImage = (ImageView)findViewById(R.id.profile_image);
        mProfileName = (TextView)findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView)findViewById(R.id.profile_totalFriends);
        mProfileSendRequestButton = (Button)findViewById(R.id.profile_send_req_btn);
        mProfileDeclineRequestButton = (Button)findViewById(R.id.profile_decline_friend_req_btn);

        mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
        mProfileDeclineRequestButton.setEnabled(false);

        mCurrentState = strNOT_FRIENDS;

        final String userId = getIntent().getStringExtra(strUSER_ID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE).child(userId);
        mFriendsRequestDatabase = FirebaseDatabase.getInstance().getReference().child(strFRIENDS_REQUEST_DATABASE);
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child(strFRIENDS_DATABASE);
        mNotificationsDatabase = FirebaseDatabase.getInstance().getReference().child(strNOTIFICATIONS_DATABASE);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProfileProgressDialog.setTitle("Loading User Profile");
        mProfileProgressDialog.setMessage("please wait !");
        mProfileProgressDialog.setCanceledOnTouchOutside(false);
        mProfileProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String displayName = strDEFAULT;
                String status = strDEFAULT;
                String image = strDEFAULT;

                if (dataSnapshot.exists()){
                    displayName = dataSnapshot.child(strName).getValue(String.class);
                    status = dataSnapshot.child(strSTATUS).getValue(String.class);
                    image = dataSnapshot.child(strIMAGE).getValue(String.class);
                }

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);


                // Friend List or Request feature
                mFriendsRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userId)){
                            String requestType = dataSnapshot.child(userId).child(strREQUEST_TYPE).getValue().toString();

                            if (requestType.equals(strRECEIVED)){
                                mCurrentState = strREQUEST_RECEIVED;
                                mProfileSendRequestButton.setText("Accept Friend Request");

                                mProfileDeclineRequestButton.setVisibility(View.VISIBLE);
                                mProfileDeclineRequestButton.setEnabled(true);

                            } else if (requestType.equals(strSENT)){
                                mCurrentState = strREQUEST_SENT;
                                mProfileSendRequestButton.setText("Cancel Friend Request");

                                mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequestButton.setEnabled(false);
                            }

                            mProfileProgressDialog.dismiss();
                        } else {

                            mFriendsDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(userId)){

                                        mCurrentState = strFRIENDS;
                                        mProfileSendRequestButton.setText("Unfriend");

                                        mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                        mProfileDeclineRequestButton.setEnabled(false);

                                    }
                                    mProfileProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProfileProgressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mProfileProgressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProfileProgressDialog.dismiss();
            }
        });

        mProfileSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendRequestButton.setEnabled(false);

                // Not Friend State

                if(mCurrentState.equals(strNOT_FRIENDS)){

                    mFriendsRequestDatabase.child(mCurrentUser.getUid()).child(userId).child(strREQUEST_TYPE)
                            .setValue(strSENT).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                mFriendsRequestDatabase.child(userId).child(mCurrentUser.getUid()).child(strREQUEST_TYPE)
                                        .setValue(strRECEIVED).addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put(strFROM, mCurrentUser.getEmail());
                                        notificationData.put(strTYPE, strREQUEST_RECEIVED);

                                        mNotificationsDatabase.child(userId).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrentState = strREQUEST_SENT;
                                                mProfileSendRequestButton.setText("Cancel Friend Request");

                                                mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                mProfileDeclineRequestButton.setEnabled(false);

                                                Toast.makeText(ProfileActivity.this, "Request Send",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request",Toast.LENGTH_LONG).show();
                            }
                            mProfileSendRequestButton.setEnabled(true);

                        }
                    });
                }

                // Cancel Request State

                if(mCurrentState.equals(strREQUEST_SENT)){
                    mFriendsRequestDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsRequestDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequestButton.setEnabled(true);
                                    mCurrentState = strNOT_FRIENDS;
                                    mProfileSendRequestButton.setText("Send Friend Request");

                                    mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                    mProfileDeclineRequestButton.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                // Request Receive State

                if(mCurrentState.equals(strREQUEST_RECEIVED)){
                    final String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    mFriendsDatabase.child(mCurrentUser.getUid()).child(userId).child(strDATE).setValue(currentDateAndTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(userId).child(mCurrentUser.getUid()).child(strDATE).setValue(currentDateAndTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendsRequestDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendsRequestDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendRequestButton.setEnabled(true);
                                                    mCurrentState = strFRIENDS;
                                                    mProfileSendRequestButton.setText("Unfriend");

                                                    mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                    mProfileDeclineRequestButton.setEnabled(false);

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                    Toast.makeText(getApplicationContext(), currentDateAndTime, Toast.LENGTH_SHORT).show();

                }

                // Friends State (Unfriend a already friends)

                if(mCurrentState.equals(strFRIENDS)){

                    mFriendsDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendRequestButton.setEnabled(true);
                                    mCurrentState = strNOT_FRIENDS;
                                    mProfileSendRequestButton.setText("Send Friend Request");

                                    mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                    mProfileDeclineRequestButton.setEnabled(false);
                                }
                            });
                        }
                    });

                    Toast.makeText(getApplicationContext(), "Unfriends Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });

        mProfileDeclineRequestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Decline Request State

                if(mCurrentState.equals(strREQUEST_RECEIVED)){
                    mFriendsRequestDatabase.child(mCurrentUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsRequestDatabase.child(userId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequestButton.setEnabled(true);
                                    mCurrentState = strNOT_FRIENDS;
                                    mProfileSendRequestButton.setText("Send Friend Request");

                                    mProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                    mProfileDeclineRequestButton.setEnabled(false);

                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
