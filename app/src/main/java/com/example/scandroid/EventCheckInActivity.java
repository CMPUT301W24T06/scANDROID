package com.example.scandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * EventCheckInActivity is shown when a check-in QR code
 * is scanned in QRScannerActivity, and allows a user to see event details, allow push
 * notifications, allow location tracking, and check into an event.
 */
// sources: https://developer.android.com/develop/sensors-and-location/location/permissions#java
//          https://chat.openai.com/share/e300e1e4-7dd2-488c-8a26-d08e038e9169
//          https://chat.openai.com/share/fa53613d-a2e6-43ee-ade9-70ff94ea22bd
public class EventCheckInActivity extends AppCompatActivity {
    private Boolean locationAllowed = false;
    private DBAccessor database;
    private TextView eventTitle;
    private TextView eventLocation;
    private CheckBox pushNotifBox;
    private CheckBox trackLocationBox;
    private AppCompatButton cancelCheckInButton;
    private AppCompatButton confirmCheckInButton;
    private ArrayList<Double> checkInLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<String[]> locationPermissionRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_check_in_fragment);
        eventTitle = findViewById(R.id.fetch_event_title);
        eventLocation = findViewById(R.id.fetch_event_location);
        pushNotifBox = findViewById(R.id.push_notif_check_box);
        trackLocationBox = findViewById(R.id.location_track_check_box);
        cancelCheckInButton = findViewById(R.id.cancel_check_in_button);
        confirmCheckInButton = findViewById(R.id.check_in_button);

        database = new DBAccessor();

        String eventID = (String) getIntent().getSerializableExtra("eventID");
        String userID = new DeviceIDRetriever(EventCheckInActivity.this).getDeviceId();
        // (String) getIntent().getSerializableExtra("userID");

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        locationAllowed = true;
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                    }
                });

        // Set up click listener for trackLocationBox
        trackLocationBox.setOnClickListener(v -> {
            if (trackLocationBox.isChecked()) {
                checkAndRequestPermissions();
            }
        });


        database.accessEvent(eventID, event -> {

            if (event != null) {
                eventTitle.setText(event.getEventName());
                eventLocation.setText(new LocationGeocoder(EventCheckInActivity.this).coordinatesToAddress(event.getEventLocation()));

                cancelCheckInButton.setOnClickListener(v -> {
                    finish();
                });

                confirmCheckInButton.setOnClickListener(v -> {
                    database.accessUser(userID, user -> {

                        checkInLocation = new ArrayList<>();

                        if (pushNotifBox.isChecked()) {
                            user.addEventToNotifiedBy(eventID);
                        }
                        if (trackLocationBox.isChecked() && locationAllowed) {
                            Location userLocation = new LocationRetriever(getApplicationContext()).getLastKnownLocation();
                            checkInLocation.add(userLocation.getLatitude());
                            checkInLocation.add(userLocation.getLongitude());
                        }
                        if (!trackLocationBox.isChecked()) {
                            checkInLocation.add(0.0);
                            checkInLocation.add(0.0);
                        }

                        // source: https://stackoverflow.com/a/5369753
                        Time time = new Time(Calendar.getInstance().getTime().getTime());
                        event.addEventAttendee(userID, time, checkInLocation);
                        user.addEventToEventsAttending(eventID);


                        database.storeEvent(event);
                        database.storeUser(user);
                        finish();
                    });
                });
            } else {
                finish();
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        // Check if permissions are already granted
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            } else {
                locationAllowed = true;
            }
        }
        // Request permissions if not already granted
        if (!allPermissionsGranted) {
            locationPermissionRequest.launch(permissions);
        }
    }

    // Handle permission request result (if needed)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Handle permission request result here
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                locationAllowed = true;
            } else {
                locationAllowed = false;
                // Handle case when permissions are not granted
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
