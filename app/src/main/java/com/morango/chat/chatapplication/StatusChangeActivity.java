package com.morango.chat.chatapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatusChangeActivity extends AppCompatActivity {

    Button okButton;
    TextView status;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);

        status = findViewById(R.id.newStatus);
        okButton = findViewById(R.id.okButton);
        toolbar = findViewById(R.id.changeStatToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
