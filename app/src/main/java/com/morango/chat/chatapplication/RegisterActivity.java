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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText userEmail, userPassword, userName;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private Button registerButton;
    DatabaseReference databaseReference;

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

                RegisterAccount(userName.getText().toString(), userEmail.getText().toString(), userPassword.getText().toString());

            }
        });


    }

    private void RegisterAccount(final String name, final String email, final String password) {

        progressDialog.setMessage("Please wati, while we are creating an account for you");
        progressDialog.show();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_LONG).show();

        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
                        databaseReference.child("userName").setValue(name);
                        databaseReference.child("userEmail").setValue(email);
                        databaseReference.child("userPassword").setValue(password);
                        databaseReference.child("userStatus").setValue("Default status");
                        databaseReference.child("userImage").setValue("Default");
                        databaseReference.child("userThumbImage").setValue("Default").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent in = new Intent(getApplicationContext(), ChatActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                }

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
