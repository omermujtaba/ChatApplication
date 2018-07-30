package com.morango.chat.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail, userPassword;

    private Button loginButton;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userEmail = findViewById(R.id.userEmailL);
        userPassword = findViewById(R.id.userPasswordL);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Logging in, Please wait.");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent in = new Intent(getApplicationContext(), ChatActivity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

            }
        });


    }
}
