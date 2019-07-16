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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login extends AppCompatActivity {

    private ScrollView scrollView;
    private ImageView  imageView;
    private EditText EmailLogin;
    private EditText PassLogin;
    private TextView forgetPass, MovetoReg;
    private Button LoginMail, LoginPhone;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        scrollView = (ScrollView) findViewById(R.id.scrollogin);
        imageView = (ImageView) findViewById(R.id.loginPhoto);
        EmailLogin = (EditText) findViewById(R.id.emailLogin);
        PassLogin = (EditText) findViewById(R.id.passwordLogin);
        forgetPass = (TextView) findViewById(R.id.forget_pass);
        MovetoReg = (TextView) findViewById(R.id.new_Account);
        LoginMail = (Button) findViewById(R.id.lgnBtn);
        LoginPhone = (Button) findViewById(R.id.phoneLgn);

        dialog = new ProgressDialog(this);

        MovetoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        LoginMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginAccount();
            }
        });


        LoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, phoneNumberAct.class);
                startActivity(intent);
            }
        });

    }

    private void LoginAccount() {

        dialog.setTitle("Sign in");
        dialog.setMessage("Please wait while signing your account .. ");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        String mail = EmailLogin.getText().toString();
        String pass = PassLogin.getText().toString();
        if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Please enter your mail ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Please enter your password ", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                String CuurentId = firebaseAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                reference.child(CuurentId).child("token")
                                        .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        Toast.makeText(Login.this, "You logged in Successfully ", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }else {
                                dialog.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(Login.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


}
