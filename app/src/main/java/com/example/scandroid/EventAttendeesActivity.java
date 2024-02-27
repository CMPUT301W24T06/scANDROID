package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;

public class EventAttendeesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendees);
        Button backButton = findViewById(R.id.event_attendees_list_back_arrow);
        Button searchButton = findViewById(R.id.event_attendees_list_search_icon);
        EditText searchNameEdit = findViewById(R.id.event_attendees_list_search_bar);
        ListView attendeeList = findViewById(R.id.event_attendees_list);

        //Serializable event = getIntent().getSerializableExtra("event");
        //ArrayList<Event.CheckIn> attendeeDataList = event.getEventAttendeeList();

        //ArrayAdapter<Event.CheckIn> attendeeListAdapter = new EventAttendeesArrayAdapter(this, attendeeDataList);
        //attendeeList.setAdapter(attendeeListAdapter);

        attendeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start activity showing profile of user that got selected
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchName = searchNameEdit.getText().toString();
                //Limits list or adapter to only names that fit search?

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