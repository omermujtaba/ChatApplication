package com.morango.chat.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    Toolbar toolbar;

    ViewPager mViewPager;
    TabLayout tabLayout;

    TabsPagerAdapter tabsPagerAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chatToolBar);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {

            String onlineUserID = mAuth.getCurrentUser().getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("User").child(onlineUserID);
        }

        mViewPager = findViewById(R.id.chatTabsPager);
        tabLayout = findViewById(R.id.chatTabLayout);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(tabsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ZoyaChat");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser == null) {
            LogoutUser();
        } else if (mCurrentUser != null) {
            databaseReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCurrentUser != null) {
            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void LogoutUser() {

        Intent in = new Intent(this, StartPageActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.settingButtonC) {
            Intent in = new Intent(this, SettingActivity.class);
            startActivity(in);
        }

        if (item.getItemId() == R.id.logoutButtonC) {
            if (mCurrentUser != null) {
                databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            mAuth.signOut();
            LogoutUser();
        }
        if (item.getItemId() == R.id.allUserButtonC) {
            Intent in = new Intent(this, AllUsersActivity.class);
            startActivity(in);
        }
        return true;
    }
}
