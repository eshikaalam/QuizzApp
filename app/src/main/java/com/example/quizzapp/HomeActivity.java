package com.example.quizzapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizzapp.MainActivity;
import com.example.quizzapp.R;
import com.example.quizzapp.RatingFragment;
import com.example.quizzapp.TestActivity;
import com.example.quizzapp.UserProfileActivity;
import com.example.quizzapp.YouTubeVideo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private LottieAnimationView laView;
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
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_review) {
            // Open RatingActivity instead of replacing the fragment
            Intent intent = new Intent(HomeActivity.this, RatingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            // Refresh the page or activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_quiz) {
            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_learn_more) {
            Intent intent = new Intent(HomeActivity.this, YouTubeVideo.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(HomeActivity.this, "Logged out of the app", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            // Clear stack to prevent user coming back once logged out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close HomeActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

