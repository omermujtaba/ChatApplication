package com.morango.chat.chatapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    Query query;
    FirebaseRecyclerAdapter<AllUsers, AllUserViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<AllUsers> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = findViewById(R.id.allUserToolbar);
        recyclerView = findViewById(R.id.allUserRecyclerView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        query = FirebaseDatabase.getInstance().getReference().child("User");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<AllUsers>()
                .setQuery(query, AllUsers.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllUserViewHolder holder, int position, @NonNull AllUsers model) {

                Toast.makeText(getApplicationContext(), "Biding", Toast.LENGTH_LONG).show();
                holder.setUserName(model.getUserName());
                holder.setUserName(model.getUserStatus());
                holder.setUserImage(model.getUserImage());
            }

            @NonNull
            @Override
            public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                return new AllUserViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate((R.layout.all_user_display_layout), viewGroup, false));
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        firebaseRecyclerAdapter.startListening();


    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();

    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder {
        View view;

        public AllUserViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setUserName(String userName) {
            TextView name = view.findViewById(R.id.allUserName);
            name.setText(userName);
        }

        public void setUserStatus(String userStatus) {
            TextView status = view.findViewById(R.id.allUserStatus);
            status.setText(userStatus);
        }

        public void setUserImage(String userImage) {
            CircleImageView image = view.findViewById(R.id.allUserProfileImage);
            Picasso.get().load(userImage).into(image);

        }

    }
}
