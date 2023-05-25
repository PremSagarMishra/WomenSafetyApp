package com.example.womensafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Inside your onCreate() method or wherever you initialize your location-related functionality
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when the location is updated
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // Do something with the latitude and longitude values
                // For example, display them on the UI or use them for further processing
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Check if the ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Request the permission if not granted
            int LOCATION_PERMISSION_REQUEST_CODE=1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }




        findViewById(R.id.relative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RelativeActivity.class));
            }
        });
        findViewById(R.id.emergencyno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EmergencyNoActivity.class));
            }
        });

        findViewById(R.id.mylocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if location is enabled
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isLocationEnabled) {
                    // Location is enabled, proceed with retrieving the location
                    if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastKnownLocation != null) {
                            latitude = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();

                            // Do something with the latitude and longitude values
                            // For example, display them on the UI or use them for further processing
                            String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Your location" + ")";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                            startActivity(intent);
                        }
                    } else {
                        // Request the permission if not granted
                        int LOCATION_PERMISSION_REQUEST_CODE = 1;
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    // Location is not enabled, redirect to location settings
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingsIntent);
                }


            }
        });



    }


}

