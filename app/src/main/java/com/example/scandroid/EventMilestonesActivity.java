package com.example.scandroid;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class EventMilestonesActivity extends AppCompatActivity {
    ListView milestoneListView;
    AppCompatButton backButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = (Event)getIntent().getSerializableExtra("event");

        setContentView(R.layout.event_milestones_activity);
        milestoneListView = findViewById(R.id.milestone_list);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());
    }
}