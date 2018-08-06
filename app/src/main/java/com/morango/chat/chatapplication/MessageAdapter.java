package com.morango.chat.chatapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> userMessageList;
    private FirebaseAuth firebaseAuth;

    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.messages_layout, viewGroup, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

        String onlineUserId = firebaseAuth.getCurrentUser().getUid();

        Messages messages = userMessageList.get(i);

        String fromUserId = messages.getFrom();

        if (fromUserId.equals(onlineUserId)) {
            messageViewHolder.text.setBackgroundResource(R.drawable.message_text_background_2);
            messageViewHolder.layout.setGravity(Gravity.RIGHT);
        } else {
            messageViewHolder.text.setBackgroundResource(R.drawable.message_text_background);
            messageViewHolder.layout.setGravity(Gravity.LEFT);
        }

        messageViewHolder.text.setText(messages.getMessage());

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public RelativeLayout layout;
//        public CircleImageView image;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.userMessage);
            layout = itemView.findViewById(R.id.layoutMessage);
//            image = itemView.findViewById(R.id.messageUserImage);

        }

    }
}
