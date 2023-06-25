package com.example.womensafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;
    private PanicService panicService;

    private static final String KEY_SERVICE_RUNNING = "service_running";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create an instance of PanicService
        panicService = new PanicService(getApplicationContext());
        TextView panicmodetext=findViewById(R.id.panicmodetext);
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("isServiceRunning",false)){
            panicmodetext.setText("Start Panic Mode");
        }
        else {
            panicmodetext.setText("Stop Panic Mode");
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(KEY_SERVICE_RUNNING, false).apply();
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


        findViewById(R.id.panicmodesetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PanicSettingActivity.class));
            }
        });


        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });
        findViewById(R.id.panicmode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panicmodetext.getText().equals("Start Panic Mode")) {
                    panicService.startPanicService();
                    panicmodetext.setText("Stop Panic Mode");
                } else {
                    panicService.stopPanicService();
                    panicmodetext.setText("Start Panic Mode");
                }
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

        //notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("your_channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                .setSmallIcon(R.drawable.location)
                .setContentTitle("Women Safety App")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true) // Keeps the notification ongoing
                .addAction(R.drawable.panicmode, "Panic Mode", createButton1PendingIntent()) // Button 1
                .addAction(R.drawable.location, "Stop Panic Mode", createButton2PendingIntent()); // Button 2

        int notificationId = 1; // Unique identifier for the notification

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

        ButtonClickReceiver receiver = new ButtonClickReceiver(panicService);
        IntentFilter filter = new IntentFilter();
        filter.addAction("BUTTON_1_ACTION");
        filter.addAction("BUTTON_2_ACTION");
        registerReceiver(receiver, filter);



    }
    private PendingIntent createButton1PendingIntent() {
        Intent button1Intent = new Intent("BUTTON_1_ACTION");
        PendingIntent button1PendingIntent = PendingIntent.getBroadcast(this, 0, button1Intent, 0);
        return button1PendingIntent;
    }

    private PendingIntent createButton2PendingIntent() {
        Intent button2Intent = new Intent("BUTTON_2_ACTION");
        PendingIntent button2PendingIntent = PendingIntent.getBroadcast(this, 0, button2Intent, 0);
        return button2PendingIntent;
    }


}


class ButtonClickReceiver extends BroadcastReceiver {
    private PanicService panicService;

    public ButtonClickReceiver(PanicService panicService) {
        this.panicService = panicService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("BUTTON_1_ACTION")) {
                // Handle Button 1 click
                if (!panicService.isPanicServiceRunning()) {
                    panicService.startPanicService();
                    updatePanicModeText(context, true);
                }
            } else if (intent.getAction().equals("BUTTON_2_ACTION")) {
                // Handle Button 2 click
                if (panicService.isPanicServiceRunning()) {
                    panicService.stopPanicService();
                    updatePanicModeText(context, false);
                }
            }
        }
    }

    private void updatePanicModeText(Context context, boolean isServiceRunning) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isServiceRunning", isServiceRunning);
        editor.apply();
    }
}

