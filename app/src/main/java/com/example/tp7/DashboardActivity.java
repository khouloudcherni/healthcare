package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class DashboardActivity extends AppCompatActivity {

    private Button btnAdvice, btnLogout, btnpersonnaliser;
    private TextView txtAdvice;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // **Ajouter ces lignes pour récupérer les boutons !**
        btnAdvice = findViewById(R.id.btnAdvice);
        btnLogout = findViewById(R.id.btnLogout);
        btnpersonnaliser = findViewById(R.id.btnpersonnaliser);
        txtAdvice = findViewById(R.id.txtAdvice);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        txtAdvice.startAnimation(fadeIn);
        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            // Fetch user illness from Firebase
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String illness = snapshot.child("illness").getValue(String.class);
                    showPersonalizedAdvice(illness);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    txtAdvice.setText("Erreur lors du chargement de l'avis santé.");
                }
            });
        } else {
            txtAdvice.setText("Utilisateur non connecté.");
        }

        // Button to go to AdviceActivity
        btnAdvice.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AdviceActivity.class)));

        // Logout button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Personnaliser button
        btnpersonnaliser.setOnClickListener(v -> {

        });
    }

    // Inflate the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    // Handle toolbar menu item clicks and back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Handle back button in toolbar
            finish();
            return true;
        }

        if (id == R.id.action_logout) {
            mAuth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_retour) {
            startActivity(new Intent(DashboardActivity.this, AdviceActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Show health advice based on illness
    private void showPersonalizedAdvice(String illness) {
        String advice;

        switch (illness != null ? illness : "") {
            case "Diabetes":
                advice = "Follow a low-sugar diet and monitor your blood sugar regularly..";
                break;
            case "Heart Disease":
                advice = "Adopt a low-cholesterol diet and do light exercises.\n.";
                break;
            case "Asthma":
                advice = "Avoid allergens and always keep your inhaler with you.\n";
                break;
            case "Obesity":
                advice = "Engage in regular physical activity and maintain a balanced diet.\n";
                break;
            default:
                advice = "Stay healthy! Drink water, get good sleep, and eat healthy.\n";
                break;
        }

        txtAdvice.setText(advice);
    }
}
