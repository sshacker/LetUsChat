package com.luciferhacker.letuschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mToolbar;
    private String mChatUserId;
    private FirebaseUser mCurrentUser;
    private TextView mProfileName;
    private TextView mLastSeen;
    private CircleImageView mProfileImage;
    private DatabaseReference mRootReferenceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRootReferenceDatabase = FirebaseDatabase.getInstance().getReference();

        mChatUserId = getIntent().getStringExtra(strUSER_ID);

        mToolbar = (Toolbar)findViewById(R.id.chat_appbar_include);
        setSupportActionBar(mToolbar);
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayHomeAsUpEnabled(true);
        customActionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(mChatUserId);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.chat_custom_appbar, null);
        customActionBar.setCustomView(customActionBarView);

        // CUSTOM ACTION BAR ITEM
        mProfileName = (TextView)findViewById(R.id.chatCustomAppbar_profileName_textView);
        mLastSeen = (TextView)findViewById(R.id.chatCustomAppbar_lastSeen_textView);
        mProfileImage = (CircleImageView) findViewById(R.id.chatCustomAppbar_profileImage_circleImageView);

        mRootReferenceDatabase.child(strUSERS_DATABASE).child(mChatUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(mCurrentUser != null) {

                    String online = dataSnapshot.child(strONLINE).getValue().toString();
                    String image = dataSnapshot.child(strIMAGE).getValue().toString();
                    String name = dataSnapshot.child(strNAME).getValue().toString();

                    mProfileName.setText(name);
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                    if (online.equals(strTRUE)) {
                        mLastSeen.setText(strONLINE);

                    } else {

                        GetTimeAgo getTimeAgoObject = new GetTimeAgo();
                        long lastTime = Long.parseLong(online);
                        String lastSeenTime = getTimeAgoObject.getTimeAgo(lastTime, getApplicationContext());
                        mLastSeen.setText(lastSeenTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*=========================
        mRootReferenceDatabase = FirebaseDatabase.getInstance().getReference();
        mRootReferenceDatabase.child(strUSERS_DATABASE).child(mChatUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chatUserName = dataSnapshot.child(strNAME).getValue().toString();
                getSupportActionBar().setTitle(chatUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        =============================*/

    }
}
