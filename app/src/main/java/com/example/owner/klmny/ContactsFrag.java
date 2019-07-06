package com.example.owner.klmny;


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
public class ContactsFrag extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private String CurrentUserId;
    private DatabaseReference contactsRef, UsersRef;
    private FirebaseAuth auth;
    public ContactsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.mRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid().toString();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserId);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<contacts> options =
                new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(contactsRef, contacts.class)
                .build();

        FirebaseRecyclerAdapter <contacts, ConViewHolder> adapter =
                new FirebaseRecyclerAdapter<contacts, ConViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ConViewHolder holder, int position, @NonNull final contacts model) {
                        String userid = getRef(position)
                                .getKey();
                        UsersRef.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")){
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String status = dataSnapshot.child("status").getValue().toString();


                                    holder.nameer.setText(name);
                                    holder.stat.setText(status);
                                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageView);

                                } else {

                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String status = dataSnapshot.child("status").getValue().toString();


                                    holder.nameer.setText(name);
                                    holder.stat.setText(status);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ConViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdata, parent, false);
                        ConViewHolder findfriendsviewholder = new ConViewHolder(view);
                        return findfriendsviewholder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class ConViewHolder extends RecyclerView.ViewHolder {
        TextView nameer, stat;
        CircleImageView imageView;
        public ConViewHolder(@NonNull View itemView) {
            super(itemView);
            nameer = itemView.findViewById(R.id.hisname);
            stat = itemView.findViewById(R.id.hisstat);
            imageView = itemView.findViewById(R.id.userprofile);
        }

    }
}
