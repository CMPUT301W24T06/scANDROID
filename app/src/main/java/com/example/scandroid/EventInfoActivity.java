package com.example.scandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

/**
 * EventInfoActivity is an activity that is shown
 * when an event attendee clicks on their event from
 * their "Attending Events" tab on their HomepageActivity.
 * This activity displays the event's details
 * (title, location, date, time, and poster).
 */
public class EventInfoActivity extends AppCompatActivity implements onClickListener{
    private ImageView posterButton;
    private TextView bigEventName;
    private TextView eventName;
    private TextView eventLocation;
    private TextView eventDate;
    private TextView eventTime;
    private Button removeButton;
    private TextView eventDescription;
    private DBAccessor database;
    private String eventID;
    private Event event;
    private Calendar calendar = Calendar.getInstance();
    private AppCompatButton backButton;
    Bitmap eventPoster;
    CheckBox promiseCheckbox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        promiseCheckbox = findViewById(R.id.promise_checkbox);
        backButton = findViewById(R.id.back_arrow);
        bigEventName = findViewById(R.id.fetch_event_title_big);
        eventName = findViewById(R.id.fetch_event_title);
        eventLocation = findViewById(R.id.fetch_event_location);
        eventDate = findViewById(R.id.fetch_event_date);
        eventTime = findViewById(R.id.fetch_event_time);
        eventDescription = findViewById(R.id.fetch_event_description);
        posterButton = findViewById(R.id.create_event_change_poster);
        removeButton = findViewById(R.id.remove_event_button);
        database = new DBAccessor();

        backButton.setOnClickListener(v -> finish());

        eventID = (String) getIntent().getSerializableExtra("eventID");

        database.accessEvent(eventID, event -> {
            bigEventName.setText(event.getEventName());
            eventName.setText(event.getEventName());
            calendar = event.getEventDate();
            String eventDateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            eventDate.setText(eventDateText);
            String eventTimeText = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            eventTime.setText(eventTimeText);
            eventLocation.setText(new LocationGeocoder(EventInfoActivity.this).coordinatesToAddress(event.getEventLocation()));
            eventDescription.setText(event.getEventDescription());
            database.accessEventPoster(eventID, new BitmapCallback() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    if (bitmap != null) {
                        eventPoster = bitmap;
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

            posterButton.setOnClickListener(v -> {
                DialogFragment imageInspectPrompt = new AdminInspectImageFragment(eventPoster, EventInfoActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("eventID", eventID);
                imageInspectPrompt.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, imageInspectPrompt);
                transaction.commit();
            });

            database.accessUser(new DeviceIDRetriever(EventInfoActivity.this).getDeviceId(), user -> {
                if (user.getHasAdminPermissions()){
                    removeButton.setVisibility(View.VISIBLE);
                    removeButton.setOnClickListener(v -> {
                        database.accessUser(event.getEventOrganizerID(), organizerUser -> {
                            organizerUser.removeEventToEventsOrganized(eventID);
                            database.storeUser(organizerUser);
                        });
                        database.deleteEvent(eventID);
                        finish();
                    });
                }
            });
            promiseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // If checkbox is checked, perform action (implement Firebase)
                    Log.d("Checkbox", "Checkbox is checked");
                    // Implement your Firebase logic here
                } else {
                    // If checkbox is unchecked, nothing
                    Log.d("Checkbox", "Checkbox is unchecked");
                }
            });
        });
    }

    @Override
    public void onClick() {
        posterButton.setImageBitmap(null);
    }
}