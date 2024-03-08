package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * EventInfoActivity is an activity that is shown
 * when an event attendee clicks on their event from
 * their "Attending Events" tab on their HomepageActivity.
 * This activity displays the event's details
 * (title, location, date, time, and poster).
 */
public class EventInfoActivity extends AppCompatActivity {
    private ImageView posterButton;
    private TextView bigEventName;
    private TextView eventName;
    private TextView eventLocation;
    private Button eventDate;
    private TextView eventDescription;
    private DBAccessor database;
    private String eventID;
    private Event event;
    private Calendar calendar = Calendar.getInstance();
    private AppCompatButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        backButton = findViewById(R.id.back_arrow);
        bigEventName = findViewById(R.id.fetch_event_title_big);
        eventName = findViewById(R.id.fetch_event_title);
        eventLocation = findViewById(R.id.fetch_event_location);
        eventDate = findViewById(R.id.fetch_event_date);
        eventDescription = findViewById(R.id.fetch_event_description);
        posterButton = findViewById(R.id.add_poster_icon);
        database = new DBAccessor();

        backButton.setOnClickListener(v -> finish());

        eventID = (String) getIntent().getSerializableExtra("eventID");

        database.accessEvent(eventID, new EventCallback() {
            @Override
            public void onEventReceived(Event event) {
                bigEventName.setText(event.getEventName());
                eventName.setText(event.getEventName());
                calendar = event.getEventDate();
                String eventDateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                eventDate.setText(eventDateText);
                eventLocation.setText(new LocationGeocoder(EventInfoActivity.this).coordinatesToAddress(event.getEventLocation()));
                eventDescription.setText(event.getEventDescription());
                database.accessEventPoster(eventID, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) {
                            BitmapDrawable imageDrawable = new BitmapDrawable(getResources(), bitmap);
                            posterButton.setImageDrawable(imageDrawable);
                        } else {
                            posterButton.setVisibility(View.INVISIBLE);
                            Log.d("BitmapInfo", "Retrieved null Bitmap");
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Log.e("BitmapInfo", "Bitmap loading failed", e);
                        posterButton.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}