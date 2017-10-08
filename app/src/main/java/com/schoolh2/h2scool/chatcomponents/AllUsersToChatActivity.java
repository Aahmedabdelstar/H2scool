package com.schoolh2.h2scool.chatcomponents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.schoolh2.h2scool.MainActivity;
import com.schoolh2.h2scool.R;

/**
 * Created by MMenem on 10/5/2017.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AllUsersToChatActivity extends AppCompatActivity {


    private FirebaseUser user;


    private DatabaseReference usersDatabase;


    private RecyclerView usersList;


    private String userId;
    private String userRecMessage;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_to_chat);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                current_user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = current_user.getUid();

                    //    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Intent i = new Intent(AllUsersToChatActivity.this, MainActivity.class);
                    startActivity(i);


                }
                // ...
            }
        };


        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        usersList = (RecyclerView) findViewById(R.id.chat_list);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));


        fetchHollasList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //  current_user.addAuthStateListener(mAuthListener);
        fetchHollasList();

    }


    private void fetchHollasList() {
        FirebaseRecyclerAdapter<AllUsers, HollaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, HollaViewHolder>
                (AllUsers.class, R.layout.chat_list_row, HollaViewHolder.class,
                        usersDatabase) {

            @Override
            protected void populateViewHolder(HollaViewHolder hollaViewHolder,
                                              AllUsers users, final int i) {
                userRecMessage = getRef(i).getKey();
                hollaViewHolder.setUserName(users.getName());
                hollaViewHolder.setUserImage(getApplicationContext(), users.getImage());
                hollaViewHolder.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // start New Message
                        startAnewMessage();
                    }
                });


            }


        };

        usersList.setAdapter(firebaseRecyclerAdapter);

    }

    private void startAnewMessage() {
        Intent userProfileIntent = new Intent(AllUsersToChatActivity.this, ChatConversationActivity.class);
        userProfileIntent.putExtra("userRecMessage", userRecMessage);
        startActivity(userProfileIntent);
    }


    public static class HollaViewHolder extends RecyclerView.ViewHolder {
        View mView, hollaRequestDivider;
        FloatingActionButton btnMessage;


        public HollaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.hollaRequestDivider = (View) mView.findViewById(R.id.chat_request_divider);
            this.btnMessage = (FloatingActionButton) mView.findViewById(R.id.btnMessage);


        }


        public void setUserName(String name) {
            TextView friend_user_name = (TextView) mView.findViewById(R.id.user_row_user_name);
            friend_user_name.setText(name);
        }


        public void setUserImage(final Context context, final String image) {
            final ImageView friend_user_image = (ImageView) mView.findViewById(R.id.user_row_user_image);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(friend_user_image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(friend_user_image);
                }
            });
        }


    }
}