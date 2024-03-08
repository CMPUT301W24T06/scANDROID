package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * EventViewAnnouncementsActivity is an Activity which allows for
 * viewing announcements related to an event.
 */
public class EventViewAnnouncementsActivity extends AppCompatActivity {
    BottomNavigationView navigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view_announcements_activity);

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.notification_button);

        navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_button:
                        startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                        return true;
                    case R.id.qr_button:
                        startActivity(new Intent(getApplicationContext(), QRScannerActivity.class));
                        return true;
                    case R.id.browse_button:
                        startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                        return true;
                    case R.id.notification_button:
                        return true;

                }
                return false;
            }
        });
    }
}