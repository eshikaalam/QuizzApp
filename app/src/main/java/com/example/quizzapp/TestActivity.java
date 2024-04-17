package com.example.quizzapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.Adapters.CategoryAdapter;
import com.example.quizzapp.Models.CategoryModel;
import com.example.quizzapp.databinding.ActivityTestBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class TestActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Toolbar toolbar;

    ActivityTestBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    CircleImageView categoryImage;
    EditText inputCategoryName;
    Button uploadCategory;
    View fetchImage;
    Uri imageUri;
    Dialog dialog;
    int i = 0;
    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        list = new ArrayList<>();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_add_category_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("please wait");

        uploadCategory = dialog.findViewById(R.id.btnUpload);
        inputCategoryName = dialog.findViewById(R.id.inputCategoryName);
        categoryImage = dialog.findViewById(R.id.categoryimage);
        fetchImage = dialog.findViewById(R.id.fetchImage);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.testRecyclerView.setLayoutManager(layoutManager);

        adapter = new CategoryAdapter(this, list);
        binding.testRecyclerView.setAdapter(adapter);

        // Get the current user's UID
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.getReference().child("Registered users").child(currentUserUid)
                .child("categories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear(); // Clear the list before adding new data
                        if (snapshot.exists()) {

                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                list.add(new CategoryModel(
                                   dataSnapshot.child("categoryName").getValue().toString(),
                                   dataSnapshot.child("categoryImage").getValue().toString(),
                                   dataSnapshot.getKey(),
                                   Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                                ));
                            }
                            updateUI(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TestActivity.this, "Category not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateUI(ArrayList<CategoryModel> list) {
                        adapter = new CategoryAdapter(TestActivity.this, list);
                        binding.testRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    dialog.show();
                } else {

                    Toast.makeText(TestActivity.this, "Please log in to add categories", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputCategoryName.getText().toString();

                if (imageUri == null) {
                    Toast.makeText(TestActivity.this, "Please Upload Category image", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    inputCategoryName.setError("Enter category name");
                } else {
                    progressDialog.show();
                    uploadData(currentUserUid);
                }
            }
        });
    }

    private void uploadData(String currentUserUid) {
        final StorageReference reference = storage.getReference().child("category")
                .child(new Date().getTime() + "");

        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setCategoryName(inputCategoryName.getText().toString());
                        categoryModel.setSetNum(0);
                        categoryModel.setCategoryImage(uri.toString());

                        // Use push() to generate a unique key for each category
                        database.getReference().child("Registered users").child(currentUserUid)
                                .child("categories").push()
                                .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(TestActivity.this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (data != null) {
                imageUri = data.getData();
                categoryImage.setImageURI(imageUri);
            }
        }
    }
}
