package com.example.driverapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private Button mSignUpButton;
    private EditText mretypePassword,msignupPassword, msignUpEmail, msignUpPhoneNo,mNationalIDNo, mLastName, mFirstName;

    private static final String TAG = "SignUpActivity";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String EMAIL_ADD = "email address";
    private static final String NID = "national ID";
    private static final String PHONE_NO = "phone no";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(SignUpActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mSignUpButton = (Button) findViewById(R.id.SignUp);

        mretypePassword = (EditText) findViewById(R.id.retypePassword);
        msignupPassword = (EditText) findViewById(R.id.signupPassword);
        msignUpEmail = (EditText) findViewById(R.id.signUpEmail);
        msignUpPhoneNo = (EditText) findViewById(R.id.signUpPhoneNo);
        mNationalIDNo = (EditText) findViewById(R.id.NationalIdNo);
        mLastName = (EditText) findViewById(R.id.LastName);
        mFirstName = (EditText) findViewById(R.id.FirstName);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String retypePassword = mretypePassword.getText().toString();
                final String signupPassword = msignupPassword.getText().toString();
                final String signUpEmail = msignUpEmail.getText().toString();
                final String signUpPhoneNo = msignUpPhoneNo.getText().toString();
                final String NationalIDNo = mNationalIDNo.getText().toString();
                final String LastName = mLastName.getText().toString();
                final String FirstName = mFirstName.getText().toString();

                mAuth.createUserWithEmailAndPassword(signUpEmail,signupPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();


                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                            current_user_db.setValue(true);

                            Map<String,Object> note = new HashMap<>();
                            note.put(FIRST_NAME, FirstName);
                            note.put(LAST_NAME, LastName);
                            note.put(NID, NationalIDNo);
                            note.put(PHONE_NO, signUpPhoneNo);
                            note.put(EMAIL_ADD, signUpEmail);

                            db.collection("Users").document(user_id).set(note)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Detail Saved",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
