package com.example.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.quizzapp.Adapters.GrideAdapter;
import com.example.quizzapp.databinding.ActivitySetQuestionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SetQuestionActivity extends AppCompatActivity {

    ActivitySetQuestionBinding binding;
    FirebaseDatabase database;

    GrideAdapter adapter;

    int a = 1;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new GrideAdapter(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("category"), key, new GrideAdapter.GridListener() {
            @Override
            public void addSets() {
                database.getReference().child("Registered users").child(currentUserUid).child("categories").child(key)
                        .child("setNum").setValue(getIntent().getIntExtra("sets", 0) + a)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    adapter.sets++;
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SetQuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                a++; // Increment a after the database operation
            }
        });

        binding.gridView.setAdapter(adapter);
    }
}
