package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * EventViewAnnouncementsActivity is an Activity which allows for
 * viewing announcements related to an event.
 */
public class EventViewAnnouncementsActivity extends AppCompatActivity {
    BottomNavigationView navigationBar;
    ListView announcementListView;
    ArrayAdapter<Event.EventAnnouncement> eventAnnouncementsAdapter;
    int numberOfEvents;
    int checkedEvents = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view_announcements_activity);
        announcementListView = findViewById(R.id.announcements_list);
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

        String userID = new DeviceIDRetriever(EventViewAnnouncementsActivity.this).getDeviceId();
        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
            ArrayList<String> attendingEvents = user.getEventsAttending();
            ArrayList<Event.EventAnnouncement> announcementList = new ArrayList<>();
            ArrayList<Event.EventAnnouncement> pastAnnouncementList = new ArrayList<>();
            numberOfEvents = attendingEvents.size();
            for (String eventID: attendingEvents){
                database.accessEvent(eventID, event -> {
                    announcementList.addAll(event.getEventAnnouncements());
                    checkedEvents += 1;
                    if (checkedEvents == numberOfEvents){
                        for (Event.EventAnnouncement announcement: announcementList){
                            Time announcementTime = announcement.getAnnouncementTime();
                            Date currentDate = new Date();
                            Date eventDate = new Date(announcementTime.getTime());
                            if (currentDate.after(eventDate)){
                                pastAnnouncementList.add(announcement);
                            }
                        }
                        TextView loadingTextView = findViewById(R.id.loading_view_announcements_text);
                        if (pastAnnouncementList.size() == 0){
                            loadingTextView.setText("No announcements");
                        } else {
                            eventAnnouncementsAdapter = new AnnouncementsArrayAdapter(EventViewAnnouncementsActivity.this, pastAnnouncementList);
                            announcementListView.setAdapter(eventAnnouncementsAdapter);
                            loadingTextView.setVisibility(View.INVISIBLE);
                        }

                    }

                });
            }
        });
    }
}