package com.example.tp7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdviceActivity extends AppCompatActivity {
    private AdviceAdapter adapter;
    private List<Advice> adviceList = new ArrayList<>();
    private ActivityResultLauncher<Intent> addAdviceLauncher;
    private DatabaseReference database;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        // Get current user ID from FirebaseAuth

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = (currentUser != null) ? currentUser.getUid() : "";
        // Initialize the ActivityResultLauncher to refresh advice list after adding
        addAdviceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadAdvices();  // Reload advice after adding
                    }
                }
        );

        // Setup Firebase database reference
        database = FirebaseDatabase.getInstance().getReference();
        Button btnPersonalize = findViewById(R.id.btnPersonalize);
        if (btnPersonalize != null) {
            btnPersonalize.setOnClickListener(v -> {
                Intent intent = new Intent(AdviceActivity.this, DashboardActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("AdviceActivity", "btnPersonalize is null. Check your layout XML or setContentView.");
        }


        adapter = new AdviceAdapter(adviceList, currentUserId, new AdviceAdapter.OnAdviceActionListener() {
            @Override
            public void onEdit(Advice advice) {
                int position = adviceList.indexOf(advice);
                if (position != -1) {
                    showEditDialog(position);
                }
            }

            @Override
            public void onDelete(Advice advice) {
                int position = adviceList.indexOf(advice);
                if (position != -1) {
                    showDeleteDialog(position);
                }
            }
        });


        //recyclerView.setAdapter(adapter);
        // Load the advice list from Firebase
        loadAdvices();
    }

    public void showDeleteDialog(int position) {
        Advice adviceToDelete = adviceList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'avis")
                .setMessage("Voulez-vous vraiment supprimer cet avis ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // Supprimer dans Firebase
                    database.child("advices").child(adviceToDelete.id).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Supprimer localement
                                adviceList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(AdviceActivity.this, "Avis supprimé", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(AdviceActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void showEditDialog(int position) {
        Advice advice = adviceList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Advice");

        // Custom layout for editing title and description
        final EditText inputTitle = new EditText(this);
        final EditText inputDescription = new EditText(this);
        inputTitle.setHint("Title");
        inputDescription.setHint("Description");

        inputTitle.setText(advice.title);
        inputDescription.setText(advice.description);

        // Use a vertical layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(inputTitle);
        layout.addView(inputDescription);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = inputTitle.getText().toString();
            String newDescription = inputDescription.getText().toString();

            // Update local list
            advice.title = newTitle;
            advice.description = newDescription;
            adviceList.set(position, advice);
            adapter.notifyItemChanged(position);

            // Update in Firebase
            database.child("advices").child(advice.id).child("title").setValue(newTitle);
            database.child("advices").child(advice.id).child("description").setValue(newDescription);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

        private void loadAdvices () {
            DatabaseReference advicesRef = FirebaseDatabase.getInstance().getReference("advices");

            advicesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    adviceList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Advice advice = dataSnapshot.getValue(Advice.class);
                        if (advice != null) {
                            adviceList.add(advice);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdviceActivity.this, "Erreur de chargement des conseils", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
