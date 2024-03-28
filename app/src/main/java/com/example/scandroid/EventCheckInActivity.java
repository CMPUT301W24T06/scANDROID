package com.example.scandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
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
public class EventCheckInActivity extends AppCompatActivity {
    private Event event;
    private DBAccessor database;
    private TextView eventTitle;
    private TextView eventLocation;
    private CheckBox pushNotifBox;
    private CheckBox trackLocationBox;
    private AppCompatButton cancelCheckInButton;
    private AppCompatButton confirmCheckInButton;
    private ArrayList<Double> checkInLocation;


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
        String userID = (String) getIntent().getSerializableExtra("userID");

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
                        if (trackLocationBox.isChecked()) {
                            Location userLocation = new LocationRetriever(getApplicationContext()).getLastKnownLocation();
                            checkInLocation.add(userLocation.getLatitude());
                            checkInLocation.add(userLocation.getLongitude());
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
}
