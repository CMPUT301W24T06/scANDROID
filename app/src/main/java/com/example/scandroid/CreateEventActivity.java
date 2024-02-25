package com.example.scandroid;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);
        //event = new Event();
        EditText editEventName = findViewById(R.id.event_name_edit_text);
        EditText editEventLocation = findViewById(R.id.event_location_edit_text);
        EditText editEventDate = findViewById(R.id.event_date_edit_text);//Split into 3 dropdowns?
        EditText editEventDescription = findViewById(R.id.event_description_edit_text);
        CheckBox editReceiveNotifications = findViewById(R.id.receive_milestones_checkbox);
        Button confirmButton = findViewById(R.id.create_event_button);
        Button backButton = findViewById(R.id.back_arrow);
        Button posterButton = findViewById(R.id.add_poster_icon);
        Button cancelButton = findViewById(R.id.cancel_update_event_button);

        //if (getIntent().getSerializableExtra("event") != null){
        // Fill out all the edit texts with selected event info
        //confirmButton.setText("Update My Event");
        //cancelButton.setVisibility(View.VISIBLE);
        //TextView qrNote = findViewById(R.id.create_event_note_text);
        //qrNote.setVisibility(View.INVISIBLE);
        //}
        posterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new PhotoAccessFragment().show(getSupportFragmentManager(), "Add Photo")
                //Image eventPoster = ?
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add event to database
                String eventName = editEventName.getText().toString();
                String eventDate = editEventDate.getText().toString();
                String eventLocation = editEventLocation.getText().toString();
                String eventDescription = editEventDescription.getText().toString();
                //Image eventPoster = ?
                boolean receiveMilestones = editReceiveNotifications.isChecked();

                //getIntent().putExtra(new Event())
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}