package com.example.owner.klmny;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFrag extends Fragment {


    private View view;
    private RecyclerView recyclerView;
    private String CurrentId;
    private DatabaseReference UsersRef, chatsReq, contactsR;
    private FirebaseAuth auth;
    public RequestsFrag() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.reqRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auth = FirebaseAuth.getInstance();
        CurrentId = auth.getCurrentUser().getUid();
        chatsReq = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsR = FirebaseDatabase.getInstance().getReference().child("Contacts");

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<contacts> options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(chatsReq.child(CurrentId), contacts.class)
                .build();

        final FirebaseRecyclerAdapter <contacts, ReqViewHolder> adapter
                = new FirebaseRecyclerAdapter<contacts, ReqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ReqViewHolder holder, final int position, @NonNull contacts model) {
                final String userid = getRef(position)
                        .getKey();

                DatabaseReference reference = getRef(position).child("request_type").getRef();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String t = dataSnapshot.getValue().toString();
                            if (t.equals("received") && !t.equals("sent")){
                                UsersRef.child(userid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")){
                                            holder.accept.setVisibility(View.VISIBLE);
                                            holder.refuse.setVisibility(View.VISIBLE);
                                            String image = dataSnapshot.child("image").getValue().toString();
                                            String name = dataSnapshot.child("name").getValue().toString();
                                            String status = dataSnapshot.child("status").getValue().toString();


                                            holder.nameer.setText(name);
                                            holder.stat.setText(status);
                                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageView);


                                        } else {
                                            holder.accept.setVisibility(View.VISIBLE);
                                            holder.refuse.setVisibility(View.VISIBLE);
                                            String name = dataSnapshot.child("name").getValue().toString();
                                            String status = dataSnapshot.child("status").getValue().toString();


                                            holder.nameer.setText(name);
                                            holder.stat.setText(status);
                                        }

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence[] arr = new CharSequence[]
                                                        {
                                                                "Accept"
                                                                , "Refuse"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Chat Request");
                                                builder.setItems(arr, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0){
                                                            contactsR.child(CurrentId).child(userid)
                                                                    .child("Contacts").setValue("Saved")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                contactsR.child(userid).child(CurrentId)
                                                                                        .child("Contacts").setValue("Saved")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    chatsReq.child(CurrentId).child(userid)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        chatsReq.child(userid).child(CurrentId)
                                                                                                                                .removeValue()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()){
                                                                                                                                            Toast.makeText(getContext(), "Contact Added", Toast.LENGTH_SHORT).show();

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }


                                                        if (i == 1){
                                                            chatsReq.child(CurrentId).child(userid)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                chatsReq.child(userid).child(CurrentId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(), "Request Canceled", Toast.LENGTH_SHORT).show();

                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }



                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {}
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdata, parent, false);
                ReqViewHolder findfriendsviewholder = new ReqViewHolder(view);
                return findfriendsviewholder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder{
        TextView nameer, stat;
        CircleImageView imageView;
        Button accept, refuse;
        public ReqViewHolder(@NonNull View itemView) {
            super(itemView);
            nameer = itemView.findViewById(R.id.hisname);
            stat = itemView.findViewById(R.id.hisstat);
            imageView = itemView.findViewById(R.id.userprofile);
            accept = itemView.findViewById(R.id.accept);
            refuse = itemView.findViewById(R.id.refuse);
        }
    }
}
