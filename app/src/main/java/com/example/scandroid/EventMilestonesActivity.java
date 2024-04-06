package com.example.scandroid;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

/**
 * EventMilestonesActivity is an activity that
 * shows an event organizer a list of the milestones
 * they hit for their event.
 */
public class EventMilestonesActivity extends AppCompatActivity {
    ListView milestoneListView;
    ArrayList<Long> milestoneList;
    EventMilestonesArrayAdapter milestonesArrayAdapter;
    AppCompatButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_milestones_activity);
        milestoneListView = findViewById(R.id.milestone_list);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        Event event = (Event)getIntent().getSerializableExtra("event");
        if (event!=null) {
            milestoneList = event.getEventMilestones();
            milestonesArrayAdapter = new EventMilestonesArrayAdapter(this, milestoneList);
            milestoneListView.setAdapter(milestonesArrayAdapter);
        }
    }
}
