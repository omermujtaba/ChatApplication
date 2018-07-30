package com.morango.chat.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView userImage;
    TextView userStatus, userName;

    Button changStatus, changeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userImage = findViewById(R.id.userImage);
        userStatus = findViewById(R.id.userStatus);
        userName = findViewById(R.id.userName);
        changStatus = findViewById(R.id.changeStatusButton);
        changeImage = findViewById(R.id.changeImageButton);


        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        changStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), StatusChangeActivity.class);
                startActivity(in);
            }
        });

    }
}
