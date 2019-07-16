package com.example.owner.klmny;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.ls.LSInput;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.MessageViewHolder>{


    private List<message> messages;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;

    public MessageAdapter (List<message> messages){
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent,false);
        auth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String CurrentId = auth.getCurrentUser().getUid();
        message mess = messages.get(position);
        String type = mess.getType();
        String from = mess.getFrom();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(from);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("image")){
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).into(holder.imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (type.equals("text")){
            holder.RecMess.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.sendermessage.setVisibility(View.INVISIBLE);

            if (from.equals(CurrentId)){
                holder.sendermessage.setVisibility(View.VISIBLE);

                holder.sendermessage.setBackgroundResource(R.drawable.sender);
                holder.sendermessage.setText(mess.getMessage());
            }else {

                holder.RecMess.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);

                holder.RecMess.setBackgroundResource(R.drawable.reciever);
                holder.RecMess.setText(mess.getMessage());

            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView sendermessage, RecMess;
        private CircleImageView imageView;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermessage = (TextView) itemView.findViewById(R.id.senderMessage);
            RecMess = (TextView) itemView.findViewById(R.id.RevMessage);
            imageView = (CircleImageView) itemView.findViewById(R.id.RecImg);
        }
    }

}
