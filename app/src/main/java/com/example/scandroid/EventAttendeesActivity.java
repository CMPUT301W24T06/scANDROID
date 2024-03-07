package com.example.scandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EventAttendeesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_attendees_activity);
        Button backButton = findViewById(R.id.event_attendees_list_back_arrow);
        Button searchButton = findViewById(R.id.event_attendees_list_search_icon);
        EditText searchNameEdit = findViewById(R.id.event_attendees_list_search_bar);
        ListView attendeeList = findViewById(R.id.event_attendees_list);

        Event event = (Event)getIntent().getSerializableExtra("event");
        assert event != null;
        ArrayList<Event.CheckIn> attendeeDataList = event.getEventAttendeeList();

        if(attendeeDataList != null) {
            ArrayAdapter<Event.CheckIn> attendeeListAdapter = new EventAttendeesArrayAdapter(this, attendeeDataList, event.getEventID());
            attendeeList.setAdapter(attendeeListAdapter);

            attendeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event.CheckIn attendee = attendeeDataList.get(position);
                    Intent profileInfoIntent = new Intent(EventAttendeesActivity.this, ProfileInfoActivity.class);
                    //profileInfoIntent.putExtra("attendee", attendeeListAdapter.getItem(position));
                    //profileInfoIntent.putExtra("eventID", event.getEventID());
                    //startActivity(profileInfoIntent);
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchName = searchNameEdit.getText().toString();
                    //Limits list or adapter to only names that fit search?

                }
            });
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
                }
        });
    }
}