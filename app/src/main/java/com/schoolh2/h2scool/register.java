package com.schoolh2.h2scool;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {


    private EditText editTextClass;
    private EditText editTextCourse;
    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextMobile;
    private EditText editTextPassword;
    private EditText editTextSchool;

    private Button buttonRegister;
    private ProgressDialog progressDialog;

    private CheckBox teahcher;
    private CheckBox student;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextClass = (EditText) findViewById(R.id.editTextclass);
        editTextCourse = (EditText) findViewById(R.id.editTextCourse);
        editTextUserName = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextSchool = (EditText) findViewById(R.id.editTextschool);

        buttonRegister = (Button) findViewById(R.id.btnDone);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("schools").child("A1");

        teahcher = (CheckBox) findViewById(R.id.teacher);
        student = (CheckBox) findViewById(R.id.student);


        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (student.isChecked()) {
                    teahcher.setChecked(false);
                    editTextClass.setVisibility(View.VISIBLE);
                    editTextCourse.setVisibility(View.INVISIBLE);

                }
            }
        });
        teahcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teahcher.isChecked()) {
                    student.setChecked(false);
                    editTextClass.setVisibility(View.VISIBLE);
                    editTextCourse.setVisibility(View.VISIBLE);

                }
            }
        });


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editTextUserName.getText().toString().trim();
                String useremail = editTextEmail.getText().toString().trim();
                useremail = useremail.replaceAll("\\s+$", "");
                String usermobil = editTextMobile.getText().toString().trim();
                String userpassword = editTextPassword.getText().toString().trim();
                String userschool = editTextSchool.getText().toString().trim();
                String classes = editTextClass.getText().toString().trim();
                String courses = editTextCourse.getText().toString().trim();


                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(useremail)
                        || TextUtils.isEmpty(usermobil) || TextUtils.isEmpty(userpassword)
                        || TextUtils.isEmpty(userschool) || TextUtils.isEmpty(classes) ) {
                    if(teahcher.isChecked())
                        if(TextUtils.isEmpty(courses)) {

                            Toast.makeText(register.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                        }
                    Toast.makeText(register.this, "Please enter all fields", Toast.LENGTH_SHORT).show();

                } else {

                    progressDialog = ProgressDialog.show(register.this, "Signing up", "Please wait", true);
                    creatAnAccout(username, useremail, usermobil, userpassword, userschool ,classes , courses);

                }



            }
        });

    }


    void creatAnAccout(final String name , final String email , final String mobile ,
                       final String password , final String school , final String classes ,final String courses) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(register.this, "Successfully created the account"
                                    , Toast.LENGTH_SHORT).show();
                            String user_id = mAuth.getCurrentUser().getUid();

                            if(teahcher.isChecked()) {
                                mDatabase.child("Teachers").child(user_id).child("name").setValue(name);
                                mDatabase.child("Teachers").child(user_id).child("mobile").setValue(mobile);
                               // mDatabase.child("Teachers").child(user_id).child("school").setValue(school);
                                mDatabase.child("Teachers").child(user_id).child("classes").setValue(classes);
                                mDatabase.child("Teachers").child(user_id).child("courses").setValue(courses);
                            }else if(student.isChecked()) {
                                mDatabase.child("students").child(user_id).child("name").setValue(name);
                                mDatabase.child("students").child(user_id).child("mobile").setValue(mobile);
                                // mDatabase.child("Teachers").child(user_id).child("school").setValue(school);
                                mDatabase.child("students").child(user_id).child("classes").setValue(classes);
                            }


                        } else {
                            try {
                                progressDialog.dismiss();
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(register.this, "Password too weak"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(register.this, "Invalid email address"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(register.this, "This user already exists"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {

                                Toast.makeText(register.this, "Error" + task.getException()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }




                    }
                });
    }

 /*   void addNameAndPic(final String name , final String email,final String mobile , final String password, final String school) {

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        signIn(email, password);
                    }
                });

    }

    void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(register.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            try {
                                progressDialog.dismiss();
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(register.this, "Password too weak"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(register.this, "Invalid email address"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(register.this, "This user already exists"
                                        , Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {

                                Toast.makeText(register.this, "Error" + task.getException()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



    }

*/
}
