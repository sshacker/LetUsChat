package com.luciferhacker.letuschat;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements MyStringsConstant{

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.fragment_friends_friends_list_recycler_view);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child(strFRIENDS_DATABASE).child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(strUSERS_DATABASE);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.user_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        )   {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int i) {

                friendsViewHolder.setDate(friends.getDate());

                String listUserId = getRef(i).getKey();
                mUsersDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child(strName).getValue(String.class);
                        String userThumb = dataSnapshot.child(strTHUMB_IMAGE).getValue(String.class);

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb,getContext());

                        if(dataSnapshot.hasChild(strONLINE)){
                            Boolean userOnline = (Boolean) dataSnapshot.child(strONLINE).getValue();
                            friendsViewHolder.setUserOnlineStatus(userOnline);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder (View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date){
            TextView userDateView = (TextView) mView.findViewById(R.id.user_single_layout_status_text);
            userDateView.setText(date);
        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_layout_name_text);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_layout_image);
            Picasso.get().load(thumb).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        public void setUserOnlineStatus (Boolean onlineStatus){
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_layout_online_icon_image);
            if(onlineStatus == true){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
