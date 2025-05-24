package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtSurname, edtAge, edtEmail, edtPassword;
    private Spinner spnIllness, spnRole;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtSurname = findViewById(R.id.edtSurname);
        edtAge = findViewById(R.id.edtAge);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        spnIllness = findViewById(R.id.spnIllness);
        spnRole = findViewById(R.id.spnRole);
        btnRegister = findViewById(R.id.btnRegister);
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // Illness Spinner
        String[] illnesses = { "Diabetes", "Heart Disease", "Asthma", "Obesity"};
        ArrayAdapter<String> illnessAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, illnesses);
        spnIllness.setAdapter(illnessAdapter);

        // Role Spinner
        String[] roles = {"client", "doctor"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        spnRole.setAdapter(roleAdapter);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String surname = edtSurname.getText().toString().trim();
            String age = edtAge.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            String illness = spnIllness.getSelectedItem().toString();
            String role = spnRole.getSelectedItem().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String userId = authResult.getUser().getUid();
                        // Create user object
                        User user = new User(name, surname, age, illness,email, role);
                        databaseUsers.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Registered as " + role, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
