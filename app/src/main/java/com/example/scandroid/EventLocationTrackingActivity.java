package com.example.scandroid;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * EventLocationTrackingActivity is an activity
 * that shows an event organizer a map with
 * pointers to where event attendees checked into their event
 * from.
 */

// sources: https://developers.google.com/maps/documentation/android-sdk/map#view_the_code
//          https://github.com/googlemaps-samples/android-samples/blob/main/ApiDemos/java/app/src/gms/java/com/example/mapdemo/RawMapViewDemoActivity.java
//          https://stackoverflow.com/a/18481305
//          https://developers.google.com/maps/documentation/android-sdk/marker

public class EventLocationTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    Event event;
    AppCompatButton backButton;
    private MapView mapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_location_tracking_activity);
        backButton = findViewById(R.id.back_button);
        event = (Event)getIntent().getSerializableExtra("event");

        backButton.setOnClickListener(v -> finish());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // TODO: add markers for each event check in that agreed to have their location shared
        if(event != null) {
            String eventTitle = event.getEventName();
            String eventAddress = new LocationGeocoder(EventLocationTrackingActivity.this).coordinatesToAddress(event.getEventLocation());
            LatLng eventLatLng = new LatLng(event.getEventLocation().get(0), event.getEventLocation().get(1));
            googleMap.addMarker(new MarkerOptions()
                    .position(eventLatLng)
                            .snippet(eventAddress)
                    .title(eventTitle));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 0));
            ArrayList<Event.CheckIn> attendees = event.getEventAttendeeList();
            for (Event.CheckIn i : attendees) {
                ArrayList<Double> checkInLoc = i.getCheckInLocation();
                if(checkInLoc != null) {
                    LatLng checkInLatLng = new LatLng(checkInLoc.get(0), checkInLoc.get(1));
                    String checkInAddress = new LocationGeocoder(EventLocationTrackingActivity.this).coordinatesToAddress(checkInLoc);
                    googleMap.addMarker(new MarkerOptions()
                            .position(checkInLatLng)
                            .snippet(checkInAddress)
                            .title("Check In")
                            .alpha(0.8f));
                }
            }
        }
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
