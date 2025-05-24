package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
//intialisation firebase
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            //acceder a firebase
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

                            userRef.child("role").get().addOnSuccessListener(snapshot -> {
                                String role = snapshot.getValue(String.class);

                                if ("doctor".equalsIgnoreCase(role)) {
                                    startActivity(new Intent(LoginActivity.this, DoctorDashboardActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, AdviceActivity.class));
                                }
                                finish();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to get user role", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}
