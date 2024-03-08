package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EventInfoActivity extends AppCompatActivity {
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        event = (Event)getIntent().getSerializableExtra("event");
    }
}