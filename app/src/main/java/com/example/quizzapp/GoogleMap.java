package com.example.quizzapp;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMap extends AppCompatActivity {
   FusedLocationProviderClient fusedLocationProviderClient;
   TextView country, city, address, longitude;
   Button getLocation;

   private final static int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);
        longitude = findViewById(R.id.longitude);
        getLocation = findViewById(R.id.get_location_button);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation();
            }
        });

    }

    private void getLastLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Your existing code to get location details
                                Geocoder geocoder = new Geocoder(GoogleMap.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                                    if (addresses != null && addresses.size() > 0) {
                                        // Check if addresses is not null and contains at least one address
                                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
                                        address.setText("Address: " + addresses.get(0).getAddressLine(0));
                                        city.setText("City: " + addresses.get(0).getLocality());
                                        country.setText("Country: " + addresses.get(0).getCountryName());
                                    } else {
                                        // Handle the case where addresses is null or empty
                                        Toast.makeText(GoogleMap.this, "No address found", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    // Handle the exception appropriately
                                    e.printStackTrace();
                                    Toast.makeText(GoogleMap.this, "Error getting location details", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(GoogleMap.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        } else {

            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(GoogleMap.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}