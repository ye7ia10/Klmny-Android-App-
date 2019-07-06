package com.example.owner.klmny;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private CircleImageView imageView;
    private EditText UserName, Status;
    private Button updatebtn;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String CurrentUserID;
    private static final int GalleryPick = 1;
    private StorageReference storageReference;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");


        imageView = (CircleImageView) findViewById(R.id.profile_image);
        UserName = (EditText) findViewById(R.id.UserNameSet);
        Status = (EditText) findViewById(R.id.StatusSet);
        updatebtn = (Button) findViewById(R.id.UpdateSett);
        loadingBar = new ProgressDialog(this);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateInfo();
            }
        });
        CurrentUserID = firebaseAuth.getCurrentUser().getUid();

        getExistingData();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GalleryPick);
            }
        });
    }

    @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                loadingBar.setMessage("please wait while uploading the image");
                loadingBar.setTitle("Updating the photo");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri = result.getUri();
                StorageReference ref = storageReference.child(CurrentUserID +".jpg");

                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Settings.this, "Photo uploaded successfuly", Toast.LENGTH_SHORT).show();
                            String DomnloadUrl = task.getResult().getDownloadUrl().toString();
                            reference.child("Users").child(CurrentUserID).child("image")
                                    .setValue(DomnloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Settings.this, "Photo saved to reference", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else  {
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(Settings.this, "Error occured while uploading", Toast.LENGTH_SHORT).show();
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }

    }

    private void getExistingData() {

        reference.child("Users").child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")){

                            String naming = dataSnapshot.child("name").getValue().toString();
                            String stating = dataSnapshot.child("status").getValue().toString();
                            String imaging = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(imaging).into(imageView);
                            UserName.setText(naming);
                            Status.setText(stating);

                        } else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")){
                            String naming = dataSnapshot.child("name").getValue().toString();
                            String stating = dataSnapshot.child("status").getValue().toString();
                            //String naming = dataSnapshot.child("name").getValue().toString();
                            UserName.setText(naming);
                            Status.setText(stating);
                        } else {
                            Toast.makeText(Settings.this, "Set and Update your info ..", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
            HashMap <String , Object> hashMap = new HashMap<String , Object>();
            hashMap.put("userID", CurrentUserID);
            hashMap.put("name", Uname);
            hashMap.put("status", status);

            reference.child("Users").child(CurrentUserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
