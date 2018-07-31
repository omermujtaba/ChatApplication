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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText userEmail, userPassword, userName;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.userEmailR);
        userPassword = findViewById(R.id.userPasswordR);
        userName = findViewById(R.id.userNameR);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RegisterAccount(userEmail.getText().toString(), userPassword.getText().toString());

            }
        });


    }

    private void RegisterAccount(String email, String password) {

        progressDialog.setMessage("Please wati, while we are creating an account for you");
        progressDialog.show();

        if (TextUtils.isEmpty(email)) {

        } else if (TextUtils.isEmpty(password)) {

        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent in = new Intent(getApplicationContext(), ChatActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }


    }
}
