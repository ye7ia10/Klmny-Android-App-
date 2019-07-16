package com.example.owner.klmny;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileInfo extends AppCompatActivity {

    private String user_name_id;
    private TextView stat, namm;
    private CircleImageView imageView;
    private Button buttonSend, decline;
    private DatabaseReference reference, chatRef, contacts, nots;
    private String CurrentID;
    private FirebaseAuth auth;
    private String currentStl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        auth = FirebaseAuth.getInstance();

        user_name_id = getIntent().getExtras().get("user_name_id").toString();
        CurrentID = auth.getCurrentUser().getUid();
        currentStl = "new";
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contacts = FirebaseDatabase.getInstance().getReference().child("Contacts");
        nots = FirebaseDatabase.getInstance().getReference().child("Notis");


        namm = (TextView) findViewById(R.id.profName);
        stat = (TextView) findViewById(R.id.profStat);
        imageView = (CircleImageView) findViewById(R.id.profileImg);
        buttonSend = (Button) findViewById(R.id.sendTo);
        decline = (Button) findViewById(R.id.decline);


        RetrieveUserData();


    }

    private void RetrieveUserData() {
        reference.child(user_name_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("image")){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();


                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(imageView);
                        namm.setText(name);
                        stat.setText(status);

                        SendMsgReq();

                    } else {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();


                        namm.setText(name);
                        stat.setText(status);
                        SendMsgReq();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendMsgReq() {

        chatRef.child(CurrentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_name_id)){
                    String type = dataSnapshot.child(user_name_id).child("request_type").getValue().toString();
                    if (type.equals("sent")){
                        currentStl = "requested";
                        buttonSend.setEnabled(true);
                        buttonSend.setText("Cancel Chat Request");
                    } else if (type.equals("received")){
                        currentStl = "received";
                        buttonSend.setText("Accept Request");
                        decline.setText("Cancel Request");
                        decline.setVisibility(View.VISIBLE);
                        decline.setEnabled(true);
                        decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelTheReq();
                            }
                        });

                    }
                } else {

                    contacts.child(CurrentID).child(user_name_id).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        currentStl = "friends";
                                        buttonSend.setText("Remove Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!CurrentID.equals(user_name_id)){

            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonSend.setEnabled(false);
                    if (currentStl.equals("new")){
                        sendTheReq();
                    } else if (currentStl.equals("requested")){
                        cancelTheReq();
                    } else if (currentStl.equals("received")){
                        AcceptChatReq();
                    } else if (currentStl.equals("friends")){
                        RemoveCon();
                    }
                }
            });

        } else {

            buttonSend.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveCon() {
        contacts.child(CurrentID).child(user_name_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contacts.child(user_name_id).child(CurrentID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                currentStl = "new";
                                                buttonSend.setEnabled(true);
                                                buttonSend.setText("Send Message");
                                                decline.setVisibility(View.INVISIBLE);
                                                decline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatReq() {
        contacts.child(CurrentID).child(user_name_id)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         contacts.child(user_name_id).child(CurrentID)
                                 .child("Contacts").setValue("Saved")
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             chatRef.child(CurrentID).child(user_name_id)
                                                     .removeValue()
                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()) {
                                                                 chatRef.child(user_name_id).child(CurrentID)
                                                                         .removeValue()
                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                 if (task.isSuccessful()){
                                                                                     currentStl = "new";
                                                                                     buttonSend.setEnabled(true);
                                                                                     buttonSend.setText("Remove Contact");
                                                                                     decline.setVisibility(View.INVISIBLE);
                                                                                     decline.setEnabled(false);
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

    private void cancelTheReq() {

        chatRef.child(CurrentID).child(user_name_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRef.child(user_name_id).child(CurrentID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    currentStl = "new";
                                                    buttonSend.setEnabled(true);
                                                    buttonSend.setText("Send Message");
                                                    decline.setVisibility(View.INVISIBLE);
                                                    decline.setEnabled(false);
                                                }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendTheReq() {
        chatRef.child(CurrentID).child(user_name_id).child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRef.child(user_name_id).child(CurrentID).child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("from", CurrentID);
                                                map.put("type", "request");
                                                nots.child(user_name_id).push()
                                                        .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                currentStl = "requested";
                                                                buttonSend.setEnabled(true);
                                                                buttonSend.setText("Cancel Chat Request");
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
