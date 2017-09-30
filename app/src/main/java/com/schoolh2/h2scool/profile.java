package com.schoolh2.h2scool;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class profile extends AppCompatActivity {

    private TextView name;
    private TextView classes;
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ListView muser_list;

    ArrayList<String> userNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = (TextView) findViewById(R.id.studentName);
        classes = (TextView) findViewById(R.id.studentClass);


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

        mAuth.addAuthStateListener(mAuthStateListener);

        String aa = mAuth.getCurrentUser().getUid();
      // name.setText(aa);

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("schools").child("A1").child("students").child(aa);
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String name1= dataSnapshot.child("name").getValue(String.class);
                String classes1= dataSnapshot.child("classes").getValue(String.class);

                name.setText(name1);
                classes.setText(classes1);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });





    }
}
