package com.morango.chat.chatapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class FriendsFragment extends Fragment {

    RecyclerView recyclerView;
    Query query;
    DatabaseReference databaseReference;
    DatabaseReference usersDatabaseReference;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Friends> options;
    FirebaseAuth mAuth;
    String onlineUserID;
    private View view;

    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = view.findViewById(R.id.friendsFragmentRecycler);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        usersDatabaseReference.keepSynced(true);
        query = databaseReference.child("Friends").child(onlineUserID);


        options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query, Friends.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                final String listUserId = getRef(position).getKey();

                usersDatabaseReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("userName").getValue().toString();
                        String userImage = dataSnapshot.child("userThumbImage").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String onlineStatus = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(onlineStatus);

                        }

                        holder.setUserName(userName);
                        holder.setUserImage(userImage);


                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{
                                        userName + "'s Profile", "Send Message"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {

                                        if (position == 0) {
                                            Intent in = new Intent(getContext(), ProfileActivity.class);
                                            in.putExtra("userid", listUserId);
                                            startActivity(in);

                                        } else if (position == 1) {

                                            if (dataSnapshot.child("online").exists()) {
                                                Intent in = new Intent(getContext(), MainChatActivity.class);
                                                in.putExtra("userid", listUserId);
                                                in.putExtra("userName", userName);
                                                startActivity(in);
                                            } else {
                                                databaseReference.child(listUserId).child("online")
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
                                    }
                                });

                                builder.show();
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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                return new FriendsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate((R.layout.all_user_display_layout), viewGroup, false));
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

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

    private static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View view;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setUserName(String userName) {
            TextView name = view.findViewById(R.id.allUserName);
            name.setText(userName);
        }

        public void setDate(String date) {
            TextView sinceFriends = view.findViewById(R.id.allUserStatus);
            sinceFriends.setText(date);
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
    }
}
