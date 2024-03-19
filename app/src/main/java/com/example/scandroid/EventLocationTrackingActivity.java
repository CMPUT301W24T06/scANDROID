package com.example.scandroid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * EventLocationTrackingActivity is an activity
 * that shows an event organizer a map with
 * pointers to where event attendees checked into their event
 * from.
 */
public class EventLocationTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    // MapView eventLocationMap;
    Event event;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_location_tracking_activity);
        event = (Event)getIntent().getSerializableExtra("event");
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if(event != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getEventLocation().get(0), event.getEventLocation().get(1)))
                    .title("Event Location"));
        }
    }
}
