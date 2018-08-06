package com.morango.chat.chatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {

    TextView userName, lastSeen;
    private String messageRecieverId, messagerReciverName, messageSenderId;
    private CircleImageView userImage;
    private ImageButton sendMessage, sendImage;
    private EditText message;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private LayoutInflater layoutInflater;
    private View view;
    private ActionBar actionBar;

    private final List<Messages> messagesList = new ArrayList<>();
    private RecyclerView userMessageList;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        /* Init */
        messageRecieverId = getIntent().getStringExtra("userid").toString();
        messagerReciverName = getIntent().getStringExtra("userName").toString();

        toolbar = findViewById(R.id.mainChatToolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.custom_chat_bar, null);

        actionBar.setCustomView(view);

        userName = findViewById(R.id.userNameMessage);
        lastSeen = findViewById(R.id.userLastSeen);
        userImage = findViewById(R.id.userImageMessage);


        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();


        sendImage = findViewById(R.id.sendImage);
        sendMessage = findViewById(R.id.sendMessage);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        messageSenderId = firebaseAuth.getCurrentUser().getUid();
        message = findViewById(R.id.messageText);


        messageAdapter = new MessageAdapter(messagesList);
        userMessageList = findViewById(R.id.messageListUsers);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        FetchMessages();

        /* implementation */
        userName.setText(messagerReciverName);

        databaseReference.child("User").child(messageRecieverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = "";
                try {
                    online = dataSnapshot.child("online").getValue().toString();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                final String userThumb = dataSnapshot.child("userThumbImage").getValue().toString();

                Picasso.get()
                        .load(userThumb)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_profile)
                        .resize(100, 100)
                        .into(userImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get()
                                        .load(userThumb)
                                        .placeholder(R.drawable.default_profile)
                                        .resize(100, 100)
                                        .into(userImage);

                            }
                        });

                if (online.equals("true") && !online.isEmpty()) {
                    lastSeen.setText("Online");
                } else {

                    LastSeenTime lastSeenTime = new LastSeenTime();

                    long lastSeenTimeParse = Long.parseLong(online);

                    String lastSeemDisplayTime = "";
                    try {
                        lastSeemDisplayTime = LastSeenTime.getTimeAgo(lastSeenTimeParse).toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    lastSeen.setText(lastSeemDisplayTime);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendMessage(message.getText().toString());

            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    private void FetchMessages() {

        databaseReference.child("Messages")
                .child(messageSenderId).child(messageRecieverId)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void SendMessage(String messageText) {

        if (TextUtils.isEmpty(messageText)) {

        } else {
            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageRecieverId;
            String messageRecieverRef = "Messages/" + messageRecieverId + "/" + messageSenderId;

            DatabaseReference userMessageKey = databaseReference.child("Messages").child(messageSenderId).child(messageRecieverId).push();

            String messagePushId = userMessageKey.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("seen", false);
            messageTextBody.put("type", "text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", messageSenderId);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageRecieverRef + "/" + messagePushId, messageTextBody);


            databaseReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat Log", databaseError.getMessage().toString());
                    }

                    message.setText("");
                }
            });


        }
    }

}

