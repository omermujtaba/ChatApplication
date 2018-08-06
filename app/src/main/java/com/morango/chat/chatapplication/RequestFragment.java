package com.morango.chat.chatapplication;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayout;
    DatabaseReference friendReqRef;
    DatabaseReference userRef;
    View view;
    Query query;
    FirebaseRecyclerAdapter<Requests, RequestsViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Requests> options;
    FirebaseAuth firebaseAuth;
    String onlineUserId;

    public RequestFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = view.findViewById(R.id.requestList);

        firebaseAuth = FirebaseAuth.getInstance();
        onlineUserId = firebaseAuth.getCurrentUser().getUid();

        recyclerView.setHasFixedSize(true);

        linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayout);

        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userRef.keepSynced(true);

        friendReqRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(onlineUserId);
        friendReqRef.keepSynced(true);
        query = friendReqRef;


        options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(query, Requests.class)
                .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Requests model) {

                final String listUserId = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("requestType").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();

                            if (type.equals("RECIEVED")) {
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("userName").getValue().toString();
                                        String userImage = dataSnapshot.child("userThumbImage").getValue().toString();
                                        String userStatus = dataSnapshot.child("userStatus").getValue().toString();

                                        holder.setUserName(userName);
                                        holder.setThumbImage(userImage);
                                        holder.setUserStatus(userStatus);

                                        holder.acceptReq.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(getContext(), "Accept request", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new RequestFragment.RequestsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate((R.layout.friend_request), viewGroup, false));
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View view;
        Button acceptReq, rejectReq;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            acceptReq = view.findViewById(R.id.requestAcceptButton);
            rejectReq = view.findViewById(R.id.requestRejectButton);
        }


        public void setUserName(String userName) {
            TextView name = view.findViewById(R.id.requestProfileName);
            name.setText(userName);
        }

        public void setThumbImage(final String thumbImage) {

            final CircleImageView image = view.findViewById(R.id.requestProfileImage);

            Picasso.get()
                    .load(thumbImage)
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
                                    .load(thumbImage)
                                    .placeholder(R.drawable.default_profile)
                                    .resize(100, 100)
                                    .into(image);
                        }
                    });
        }

        public void setUserStatus(String userStatus) {
            TextView status = itemView.findViewById(R.id.requestProfileStatus);
            status.setText(userStatus);
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();

    }
}
