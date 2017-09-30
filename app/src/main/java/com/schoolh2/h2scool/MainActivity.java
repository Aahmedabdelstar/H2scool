package com.schoolh2.h2scool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private EditText USERNAME_LOGIN;
    private EditText USER_PASSWORD_LOGIN;

    Button LOGIN;
    Button GO_TO_REGISTER;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlestiner;

    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        GO_TO_REGISTER = (Button) findViewById(R.id.buttonGo);
        LOGIN = (Button) findViewById(R.id.buttonLogin);
        USERNAME_LOGIN = (EditText) findViewById(R.id.editTextUsernameLogin);
        USER_PASSWORD_LOGIN = (EditText) findViewById(R.id.editTextpasswordLogin);


        mAuthlestiner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null)
                    startActivity(new Intent(MainActivity.this,profile.class));
            }
        };


        LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSignIn();
            }
        });





        GO_TO_REGISTER.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                {
                    Intent intent = new Intent(MainActivity.this, register.class);
                    startActivity(intent);
                }
            }
        });




    }

    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthlestiner);

    }



    private void startSignIn ()
    {
        String email = USERNAME_LOGIN.getText().toString().trim();
        String password = USER_PASSWORD_LOGIN.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(MainActivity.this,"Please " , Toast.LENGTH_LONG).show();

        }else
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful())
                        Toast.makeText(MainActivity.this,"sing problem " , Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}
