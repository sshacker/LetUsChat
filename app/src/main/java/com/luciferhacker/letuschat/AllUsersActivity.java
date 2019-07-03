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


public class AllUsersActivity extends AppCompatActivity implements MyStringsConstant {

    private Toolbar mAllUsersToolbar;
    private RecyclerView mAllUsersRecyclerViewList;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);


        mAllUsersToolbar = (Toolbar) findViewById(R.id.user_appBar);
        setSupportActionBar(mAllUsersToolbar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE);

        mAllUsersRecyclerViewList = (RecyclerView) findViewById(R.id.user_list);
        mAllUsersRecyclerViewList.setHasFixedSize(true);
        mAllUsersRecyclerViewList.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
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
                        Intent profileIntent =new Intent(AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra(strUSER_ID, userId);
                        startActivity(profileIntent);
                    }
                });

            }
        };
        mAllUsersRecyclerViewList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumbImage) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }
}