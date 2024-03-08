package com.example.scandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

/**
 * EditEventActivity is an activity that links all of
 * the necessary activities for an event organizer:
 * CreateEventActivity, EventAttendeesActivity, EventQRCodesActivity,
 * EventQRCodesActivity, EventCreateAnnouncementsActivity, EventLocationTrackingActivity,
 * and EventMilestonesActivity.
 */
public class EditEventActivity extends AppCompatActivity {
    Event event;
    AppCompatButton backButton;
    AppCompatButton eventDetailsPageButton;
    AppCompatButton eventAttendeesPageButton;
    AppCompatButton eventQRCodePageButton;
    AppCompatButton eventCreateAnnouncementsPageButton;
    AppCompatButton eventLocationTrackingPageButton;
    AppCompatButton eventMilestonesPageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event_activity);

        backButton = findViewById(R.id.back_button);
        eventDetailsPageButton = findViewById(R.id.event_details_button);
        eventAttendeesPageButton = findViewById(R.id.attendees_button);
        eventQRCodePageButton = findViewById(R.id.QR_code_info_button);
        eventCreateAnnouncementsPageButton = findViewById(R.id.announcements_button);
        eventLocationTrackingPageButton = findViewById(R.id.location_tracking_button);
        eventMilestonesPageButton = findViewById(R.id.milestones_button);

        backButton.setOnClickListener(v -> finish());

        if (getIntent().getSerializableExtra("event") != null) {
            event = (Event) getIntent().getSerializableExtra("event");
            eventDetailsPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, CreateEventActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
            eventAttendeesPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, EventAttendeesActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
            eventQRCodePageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, EventQRCodesActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
            eventCreateAnnouncementsPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, EventCreateAnnouncementActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
            eventLocationTrackingPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, EventLocationTrackingActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
            eventMilestonesPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditEventActivity.this, EventMilestonesActivity.class);
                    i.putExtra("event", getIntent().getSerializableExtra("event"));
                    startActivity(i);
                }
            });
        }
    }
}