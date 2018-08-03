package com.morango.chat.chatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    private String messageRecieverId, messagerReciverName;
    private TextView userName, lastSeen;
    private CircleImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        messageRecieverId = getIntent().getStringExtra("userid").toString();
        messagerReciverName = getIntent().getStringExtra("userName").toString();

        toolbar = findViewById(R.id.mainChatToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.custom_chat_bar, null);

        actionBar.setCustomView(view);

        userName = findViewById(R.id.userNameMessage);
        lastSeen = findViewById(R.id.userLastSeen);
        userImage = findViewById(R.id.userImageMessage);

        userName.setText(messagerReciverName);


    }
}
