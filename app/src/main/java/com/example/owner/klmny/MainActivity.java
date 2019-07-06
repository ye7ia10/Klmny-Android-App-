package com.example.owner.klmny;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar ntool;
    private TabLayout mtabLayout;
    private ViewPager mViewPager;
    private TabAccess tabAccess;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        ntool = (Toolbar) findViewById(R.id.main_page_bar);
        setSupportActionBar(ntool);
        getSupportActionBar().setTitle("Klmny");


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tabAccess = new TabAccess(getSupportFragmentManager());
        mViewPager.setAdapter(tabAccess);

        mtabLayout = (TabLayout) findViewById(R.id.tablayout);
        mtabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            this.finish();
            startActivity(intent);
        } else {
            VerifyUserData();
        }
    }

    private void VerifyUserData() {

        String UserID = firebaseAuth.getCurrentUser().getUid();
        reference.child("Users").child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){
                    //Toast.makeText(MainActivity.this , "Welcome to Klmny", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this , "Complete Information", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout){
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            this.finish();
            startActivity(intent);
        }
        if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.findFreinds){
            Intent intent =  new Intent(MainActivity.this, FindFriends.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.CreateGroup){
            MakeNewGroup();
        }
        return true;
    }

    private void MakeNewGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("New Group");
        final EditText grpName = new EditText(MainActivity.this);
        grpName.setHint("Group name");
        builder.setView(grpName);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupname = grpName.getText().toString();
                if (TextUtils.isEmpty(groupname)){
                    Toast.makeText(MainActivity.this, "Set A group name" , Toast.LENGTH_SHORT).show();
                } else {
                    CreateTheGroup(groupname);
                }

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateTheGroup(String grp){
        reference.child("Groups").child(grp).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Group Created successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
