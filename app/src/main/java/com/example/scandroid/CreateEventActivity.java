package com.example.scandroid;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.Manifest;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Base64;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    ActivityResultLauncher<Intent> launcher;

    Button posterButton;
    Bitmap posterBitmap;
    Boolean newEvent = true;
    Event event;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);
        EditText editEventName = findViewById(R.id.event_name_edit_text);
        EditText editEventLocation = findViewById(R.id.event_location_edit_text);
        Button editEventDate = findViewById(R.id.edit_event_date_button);
        Button editEventTime = findViewById(R.id.edit_event_time_button);
        EditText editEventDescription = findViewById(R.id.event_description_edit_text);
        Button confirmButton = findViewById(R.id.create_event_button);
        Button backButton = findViewById(R.id.back_arrow);
        posterButton = findViewById(R.id.add_poster_icon);
        Button cancelButton = findViewById(R.id.cancel_update_event_button);
        registerResult();

        Calendar calendar;
        if (getIntent().getSerializableExtra("event") != null) {
            newEvent = false;
            event = (Event)getIntent().getSerializableExtra("event");
            confirmButton.setText("Update My Event");
            cancelButton.setVisibility(View.VISIBLE);
            TextView qrNote = findViewById(R.id.create_event_note_text);
            qrNote.setVisibility(View.INVISIBLE);
            editEventName.setText(event.getEventName());
            calendar = event.getEventDate();
            String eventDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            editEventDate.setText(eventDate);
            String eventTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            editEventTime.setText(eventTime);
            editEventLocation.setText(coordinatesToAddress(event.getEventLocation()));
            editEventDescription.setText(event.getEventDescription());
        } else {
            calendar = Calendar.getInstance();
        }

        posterButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });
        editEventDate.setOnClickListener(new View.OnClickListener() {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateEventActivity.this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editEventDate.setText(dayOfMonth + "-" + (monthOfYear) + "-" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        editEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, R.style.TimePickerTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editEventTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editEventName.getText().toString();
                String eventLocation = editEventLocation.getText().toString();
                String eventDescription = editEventDescription.getText().toString();
                ArrayList<Double> coords = addressToCoordinates(eventLocation);
                posterBitmap = drawableToBitmap(posterButton.getBackground());
                String eventID;

                if (newEvent) {
                    event = new Event("1", eventName, eventDescription, calendar, coords);
                    eventID = event.getEventID();
                }else{
                    eventID = event.getEventID();
                    event.setEventDate(calendar);
                    event.setEventName(eventName);
                    //event.setEventDescription(eventDescription); //Missing?
                    event.setEventLocation(coords);
                }

                DBAccessor database = new DBAccessor();
                //database.storeEvent(event);//Does this update existing events?
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //posterBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                //byte[] byteArray = byteArrayOutputStream.toByteArray();
                //String base64Image = Base64.encodeToString(byteArray, Base64.URL_SAFE);
                //database.storeEventPoster(eventID, byteArray);
                //database.getEvent("77f4bfa0-0583-4085-a197-f53cffacfbf0");
                //database.storeImageAsset("test", posterBitmap);

                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            pickImage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot pick image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    private void registerResult(){
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Drawable posterDrawable = Drawable.createFromStream(inputStream, imageUri.toString() );
                            posterBitmap = BitmapFactory.decodeStream(inputStream);
                            posterButton.setBackground(posterDrawable);

                        } catch (Exception e){
                            Toast.makeText(CreateEventActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

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

    public ArrayList<Double> addressToCoordinates(String eventLocation){
        Geocoder geocoder = new Geocoder(CreateEventActivity.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(eventLocation, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Double> coords = new ArrayList<Double>();
        coords.add(addresses.get(0).getLatitude());
        coords.add(addresses.get(0).getLongitude());
        return coords;
    }

    public String coordinatesToAddress(ArrayList<Double> coordinates){
        Geocoder geocoder = new Geocoder(CreateEventActivity.this, Locale.getDefault());
        Address location;

        String eventLocation;
        try {
            eventLocation = geocoder.getFromLocation(coordinates.get(0), coordinates.get(1), 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return eventLocation;
    }

}

