package com.morango.chat.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail, userPassword;

    private Button loginButton;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    private DatabaseReference userReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userEmail = findViewById(R.id.userEmailL);
        userPassword = findViewById(R.id.userPasswordL);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("User");

        progressDialog = new ProgressDialog(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser(userEmail.getText().toString(), userPassword.getText().toString());
            }
        });


    }

    private void LoginUser(String email, String password) {
        progressDialog.setMessage("Logging in, Please wait.");
        progressDialog.show();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String uID = mAuth.getCurrentUser().getUid();
                                String token = FirebaseInstanceId.getInstance().getToken();

                                userReference.child(uID).child("deviceToken").setValue(token)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent in = new Intent(getApplicationContext(), ChatActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(in);
                                                finish();
                                            }
                                        });

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }

    }
}
