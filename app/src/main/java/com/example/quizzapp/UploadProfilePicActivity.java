package com.example.quizzapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.example.quizzapp.R;
import com.example.quizzapp.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri  uriImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Upload Profile Picture");

        }

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBar = findViewById(R.id.progessBar);
        imageViewUploadPic = findViewById(R.id.imageView_profile_pic);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();


        // set User's current dp in imageview (if uploaded already)
        if (uri != null) {
            Picasso.get().load(uri).into(imageViewUploadPic);
        }

        //choosing image to upload
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });

        // upload image
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
            }
        });

    }

    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    private void UploadPic(){
        if (uriImage != null){
            //save the image with vid of the currently logged user
            FirebaseUser currentUser = authProfile.getCurrentUser();

            if (currentUser != null) {
                StorageReference fileReference = storageReference.child(currentUser.getUid() + "." + getFileExtension(uriImage));

                //upload profile pic to storage
                fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                                firebaseUser = authProfile.getCurrentUser();

                                //finally set the display image of the user to upload
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(
                                        downloadUri).build();
                                firebaseUser.updateProfile(profileUpdates);
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadProfilePicActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadProfilePicActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadProfilePicActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //File extension method

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //creating actionbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_refresh){
            //Refresh the page or activity start
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }  else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UploadProfilePicActivity.this, TestActivity.class);
            startActivity(intent);
            finish();
        } /* else if(id == R.id.menu_quiz){
            Intent intent = new Intent(UserProfileActivity.this, QuizActivity.class);
            startActivity(intent);
        } else if(id == R.id.menu_settings){
            Toast.makeText(UserProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_review){
            Intent intent = new Intent(UserProfileActivity.this, ReviewActivity.class);
            startActivity(intent);
        } */else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this, "Logged out of the app", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadProfilePicActivity.this,MainActivity.class);

            //Clear stack to prevent user coming back once logged out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //close UserProfileActivity
        } else {
            Toast.makeText(UploadProfilePicActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}