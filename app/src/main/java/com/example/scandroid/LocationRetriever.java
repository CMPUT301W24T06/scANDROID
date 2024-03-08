package com.example.scandroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Retrieves current location of user
 */
//OpenAI, 2024, ChatGPT, How to get current location
public class LocationRetriever {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public LocationRetriever(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }


    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(new Activity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }
    }

    Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!hasLocationPermission()){
            getLocationPermission();
        }
        // Check for location permission
        if (hasLocationPermission() && locationManager != null) {
            try {
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (lastKnownLocationGPS != null && (lastKnownLocationNetwork == null || lastKnownLocationGPS.getTime() > lastKnownLocationNetwork.getTime())) {
                    return lastKnownLocationGPS;
                } else if (lastKnownLocationNetwork != null) {
                    return lastKnownLocationNetwork;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
