package com.morango.chat.chatapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    final static String NOT_FRIENDS = "NOT_FRIENDS";
    final static String REQUEST_SENT = "REQUEST_SENT";
    final static String REQUEST_RECIEVED = "REQUEST_RECIEVED";
    final static String FRIENDS = "FRIENDS";
    Toolbar toolbar;
    DatabaseReference databaseReference;
    DatabaseReference friendRequestReference;
    DatabaseReference friendReference;
    DatabaseReference notificationReference;

    FirebaseAuth mAuth;
    String recieverUId;
    String currentUId;
    String CURRENT_STATE;
    private CircleImageView userImage;
    private TextView userStatus, userName;
    private Button sendRequest, declineRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getStringExtra("userid") != null) {
            recieverUId = getIntent().getStringExtra("userid");
        }


        currentUId = mAuth.getCurrentUser().getUid();

        userImage = findViewById(R.id.profileUserImage);
        userName = findViewById(R.id.profileUserName);
        userStatus = findViewById(R.id.profileUserStatus);
        sendRequest = findViewById(R.id.sendRequestButton);
        declineRequest = findViewById(R.id.cancelRequestButton);
        toolbar = findViewById(R.id.profileToolbar);
        CURRENT_STATE = NOT_FRIENDS;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");

        databaseReference.keepSynced(true);
        friendRequestReference.keepSynced(true);
        friendReference.keepSynced(true);
        notificationReference.keepSynced(true);


        databaseReference.child(recieverUId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("userName").getValue().toString());
                userStatus.setText(dataSnapshot.child("userStatus").getValue().toString());
                Picasso.get().load(dataSnapshot.child("userThumbImage").getValue().toString()).placeholder(R.drawable.default_profile).into(userImage);

                friendRequestReference.child(currentUId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(recieverUId)) {
                                String requestType = dataSnapshot.child(recieverUId).child("requestType").getValue().toString();

                                if (requestType.equals("SENT")) {
                                    CURRENT_STATE = REQUEST_SENT;
                                    sendRequest.setText("Cancel request");
                                    declineRequest.setVisibility(View.INVISIBLE);
                                    declineRequest.setEnabled(false);

                                } else if (requestType.equals("RECIEVED")) {
                                    CURRENT_STATE = REQUEST_RECIEVED;
                                    sendRequest.setText("Accept request");

                                    declineRequest.setVisibility(View.VISIBLE);
                                    declineRequest.setEnabled(true);

                                    declineRequest.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DeclineRequest();
                                        }
                                    });

                                }
                            }

                        } else {
                            friendReference.child(currentUId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.hasChild(recieverUId)) {
                                            CURRENT_STATE = FRIENDS;
                                            sendRequest.setText("Unfriend");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        declineRequest.setVisibility(View.INVISIBLE);
        declineRequest.setEnabled(false);


        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest.setEnabled(false);

                switch (CURRENT_STATE) {
                    case NOT_FRIENDS:
                        SendRequestToFriend();
                        break;
                    case REQUEST_SENT:
                        CancelFriendRequest();
                        break;
                    case REQUEST_RECIEVED:
                        AcceptFriendRequest();
                        break;
                    case FRIENDS:
                        Unfriend();
                    default:
                        return;
                }

            }
        });

    }

    private void DeclineRequest() {

        friendRequestReference.child(currentUId).child(recieverUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestReference.child(recieverUId).child(currentUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendRequest.setEnabled(true);
                                CURRENT_STATE = NOT_FRIENDS;
                                sendRequest.setText("Send Request");
                                DisableDeclineButton();
                            }
                        }
                    });
                }
            }
        });

    }

    private void Unfriend() {

        friendReference.child(currentUId).child(recieverUId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendReference.child(recieverUId).child(currentUId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendRequest.setEnabled(true);
                                                CURRENT_STATE = NOT_FRIENDS;
                                                sendRequest.setText("Send request");
                                                DisableDeclineButton();
                                            }
                                        }
                                    });

                        }
                    }
                });


    }

    private void AcceptFriendRequest() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        final String currentDate = dateFormat.format(calendar.getTime());

        friendReference.child(currentUId).child(recieverUId).setValue(currentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendReference.child(recieverUId).child(currentUId).setValue(currentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestReference.child(currentUId).child(recieverUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    friendRequestReference.child(recieverUId).child(currentUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                sendRequest.setEnabled(true);
                                                                CURRENT_STATE = FRIENDS;
                                                                sendRequest.setText("Unfriend");
                                                                DisableDeclineButton();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                });


    }

    private void CancelFriendRequest() {
        friendRequestReference.child(currentUId).child(recieverUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestReference.child(recieverUId).child(currentUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendRequest.setEnabled(true);
                                CURRENT_STATE = NOT_FRIENDS;
                                sendRequest.setText("Send Request");
                                DisableDeclineButton();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendRequestToFriend() {

        friendRequestReference.child(currentUId).child(recieverUId).child("requestType").setValue("SENT")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        friendRequestReference.child(recieverUId).child(currentUId).child("requestType").setValue("RECIEVED")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            HashMap<String, String> notificationData = new HashMap<String, String>();
                                            notificationData.put("from", currentUId);
                                            notificationData.put("type", "REQUEST");

                                            notificationReference.child(recieverUId).push().setValue(notificationData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                sendRequest.setEnabled(true);
                                                                CURRENT_STATE = "REQUEST_SENT";
                                                                sendRequest.setText("Cancel Request");

                                                                DisableDeclineButton();
                                                            }
                                                        }
                                                    });

                                        }


                                    }
                                });

                    }
                });
    }

    private void DisableDeclineButton() {
        declineRequest.setVisibility(View.INVISIBLE);
        declineRequest.setEnabled(false);
    }


}
