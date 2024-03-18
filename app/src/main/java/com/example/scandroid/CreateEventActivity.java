package com.example.scandroid;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * The view for when Users wish to create a new event or edit an existing event's parameters
 */
public class CreateEventActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> launcher;
    Button posterButton;
    Bitmap posterBitmap;
    Boolean newEvent = true;
    String eventID;
    Event event;
    Calendar calendar = Calendar.getInstance();
    ImageView eventPoster;

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


        //Fills in event details if this Activity was accessed by clicking on an existing event
        event = (Event)getIntent().getSerializableExtra("event");

        if (event != null){
            newEvent = false;
            eventID = event.getEventID();
            database.accessEvent(eventID, retrievedEvent -> {
                event = retrievedEvent;
                editEventName.setText(event.getEventName());
                calendar = event.getEventDate();
                String eventDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                editEventDate.setText(eventDate);
                String eventTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                editEventTime.setText(eventTime);
                editEventLocation.setText(new LocationGeocoder(CreateEventActivity.this).coordinatesToAddress(event.getEventLocation()));
                editEventDescription.setText(event.getEventDescription());
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

        posterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowAccessCameraRollFragment chooseImageFragment = AllowAccessCameraRollFragment.newInstance(eventID, eventPoster.getId(), "event", null);
                // Use a FragmentTransaction to add the fragment to the layout
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, chooseImageFragment);
                transaction.commit();
            }
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
                        CreateEventActivity.this, R.style.DatePickerTheme, (view, year, monthOfYear, dayOfMonth)
                        -> editEventDate.setText(dayOfMonth + "-" + (monthOfYear) + "-" + year), year, month, day);
                datePickerDialog.show();
            }
        });

        //Source:https://www.geeksforgeeks.org/timepicker-in-android/
        //User edits the event's time
        editEventTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, R.style.TimePickerTheme,
                    (view, hourOfDay, minute1) -> editEventTime.setText(hourOfDay + ":" + minute1), hour, minute, false);
            timePickerDialog.show();
        });

        //Update or create a new Event and store in database
        confirmButton.setOnClickListener(v -> {
            String eventName = editEventName.getText().toString();
            String eventDescription = editEventDescription.getText().toString();
            String eventLocation = editEventLocation.getText().toString();
            ArrayList<Double> coords = new LocationGeocoder(CreateEventActivity.this).addressToCoordinates(eventLocation);
            if (coords.size() == 0){
                Toast.makeText(CreateEventActivity.this, "Invalid event location", Toast.LENGTH_SHORT).show();
                return;
            }
            posterBitmap = new BitmapConfigurator().drawableToBitmap(eventPoster.getDrawable());

            //If this was a new event, create new Event object, new QR codes and store those
            if (newEvent) {
                event = new Event(new DeviceIDRetriever(CreateEventActivity.this).getDeviceId(),
                        eventName, eventDescription, calendar, coords);
                eventID = event.getEventID();
                new EventQRCodesActivity().generateCheckInQR(eventID, database);
                new EventQRCodesActivity().generatePromoQR(eventID, database);

                database.accessUser(new DeviceIDRetriever(CreateEventActivity.this).getDeviceId(), user -> {
                    user.addEventToEventsOrganized(eventID);
                    database.storeUser(user);
                });
            } else { // Updating an old event
                event.setEventDate(calendar);
                event.setEventName(eventName);
                event.setEventDescription(eventDescription);
                event.setEventLocation(coords);
            }
            database.storeEvent(event);
            database.storeEventPoster(eventID, posterBitmap);
            finish();
        });

        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Retrieves the image user selected from their gallery
     */
    //Source: https://www.youtube.com/watch?v=nOtlFl1aUCw
    private void registerResult(){
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        assert result.getData() != null;
                        Uri imageUri = result.getData().getData();
                        assert imageUri != null;
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Drawable posterDrawable = Drawable.createFromStream(inputStream, imageUri.toString() );
                        posterBitmap = BitmapFactory.decodeStream(inputStream);
                        posterButton.setBackground(posterDrawable);

                    } catch (Exception e){
                        Toast.makeText(CreateEventActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Starts an Intent for selecting an image from a user's gallery
     */
    //Source: https://www.youtube.com/watch?v=nOtlFl1aUCw
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    /**
     * Creates a Bitmap object from a Drawable object
     * @param drawable Drawable object that is being converted
     * @return
     * Returns a Bitmap object created from parameters of the Drawable object
     */
    //Source: https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

