package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardActivity extends AppCompatActivity {
    // Composants de l'interface

    private RecyclerView recyclerAdvices;
    private Button btnAddAdvice;

    // Liste pour stocker les conseils depuis Firebase
    private List<Advice> adviceList = new ArrayList<>();
    private AdviceAdapter adapter;

    private DatabaseReference advicesRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        recyclerAdvices = findViewById(R.id.recyclerAdvices);
        btnAddAdvice = findViewById(R.id.btnAddAdvice);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String currentUserId = currentUser.getUid();
        advicesRef = FirebaseDatabase.getInstance().getReference("advices");
        adapter = new AdviceAdapter(adviceList, currentUserId, new AdviceAdapter.OnAdviceActionListener() {
            @Override
            public void onEdit(Advice advice) {
                Intent intent = new Intent(DoctorDashboardActivity.this, DoctorAdviceEditActivity.class);
                intent.putExtra("adviceId", advice.id);
                startActivity(intent);
            }
            @Override
            public void onDelete(Advice advice) {
                if (advice.id != null) {
                    advicesRef.child(advice.id).removeValue()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(DoctorDashboardActivity.this, "Conseil supprimé", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(DoctorDashboardActivity.this, "Erreur suppression", Toast.LENGTH_SHORT).show());
                }
            }
        });
        recyclerAdvices.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdvices.setAdapter(adapter);
        btnAddAdvice.setOnClickListener(v -> {
            startActivity(new Intent(DoctorDashboardActivity.this, DoctorAdviceEditActivity.class));
        });

        loadAdvices();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_doctor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;

        } else if (id == R.id.menu_advice_client) {
            startActivity(new Intent(this, AdviceActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAdvices() {
        advicesRef.orderByChild("createdBy").equalTo(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adviceList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Advice advice = ds.getValue(Advice.class);
                            if (advice != null) {
                                advice.id = ds.getKey();
                                adviceList.add(advice);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DoctorDashboardActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
