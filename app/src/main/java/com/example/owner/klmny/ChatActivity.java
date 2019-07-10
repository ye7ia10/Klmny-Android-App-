package com.example.owner.klmny;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private String CurrentId, chaterId, chaterName;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;
    private CircleImageView imageView;
    private TextView userName, lastSeen;
    private String chaterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chaterId = getIntent().getExtras().get("user_id").toString();
        chaterName =  getIntent().getExtras().get("user_name").toString();
        chaterImage =  getIntent().getExtras().get("user_photo").toString();

        recyclerView = (RecyclerView) findViewById(R.id.messageRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();
        CurrentId = auth.getCurrentUser().getUid();

        IntializeFields();

        userName.setText(chaterName);
        Picasso.get().load(chaterImage).placeholder(R.drawable.profile_image).into(imageView);


    }

    private void IntializeFields() {
        toolbar = (Toolbar) findViewById(R.id.chatBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAct = layoutInflater.inflate(R.layout.custom_bar, null);
        getSupportActionBar().setCustomView(viewAct);

        imageView = (CircleImageView) findViewById(R.id.customPhoto);
        userName = (TextView) findViewById(R.id.custom_name);
        lastSeen = (TextView) findViewById(R.id.custom_seen);

    }
}
