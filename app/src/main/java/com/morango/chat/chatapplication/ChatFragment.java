package com.morango.chat.chatapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    Query query;
    DatabaseReference friendsRef, userRef;
    FirebaseAuth firebaseAuth;
    String onlineUserId;
    FirebaseRecyclerAdapter<Chats, ChatViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Chats> options;
    LinearLayoutManager linearLayoutManager;
    private View view;
    private RecyclerView chatsList;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatsList = view.findViewById(R.id.chatListLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        onlineUserId = firebaseAuth.getCurrentUser().getUid();

        chatsList.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatsList.setLayoutManager(linearLayoutManager);
        friendsRef = FirebaseDatabase.getInstance().getReference();
        friendsRef.keepSynced(true);
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userRef.keepSynced(true);
        query = friendsRef.child("Friends").child(onlineUserId);

        options = new FirebaseRecyclerOptions.Builder<Chats>()
                .setQuery(query, Chats.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, ChatViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position, @NonNull Chats model) {


                final String listUserId = getRef(position).getKey();

                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("userName").getValue().toString();
                        String userImage = dataSnapshot.child("userThumbImage").getValue().toString();
                        String userStatus = dataSnapshot.child("userStatus").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String onlineStatus = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(onlineStatus);
                        }

                        holder.setUserName(userName);
                        holder.setUserImage(userImage);
                        holder.setUserStatus(userStatus);


                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (dataSnapshot.child("online").exists()) {
                                    Intent in = new Intent(getContext(), MainChatActivity.class);
                                    in.putExtra("userid", listUserId);
                                    in.putExtra("userName", userName);
                                    startActivity(in);
                                } else {
                                    friendsRef.child(listUserId).child("online")
                                            .setValue(ServerValue.TIMESTAMP)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent in = new Intent(getContext(), MainChatActivity.class);
                                                    in.putExtra("userid", listUserId);
                                                    in.putExtra("userName", userName);
                                                    startActivity(in);
                                                }
                                            });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate((R.layout.all_user_display_layout), viewGroup, false));
            }
        };

        chatsList.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setUserName(String userName) {
            TextView name = view.findViewById(R.id.allUserName);
            name.setText(userName);
        }

        public void setUserImage(final String userThumbImage) {
            final CircleImageView image = view.findViewById(R.id.allUserProfileImage);

            Picasso.get()
                    .load(userThumbImage)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_profile)
                    .resize(100, 100)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get()
                                    .load(userThumbImage)
                                    .placeholder(R.drawable.default_profile)
                                    .resize(100, 100)
                                    .into(image);
                        }
                    });

        }

        public void setUserOnline(String userOnline) {
            ImageView imageView = view.findViewById(R.id.onlineStatus);

            if (userOnline.equals("true")) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserStatus(String userStatus) {
            TextView status = itemView.findViewById(R.id.allUserStatus);
            status.setText(userStatus);
        }
    }
}
