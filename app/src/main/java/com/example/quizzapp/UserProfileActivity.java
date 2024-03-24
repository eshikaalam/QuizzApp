package com.example.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textviewWelcome, textviewFullName, textviewEmail, textviewDoB, textviewGender, textviewMobile, textviewMap;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile,map;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        textviewWelcome = findViewById(R.id.textView_show_welcome);
        textviewFullName = findViewById(R.id.textView_show_full_name);
        textviewEmail = findViewById(R.id.textView_show_email);
        textviewDoB = findViewById(R.id.textView_show_dob);
        textviewGender = findViewById(R.id.textView_show_gender);
        textviewMobile = findViewById(R.id.textView_show_mobile);
        textviewMap = findViewById(R.id.textView_show_map);
        progressBar = findViewById(R.id.progessBar);

        //Set OnclickListener on ImageView to open UploadProfileActivity

        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });
        textviewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, MyGoogleMap.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User details are not available", Toast.LENGTH_SHORT).show();
        } else {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("You can not login next time. verify your email first");

        //open email app if user clicks continue

        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window, not within our app
                startActivity(intent);
            }
        });

        //create alert dialog box
        AlertDialog alertDialog = builder.create();

        //show the alert dialog box
        alertDialog.show();
    }

    private void showUserProfile( FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // extracting user reference from database for registered users

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readWriteUserDetails.doB;
                    gender = readWriteUserDetails.gender;
                    mobile = readWriteUserDetails.mobile;

                    textviewWelcome.setText("Welcome, " + fullName + "!");
                    textviewFullName.setText(fullName);
                    textviewEmail.setText(email);
                    textviewDoB.setText(doB);
                    textviewGender.setText(gender);
                    textviewMobile.setText(mobile);

                    //set User dp (after user has uploaded)
                    //set User dp (after the user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();

// ImageView setImageURI() should not be used with regular URIs, so use Picasso
                    if (uri != null) {
                        Picasso.get().load(uri).placeholder(R.drawable.no_profile_pic).into(imageView);
                    } else {
                        // If there is no profile picture URI, load a placeholder image
                        Picasso.get().load(R.drawable.no_profile_pic).into(imageView);
                    }

                } else {
                    Toast.makeText(UserProfileActivity.this, "Error Uploading Profile pic", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }




}