package com.example.owner.klmny;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        toolbar = (Toolbar) findViewById(R.id.findbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<contacts> options =
        new FirebaseRecyclerOptions.Builder<contacts>()
        .setQuery(reference, contacts.class)
                .build();

        FirebaseRecyclerAdapter <contacts, findfriendsviewholder> adapter = new FirebaseRecyclerAdapter<contacts, findfriendsviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findfriendsviewholder holder, final int position, @NonNull contacts model) {
                holder.nameer.setText(model.getName());
                holder.stat.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.imageView);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String user_name_id = getRef(position).getKey();
                        Intent intent = new Intent(FindFriends.this, profileInfo.class);
                        intent.putExtra("user_name_id", user_name_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public findfriendsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdata, parent, false);
                findfriendsviewholder findfriendsviewholder = new findfriendsviewholder(view);
                return findfriendsviewholder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public class findfriendsviewholder extends RecyclerView.ViewHolder{

        TextView nameer, stat;
        CircleImageView imageView;

        public findfriendsviewholder(@NonNull View itemView) {
            super(itemView);

            nameer = itemView.findViewById(R.id.hisname);
            stat = itemView.findViewById(R.id.hisstat);
            imageView = itemView.findViewById(R.id.userprofile);
        }
    }
}
