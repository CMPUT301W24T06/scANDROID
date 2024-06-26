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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import javax.annotation.Nullable;
import java.util.Objects;

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
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 99;
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
        
        checkAndRequestPermissions();
        
        database.accessEvent(eventID, event -> {
            if (event != null) {
                eventTitle.setText(event.getEventName());
                eventLocation.setText(new LocationGeocoder(EventCheckInActivity.this).coordinatesToAddress(event.getEventLocation()));
                cancelCheckInButton.setOnClickListener(v -> {
                    finish();
                });
                Calendar eventDate = event.getEventDate();
                Calendar midnight = (Calendar) eventDate.clone();
                midnight.set(Calendar.MINUTE, 0);
                midnight.set(Calendar.HOUR_OF_DAY, 0);
                midnight.set(Calendar.SECOND, 0);
                midnight.add(Calendar.DAY_OF_MONTH, 1);
                if (Calendar.getInstance().after(midnight)){
                    NoticeFragment endedNoticeFragment = new NoticeFragment("This event has already ended");
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(android.R.id.content, endedNoticeFragment);
                    transaction.commit();
                }

                // https://www.youtube.com/watch?v=96IBhBs-k1M&t=189s
                pushNotifBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // If the checkbox is checked, request notification permissions
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestNotificationPermissions();
                            }
                        }
                    }
                });

                confirmCheckInButton.setOnClickListener(v -> {
                    boolean alreadyAttendee = false;
                    boolean canCheckIn = false;
                    //Check if user is already an attendee of event
                    ArrayList<Event.CheckIn> attendeeList = event.getEventAttendeeList();
                    for (Event.CheckIn attendee: attendeeList){
                        if (Objects.equals(attendee.getUserID(), userID)){
                            alreadyAttendee = true;
                            canCheckIn = true;
                            break;
                        }
                    }
                    //If not already an attendee, check if event still has capacity for more check ins
                    if(!alreadyAttendee){
                        if (event.getEventHasCapacity()) {
                            if (event.getEventAttendeesTotal() < event.getEventCapacity()) {
                                canCheckIn = true;
                            } else {
                                canCheckIn = false;
                            }
                        } else {
                            canCheckIn = true; // Event has no capacity limit
                        }
                    }
                    if (canCheckIn){
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
                                checkInLocation.add(32.909630);
                                checkInLocation.add(-117.181930);
                            }

                            // source: https://stackoverflow.com/a/5369753
                            Time time = new Time(Calendar.getInstance().getTime().getTime());
                            if (user.getEventsAttending().contains(eventID)) {
                                event.addExistingEventAttendee(userID, time, checkInLocation);
                            } else {
                                event.addEventAttendee(userID, time, checkInLocation);
                            }
                            user.addEventToEventsAttending(eventID);
                            database.storeEvent(event);
                            database.storeUser(user);
                            CheckInConfirmationFragment confirmationFragment = CheckInConfirmationFragment.newInstance("param1", "param2");
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(android.R.id.content, confirmationFragment)
                                    .addToBackStack(null)  // Optional: Add to back stack if you want to allow back navigation
                                    .commit();
                        });
                    } else {
                        NoticeFragment fullEventNotice = new NoticeFragment("This event has reached maximum capacity");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(android.R.id.content, fullEventNotice);
                        transaction.commit();
                    }
                });
            } else {
                finish();
            }
        });
    }

    /**
     * Requests notification permissions for posting notifications.
     * Requires Tiramisu (Android 18).
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermissions() {
        // Check if the notification permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Notification permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Checks and requests location permissions if not already granted.
     */
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

    /**
     * Callback for the result of a permission request.
     * Handles location and notification permission requests.
     *
     * @param requestCode  The request code passed to requestPermissions().
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions.
     *                     Either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Handle location permission request result
            handleLocationPermissionResult(grantResults);
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Handle notification permission request result
            handleNotificationPermissionResult(grantResults);
        }
    }

    /**
     * Handles the result of a location permission request.
     *
     * @param grantResults The grant results for the corresponding permissions.
     *                     Either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    // OpenAI ChatGPT 2024: How to handle different types of permissions
    // Method to handle location permission request result
    // OpenAI ChatGPT 2024: How to handle different types of permissions
    private void handleLocationPermissionResult(int[] grantResults) {
        // Check if all location permissions are granted
        boolean allPermissionsGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            // Handle case when location permissions are not granted
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the result of a notification permission request.
     *
     * @param grantResults The grant results for the corresponding permissions.
     *                     Either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    private void handleNotificationPermissionResult(int[] grantResults) {
        // Check if notification permission is granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Notification permission is granted
            Toast.makeText(this, "Notification permissions granted", Toast.LENGTH_SHORT).show();
        } else {
            // Handle case when notification permission is not granted
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}

