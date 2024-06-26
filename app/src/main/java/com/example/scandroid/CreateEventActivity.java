package com.example.scandroid;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;


/**
 * The view for when Users wish to create a new event or edit an existing event's parameters
 */
public class CreateEventActivity extends AppCompatActivity {
    Button posterButton;
    Bitmap posterBitmap;
    Boolean newEvent = true;
    String eventID;
    Event event;
    Calendar calendar = Calendar.getInstance();
    ImageView eventPoster;
    TextView attendeeLimit;
    int attendeeLimitNum;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);
        EditText editEventName = findViewById(R.id.event_name_edit_text);
        EditText editEventLocation = findViewById(R.id.event_location_edit_text);
        Button editEventDate = findViewById(R.id.edit_event_date_button);
        Button editEventTime = findViewById(R.id.edit_event_time_button);
        EditText editEventDescription = findViewById(R.id.event_description_edit_text);
        Button confirmButton = findViewById(R.id.create_event_confirm_button);
        Button backButton = findViewById(R.id.back_arrow);
        posterButton = findViewById(R.id.create_event_change_poster);
        eventPoster = findViewById(R.id.create_event_poster);
        DBAccessor database = new DBAccessor();
        Button setLimitButton = findViewById(R.id.attendee_limit_button);
        attendeeLimit = findViewById(R.id.current_limit_value);


        //Fills in event details if this Activity was accessed by clicking on an existing event
        event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            newEvent = false;
            eventID = event.getEventID();
            database.accessEvent(eventID, retrievedEvent -> {
                event = retrievedEvent;
                editEventName.setText(event.getEventName());
                calendar = event.getEventDate();
                int month = calendar.get(Calendar.MONTH) + 1;
                String eventDate = calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                editEventDate.setText(eventDate);
                String eventTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                editEventTime.setText(eventTime);
                editEventLocation.setText(new LocationGeocoder(CreateEventActivity.this).coordinatesToAddress(event.getEventLocation()));
                editEventDescription.setText(event.getEventDescription());
                String limitString;
                attendeeLimitNum = event.getEventCapacity();
                if (attendeeLimitNum == 0) {
                    limitString = "N/A";
                } else if (attendeeLimitNum == 1) {
                    limitString = "1 Attendee";
                } else {
                    limitString = String.format("%d Attendees", attendeeLimitNum);
                    ;
                }
                attendeeLimit.setText(limitString);
                database.accessEventPoster(eventID, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) {
                            eventPoster.setImageBitmap(bitmap);
                        } else {
                            Log.d("BitmapInfo", "Retrieved null Bitmap");
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Log.e("BitmapInfo", "Bitmap loading failed", e);
                    }
                });
            });
            confirmButton.setText(R.string.update_my_event);
            TextView qrNote = findViewById(R.id.create_event_note_text);
            qrNote.setVisibility(View.INVISIBLE);
        }


        posterButton.setOnClickListener(v -> {
            AllowAccessCameraRollFragment chooseImageFragment = AllowAccessCameraRollFragment.newInstance(eventID, eventPoster.getId(), "event", null);
            // Use a FragmentTransaction to add the fragment to the layout
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, chooseImageFragment);
            transaction.commit();
        });

        //Source: https://www.geeksforgeeks.org/datepicker-in-android/
        //User edits the event's date
        editEventDate.setOnClickListener(new View.OnClickListener() {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateEventActivity.this, R.style.DatePickerTheme, (view, year, monthOfYear, dayOfMonth) -> {

                    // check to see if the date of the event is in the future
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);
                    Calendar currentDate = Calendar.getInstance();
                    currentDate.add(Calendar.DAY_OF_MONTH, -1); // allows for an event to happen today

                    if (selectedDate.before(currentDate)) {
                        showToast("Please select a date in the future");
                    } else {
                        // Proceed with setting the selected date
                        editEventDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        //Source:https://www.geeksforgeeks.org/timepicker-in-android/
        //User edits the event's time
        editEventTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, R.style.TimePickerTheme,
                    (view, hourOfDay, minute1) -> {
                        editEventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                        calendar.set(Calendar.MINUTE, minute1);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        setLimitButton.setOnClickListener(v -> {
            showLimitAttendeesFragment();

        });

        //Update or create a new Event and store in database
        confirmButton.setOnClickListener(v -> {
            String eventName = editEventName.getText().toString();
            String eventDescription = editEventDescription.getText().toString();
            String eventLocation = editEventLocation.getText().toString();
            String eventDate = editEventDate.getText().toString();
            String eventTime = editEventTime.getText().toString();
            String attendeeLim = attendeeLimit.getText().toString();

            posterBitmap = new BitmapConfigurator().drawableToBitmap(eventPoster.getDrawable());

            // validate input before performing database operations
            boolean isValidInput = handleUserInput(eventName, eventLocation, eventDate, eventTime, eventDescription, attendeeLim);

            if (isValidInput) {
                //If this was a new event, create new Event object, new QR codes and store those
                ArrayList<Double> coords = new LocationGeocoder(CreateEventActivity.this).addressToCoordinates(eventLocation);
                if (newEvent) {
                    event = new Event(new DeviceIDRetriever(CreateEventActivity.this).getDeviceId(),
                            eventName, eventDescription, calendar, coords);
                    event.setEventCapacity(attendeeLimitNum);
                    if (coords.size() == 0) {
                        Toast.makeText(CreateEventActivity.this, "Invalid event location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    eventID = event.getEventID();
                    new EventQRCodesActivity().generateQRCode(eventID, false);
                    new EventQRCodesActivity().generateQRCode(eventID, true);


                    database.accessUser(new DeviceIDRetriever(CreateEventActivity.this).getDeviceId(), user -> {
                        user.addEventToEventsOrganized(eventID);
                        database.storeUser(user);
                    });
                } else { // Updating an old event
                    event.setEventDate(calendar);
                    event.setEventName(eventName);
                    event.setEventDescription(eventDescription);
                    event.setEventLocation(coords);
                    event.setEventCapacity(attendeeLimitNum);
                }
                database.storeEvent(event);
                database.storeEventPoster(eventID, posterBitmap);
                finish();
            }
        });
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Validates user input for creating an event.
     * Checks if the event name, location, description, date, time, and attendee limit are valid.
     *
     * @param eventName        The name of the event.
     * @param location         The location of the event.
     * @param date             The date of the event.
     * @param time             The time of the event.
     * @param eventDescription The description of the event.
     * @param eventLimit       The attendee limit of the event.
     * @return True if all inputs are valid, false otherwise.
     */
    private boolean handleUserInput(String eventName, String location, String date, String time, String eventDescription, String eventLimit) {
        boolean isValid = true;

        // check if an event name is a string and if it is valid
        if (eventName.isEmpty() || eventName.length() > 35) {
            showToast("Please enter a valid event name (up to 35 characters)");
            isValid = false;
            Log.d("Validation", "Event name validation failed");
        }
        if (location.isEmpty()) {
            showToast("Please enter a location for your event");
            isValid = false;
            Log.d("Validation", "Location validation failed");
        }
        // check if the user provided a description for their event
        if (eventDescription.isEmpty()) {
            showToast("Please enter a description for your event");
            isValid = false;
            Log.d("Validation", "Event description validation failed");
        }
        if (date.isEmpty()) {
            showToast("Please set a date for your event");
            isValid = false;
            Log.d("Validation", "Date validation failed");
        }
        if (time.isEmpty()) {
            showToast("Please set a time for your event");
            isValid = false;
            Log.d("Validation", "Time validation failed");
        }
        if (eventLimit.isEmpty()) {
            showToast("Please input a number, or press 'cancel' to abort");
            isValid = false;
            Log.d("Validation", "Attendee limit validation failed");
        }

        return isValid;
    }

    /**
     * Displays a custom toast message.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        StyleableToast.makeText(this, message, R.style.customToast).show();
    }

    /**
     * Updates the attendee limit based on received data.
     * If the attendee limit is 0, sets it to "N/A".
     * If the attendee limit is 1, sets it to "1 Attendee".
     * Otherwise, sets it to the number of attendees.
     *
     * @param data The data bundle containing the attendee limit.
     */
    @SuppressLint("DefaultLocale")
    public void onDataReceived(Bundle data) {
        int enteredLimit = data.getInt("attendeeLimit");
        String finalAttendeeNum;
        if (enteredLimit == 0) {
            finalAttendeeNum = "N/A";
            enteredLimit = Integer.MAX_VALUE; // assign an extremely high number
        } else if (enteredLimit == 1) {
            finalAttendeeNum = "1 Attendee";
        } else {
            finalAttendeeNum = String.format("%d Attendees", enteredLimit);
        }
        attendeeLimit.setText(finalAttendeeNum);
        attendeeLimitNum = enteredLimit;
    }

    /**
     * Displays the LimitAttendeesFragment dialog.
     * This dialog allows users to set the attendee limit for an event.
     * Shows the "Limit Attendee Fragment".
     */
    private void showLimitAttendeesFragment() {
        LimitAttendeesFragment limitAttendeesFragment = new LimitAttendeesFragment();
        limitAttendeesFragment.show(getSupportFragmentManager(), "LimitAttendeesFragment");
    }
}
