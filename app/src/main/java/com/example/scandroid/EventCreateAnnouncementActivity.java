package com.example.scandroid;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class EventCreateAnnouncementActivity extends AppCompatActivity {
    //incomplete, deal with editing the time later
    AppCompatButton backButton;
    EditText editNotificationTitle;
    EditText editNotificationInfo;
    EditText editNotificationMinutes;
    EditText editNotificationHour;
    EditText editNotificationPeriods;
    AppCompatButton sendNotificationButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = (Event)getIntent().getSerializableExtra("event");

        setContentView(R.layout.event_create_announcement_activity);
        backButton = findViewById(R.id.back_button);
        editNotificationTitle = findViewById(R.id.edit_notification_title);
        editNotificationInfo = findViewById(R.id.edit_notification_info);
        editNotificationMinutes = findViewById(R.id.notification_minutes);
        editNotificationHour = findViewById(R.id.notification_hour);
        editNotificationPeriods = findViewById(R.id.notification_periods);
        sendNotificationButton = findViewById(R.id.send_notification_button);

        backButton.setOnClickListener(v -> finish());
    }
}
