// RatingActivity.java
package com.example.quizzapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Display the RatingFragment
        RatingFragment ratingFragment = new RatingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rating_fragment_container, ratingFragment)
                .addToBackStack(null)
                .commit();
    }
}
