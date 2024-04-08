package com.example.scandroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * EventMilestonesActivity is an activity that
 * shows an event organizer a list of the milestones
 * they hit for their event.
 */
public class EventMilestonesActivity extends AppCompatActivity {
    ListView milestoneListView;
    DBAccessor database;
    ArrayList<Long> milestoneList;
    EventMilestonesArrayAdapter milestonesArrayAdapter;
    AppCompatButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_milestones_activity);
        database = new DBAccessor();
        milestoneListView = findViewById(R.id.milestone_list);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        Event event = (Event) getIntent().getSerializableExtra("event");
        if (event != null) {
            milestoneList = event.getEventMilestones();
            milestoneList.remove(milestoneList.size() - 1);
            milestonesArrayAdapter = new EventMilestonesArrayAdapter(this, milestoneList);
            if (event.getEventAttendeesTotal() > 0) {
                milestoneListView.setAdapter(milestonesArrayAdapter);
            }
        }
    }
}
