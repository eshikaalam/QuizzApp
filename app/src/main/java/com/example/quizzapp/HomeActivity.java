package com.example.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    LottieAnimationView laView;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        laView = findViewById(R.id.animation);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        laView.setAnimation(R.raw.animationlottie);
        laView.playAnimation();
        laView.loop(true);
    }

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
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }  else if(id == R.id.menu_quiz){
            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
            startActivity(intent);
        }  else if(id == R.id.menu_learn_more){
            Intent intent = new Intent(HomeActivity.this, YouTubeVideo.class);
            startActivity(intent);
        } /* else if(id == R.id.menu_review){
            Intent intent = new Intent(UserProfileActivity.this, ReviewActivity.class);
            startActivity(intent);
        } */else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(HomeActivity.this, "Logged out of the app", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);

            //Clear stack to prevent user coming back once logged out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //close UserProfileActivity
        } else {
            Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}