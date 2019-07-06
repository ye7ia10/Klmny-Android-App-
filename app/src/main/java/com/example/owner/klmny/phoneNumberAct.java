package com.example.owner.klmny;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class phoneNumberAct extends AppCompatActivity {


    private EditText phoneNum;
    private EditText Verif;
    private Button SendVer;
    private Button verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        phoneNum = (EditText) findViewById(R.id.phoneNum);
        Verif = (EditText) findViewById(R.id.VerNum);
        SendVer = (Button) findViewById(R.id.sendVer);
        verify = (Button) findViewById(R.id.verify);


        SendVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = phoneNum.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(phoneNumberAct.this, "Enter the phone" , Toast.LENGTH_SHORT).show();
                } else {
                    phoneNum.setEnabled(false);
                    SendVer.setVisibility(View.INVISIBLE);
                    Verif.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.VISIBLE);

                }
            }
        });
    }
}
