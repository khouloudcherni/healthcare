package com.example.tp7;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
public class DoctorAdviceEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText edtTitle, edtDescription;
    private Button btnSave, btnUploadImage;
    private Uri imageUri;
    private DatabaseReference advicesRef;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_advice_edit); // Ensure this layout exists
        advicesRef = FirebaseDatabase.getInstance().getReference("advices");
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        edtTitle = findViewById(R.id.edtAdviceTitle);
        edtDescription = findViewById(R.id.edtAdviceDescription);
        btnSave = findViewById(R.id.btnSaveAdvice);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        // Firebase
        advicesRef = FirebaseDatabase.getInstance().getReference("advices");
        storageRef = FirebaseStorage.getInstance().getReference("advice_images");

        btnUploadImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveAdvice());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAdvice() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Titre et description requis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            saveAdviceToDatabase(title, description, userId, uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur lors de l'upload de l'image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveAdviceToDatabase(title, description, userId, null);
        }
    }
    private void saveAdviceToDatabase(String title, String description, String userId, String imageUrl) {
        // Créer une clé unique
        String id = advicesRef.push().getKey();

        // Créer l'objet Advice
        Advice advice = new Advice(title, description, userId, imageUrl);
        DatabaseReference advicesRef = FirebaseDatabase.getInstance().getReference("advices");
        advicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Advice> adviceList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Advice advice = dataSnapshot.getValue(Advice.class);
                    adviceList.add(advice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorAdviceEditActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });

        // Enregistrer dans "advices"
        advicesRef.child(id).setValue(advice).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Enregistrer dans "activityadvice" avec la même clé
                DatabaseReference activityAdviceRef = FirebaseDatabase.getInstance().getReference("activityadvice");
                activityAdviceRef.child(id).setValue(advice).addOnCompleteListener(activityTask -> {
                    if (activityTask.isSuccessful()) {
                        Toast.makeText(this, "Conseil enregistré dans les deux nœuds", Toast.LENGTH_SHORT).show();
                        // Exemple : revenir à la page précédente
                        finish(); } else {
                        Toast.makeText(this, "Erreur lors de l'enregistrement dans activityadvice", Toast.LENGTH_LONG).show();
                        activityTask.getException().printStackTrace();
                    }
                });
            } else {
                Toast.makeText(this, "Erreur lors de l'enregistrement dans advices", Toast.LENGTH_LONG).show();
                task.getException().printStackTrace();
            }
        });
    }


}
