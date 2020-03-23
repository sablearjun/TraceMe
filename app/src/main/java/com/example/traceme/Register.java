package com.example.traceme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    EditText name,mail,mobile;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.nameet);
        mail = findViewById(R.id.mailet);
        mobile = findViewById(R.id.mobileet);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().equals("") && !mail.getText().toString().equals("") && !mobile.getText().toString().equals("")){

                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putString("name", name.getText().toString()).apply();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putString("mail", mail.getText().toString()).apply();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putString("mobile", mobile.getText().toString()).apply();

                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}
