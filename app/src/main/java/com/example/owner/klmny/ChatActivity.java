package com.example.owner.klmny;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private String CurrentId, chaterId, chaterName;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef, rootRef;
    private CircleImageView imageView;
    private TextView userName, lastSeen;
    private String chaterImage;
    private ImageButton sendbutton;
    private EditText messageInput;

    private MessageAdapter messageAdapter;
    private message msg;
    private List<message> messages = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chaterId = getIntent().getExtras().get("user_id").toString();
        chaterName =  getIntent().getExtras().get("user_name").toString();
        chaterImage =  getIntent().getExtras().get("user_photo").toString();


        auth = FirebaseAuth.getInstance();
        CurrentId = auth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        IntializeFields();

        userName.setText(chaterName);
        Picasso.get().load(chaterImage).placeholder(R.drawable.profile_image).into(imageView);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessage();
            }
        });


    }

    private void saveMessage() {
        String message = messageInput.getText().toString();
        if (TextUtils.isEmpty(message)){

        } else {
            String MessageSenderRef = "Messages/" + CurrentId + "/" + chaterId;
            String MessageRecRef = "Messages/" + chaterId + "/" + CurrentId;

            //create key for each message
            DatabaseReference reference = rootRef.child("Messages").
                    child(CurrentId).child(chaterId).push();
            String messageKey = reference.getKey();

            Map map = new HashMap();
            map.put("message", message);
            map.put("type" , "text");
            map.put("from", CurrentId);

            Map map1 = new HashMap();
            map1.put(MessageSenderRef + "/" + messageKey , map);
            map1.put(MessageRecRef + "/" + messageKey , map);

            rootRef.updateChildren(map1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        messageInput.setText("");
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(CurrentId).child(chaterId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        message mes = dataSnapshot.getValue(message.class);
                        messages.add(mes);
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
        sendbutton = (ImageButton) findViewById(R.id.sendMsg);
        messageInput = (EditText) findViewById(R.id.inputMessage);


        messageAdapter = new MessageAdapter(messages);
        recyclerView = (RecyclerView) findViewById(R.id.messageRec);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

    }
}
