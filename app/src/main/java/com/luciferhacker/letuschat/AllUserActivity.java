package com.luciferhacker.letuschat;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mAllUserToolbar;
    private RecyclerView mAllUserRecyclerViewList;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        mAllUserToolbar = (Toolbar) findViewById(R.id.all_user_appbar_toolbar);
        setSupportActionBar(mAllUserToolbar);
        getSupportActionBar().setTitle(strAll_User);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE);

        mAllUserRecyclerViewList = (RecyclerView) findViewById(R.id.all_user_users_list_recycler_view);
        mAllUserRecyclerViewList.setHasFixedSize(true);
        mAllUserRecyclerViewList.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.user_single_layout,
                UsersViewHolder.class,
                mUserDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, User users, int position) {
                usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumbImage());

                final String userId = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUserActivity.this, ProfileActivity.class);
                        profileIntent.putExtra(strUSER_ID, userId);
                        startActivity(profileIntent);
                    }
                });

            }
        };
        mAllUserRecyclerViewList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_layout_name_text);
            userNameView.setText(name);
        }

        public void setUserStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_layout_status_text);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumbImage) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_layout_image);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }
}