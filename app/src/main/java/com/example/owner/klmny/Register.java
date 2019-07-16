package com.example.owner.klmny;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register extends AppCompatActivity {


    private EditText MailReg, PassReg;
    private Button Register;
    private TextView MoveToLgn;
    private ImageView imageView;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance().getReference();
        imageView = (ImageView) findViewById(R.id.regPhoto);
        MailReg = (EditText) findViewById(R.id.emailReg);
        PassReg = (EditText) findViewById(R.id.passwordReg);
        Register = (Button) findViewById(R.id.regBtn);
        MoveToLgn = (TextView) findViewById(R.id.have_Account);
        dialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        MoveToLgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        dialog.setTitle("Create new Account");
        dialog.setMessage("Please wait while creating your account .. ");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        String mail = MailReg.getText().toString();
        String pass = PassReg.getText().toString();
        if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Please enter your mail ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Please enter your password ", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String UserID = firebaseAuth.getCurrentUser().getUid();
                                database.child("Users").child(UserID).setValue("");
                                database.child("Users").child(UserID).child("token").setValue(deviceToken);
                                Toast.makeText(Register.this, "Your account created successfully", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                Intent intent = new Intent(Register.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                dialog.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(Register.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }
}
