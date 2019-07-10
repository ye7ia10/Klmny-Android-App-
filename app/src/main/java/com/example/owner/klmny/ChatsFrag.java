package com.example.owner.klmny;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFrag extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference UsersRef, ChatsReqRef, ContactsRef;
    private FirebaseAuth auth;
    private String CurrentId;
    public ChatsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth = FirebaseAuth.getInstance();
        CurrentId = auth.getCurrentUser().getUid();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions <contacts> options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(ContactsRef.child(CurrentId), contacts.class)
                .build();

        FirebaseRecyclerAdapter<contacts , chatsViewHolder> adapter = new FirebaseRecyclerAdapter<contacts, chatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatsViewHolder holder, int position, @NonNull contacts model) {

                final String UserId = getRef(position).getKey();
                UsersRef.child(UserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      if (dataSnapshot.exists()){
                          final String []image = {"default"};
                          final String name = dataSnapshot.child("name").getValue().toString();
                          String status = dataSnapshot.child("status").getValue().toString();
                          if (dataSnapshot.hasChild("image")){
                              image[0] = dataSnapshot.child("image").getValue().toString();
                              holder.nameer.setText(name);
                              holder.stat.setText("Last Seen: \n" + "Date "  + "Time");
                              Picasso.get().load(image[0]).placeholder(R.drawable.profile_image).into(holder.imageView);

                          } else {
                              holder.nameer.setText(name);
                              holder.stat.setText("Last Seen: \n" + "Date "  + "Time");
                          }

                          holder.itemView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View view) {
                                  Intent intent = new Intent(getContext(), ChatActivity.class);
                                  intent.putExtra("user_name", name);
                                  intent.putExtra("user_id", UserId);
                                  intent.putExtra("user_photo", image[0]);
                                  startActivity(intent);
                              }
                          });
                      }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdata, parent, false);
                chatsViewHolder findfriendsviewholder = new chatsViewHolder(view);
                return findfriendsviewholder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder{


        TextView nameer, stat;
        CircleImageView imageView;
        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameer = itemView.findViewById(R.id.hisname);
            stat = itemView.findViewById(R.id.hisstat);
            imageView = itemView.findViewById(R.id.userprofile);
        }

    }
}
