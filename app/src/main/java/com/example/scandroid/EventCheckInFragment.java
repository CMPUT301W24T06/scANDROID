package com.example.scandroid;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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
    @Nullable
    private ArrayList<Double> checkInLocation;
    private Time checkInTime;

    public EventCheckInFragment() {
        // Required empty public constructor
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
                    eventTitle.setText(event.getEventName());
                    eventLocation.setText(new LocationGeocoder(getActivity()).coordinatesToAddress(event.getEventLocation()));

                    if (pushNotifBox.isChecked()) {
                        database.accessUser(new DeviceIDRetriever(requireActivity()).getDeviceId(), user -> {
                            user.addEventToNotifiedBy(eventID);
                            database.storeUser(user);
                        });
                    }

                    if (trackLocationBox.isChecked()) {
                        database.accessUser(new DeviceIDRetriever(requireActivity()).getDeviceId(), user -> {
                            // would set check in location here
                            Location userLocation = new LocationRetriever(requireContext()).getLastKnownLocation();
                            checkInLocation.add(userLocation.getLatitude());
                            checkInLocation.add(userLocation.getLongitude());
//                            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//                            if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                //    ActivityCompat#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for ActivityCompat#requestPermissions for more details.
//                                return;
//                            }
//                            fusedLocationClient.getLastLocation();
                        });
                    }

                    cancelCheckInButton.setOnClickListener(v -> {
                        dismiss();
                    });

                    confirmCheckInButton.setOnClickListener(v -> {
                        database.accessUser(new DeviceIDRetriever(requireActivity()).getDeviceId(), user -> {
                            user.addEventToEventsAttending(eventID);
                            // source: https://stackoverflow.com/a/5369753
                            Date currentTime = Calendar.getInstance().getTime();
                            event.addEventAttendee(user.getUserID(), new Time((currentTime).getTime()), checkInLocation);
                            database.storeUser(user);
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
