package com.example.owner.klmny;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChat extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText messsage;
    private ScrollView scrollView;
    private ImageButton imageButton;
    private TextView textView;
    private String gpname, CurrentUserName, CurrentUserID, CurrentTime, currentDate;
    private FirebaseAuth auth;
    private DatabaseReference  reference , grpRef, MessageKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        gpname = getIntent().getExtras().get("namer").toString();

        grpRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(gpname);

        toolbar = (Toolbar) findViewById(R.id.AppBarlay);
        setSupportActionBar(toolbar);
        toolbar.setTitle(gpname);

        messsage = (EditText) findViewById(R.id.message);
        scrollView = (ScrollView) findViewById(R.id.my_scroll);
        imageButton = (ImageButton) findViewById(R.id.sendBtn);
        textView = (TextView) findViewById(R.id.displayText);

        getUserInfo();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageIntoDataBase();
                messsage.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        grpRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }

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

    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator it = dataSnapshot.getChildren().iterator();
        while (it.hasNext()){

            String date = (String) (((DataSnapshot)it.next()).getValue());
            String message = (String) (((DataSnapshot)it.next()).getValue());
            String name = (String) (((DataSnapshot)it.next()).getValue());
            String time = (String) (((DataSnapshot)it.next()).getValue());

            textView.append(name + ": \n" + message + "\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }

    private void getUserInfo() {
        reference.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    CurrentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SaveMessageIntoDataBase(){
        String text = messsage.getText().toString();
        String messagekey = grpRef.push().getKey();
        if (TextUtils.isEmpty(text)){

        } else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = dateFormat.format(c.getTime());
            Calendar cc = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            CurrentTime = dateFormat.format(c.getTime());

            HashMap<String, Object> map  = new HashMap<>();

            grpRef.updateChildren(map);

            MessageKey = grpRef.child(messagekey);
            HashMap<String, Object> mapMess  = new HashMap<>();

            mapMess.put("name", CurrentUserName);
            mapMess.put("message", text);
            mapMess.put("date", currentDate);
            mapMess.put("time", CurrentTime);

            MessageKey.updateChildren(mapMess);


        }
    }
}
