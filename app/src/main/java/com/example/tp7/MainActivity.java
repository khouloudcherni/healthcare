package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnAdvice = findViewById(R.id.btnAdvice);


        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //le health advice
        btnAdvice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdviceActivity.class);
            startActivity(intent);
        });
    }
}
