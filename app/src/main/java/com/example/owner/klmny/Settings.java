package com.example.owner.klmny;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private CircleImageView imageView;
    private EditText UserName, Status;
    private Button updatebtn;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String CurrentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        imageView = (CircleImageView) findViewById(R.id.profile_image);
        UserName = (EditText) findViewById(R.id.UserNameSet);
        Status = (EditText) findViewById(R.id.StatusSet);
        updatebtn = (Button) findViewById(R.id.UpdateSett);


        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInfo();
            }
        });

    }

    private void UpdateInfo() {
        String Uname = UserName.getText().toString();
        String status = Status.getText().toString();
        CurrentUserID = firebaseAuth.getCurrentUser().getUid();

        if (TextUtils.isEmpty(Uname)){
            Toast.makeText(Settings.this, "Please set your userName ", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(status)){
            Toast.makeText(Settings.this, "Please set your status ", Toast.LENGTH_LONG).show();
        }
        else{
            HashMap <String , String> hashMap = new HashMap<String , String>();
            hashMap.put("userID", CurrentUserID);
            hashMap.put("name", Uname);
            hashMap.put("status", status);
            reference.child("Users").child(CurrentUserID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Settings.this, "Your profile Updated Successfully ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(Settings.this, "Error : " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
