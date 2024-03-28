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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * EventCheckInFragment is shown when a check-in QR code
 * is scanned in QRScannerActivity, and allows a user to see event details, allow push
 * notifications, allow location tracking, and check into an event.
 */
public class EventCheckInFragment extends DialogFragment {
    private Event event;
    private DBAccessor database;
    private TextView eventTitle;
    private TextView eventLocation;
    private CheckBox pushNotifBox;
    private CheckBox trackLocationBox;
    private AppCompatButton cancelCheckInButton;
    private AppCompatButton confirmCheckInButton;
    private ArrayList<Double> checkInLocation;
    private Context context;

    public EventCheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_check_in_fragment, container, false);

        eventTitle = view.findViewById(R.id.fetch_event_title);
        eventLocation = view.findViewById(R.id.fetch_event_location);
        pushNotifBox = view.findViewById(R.id.push_notif_check_box);
        trackLocationBox = view.findViewById(R.id.location_track_check_box);
        cancelCheckInButton = view.findViewById(R.id.cancel_check_in_button);
        confirmCheckInButton = view.findViewById(R.id.check_in_button);

        database = new DBAccessor();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            String eventID = bundle.getString("eventID");
            database.accessEvent(eventID, event -> {

                if (event != null) {
                    checkInLocation = new ArrayList<>();
                    eventTitle.setText(event.getEventName());
                    eventLocation.setText(new LocationGeocoder(context).coordinatesToAddress(event.getEventLocation()));
                    String userID = new DeviceIDRetriever(context).getDeviceId();

                    cancelCheckInButton.setOnClickListener(v -> {
                        dismiss();
                    });

                    confirmCheckInButton.setOnClickListener(v -> {
                        if (pushNotifBox.isChecked()) {
                            database.accessUser(userID, user -> {
                                user.addEventToNotifiedBy(eventID);
                                database.storeUser(user);
                            });
                        }

                        if (trackLocationBox.isChecked()) {
                            database.accessUser(userID, user -> {
                                Location userLocation = new LocationRetriever(requireContext()).getLastKnownLocation();
                                checkInLocation.add(userLocation.getLatitude());
                                checkInLocation.add(userLocation.getLongitude());
                            });
                        }

                        database.accessUser(userID, user1 -> {
                            user1.addEventToEventsAttending(eventID);
                            // source: https://stackoverflow.com/a/5369753
                            Time time = new Time(Calendar.getInstance().getTime().getTime());

                            event.addEventAttendee(user1.getUserID(), time, checkInLocation);
                            database.storeUser(user1);
                        });
                        dismiss();
                    });
                } else {
                    dismiss();
                }
            });
        }
        return view;
    }
}
