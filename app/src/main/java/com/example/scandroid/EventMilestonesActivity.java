package com.example.scandroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

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
            milestonesArrayAdapter = new EventMilestonesArrayAdapter(this, milestoneList);
            milestoneListView.setAdapter(milestonesArrayAdapter);
        }
    }

    // https://www.youtube.com/watch?v=vyt20Gg2Ckg
    public void milestoneNotify(Long milestone, String eventID) {

        database.accessEvent(eventID, event -> {
            String sentence;
            if (milestone.intValue() == 1) {
                sentence = " user has checked into ";
            } else {
                sentence = " users have checked into ";
            }
            String channelID = "CHANNEL_ID_NOTIFICATION";
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), channelID);
            builder.setSmallIcon(R.drawable.milestone_party_popper)
                    .setContentTitle("New Milestone Reached")
                    .setContentText(String.valueOf(milestone) + sentence + event.getEventName())
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(getApplicationContext(), EventMilestonesActivity.class);
            intent.putExtra("event", event);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
                if (notificationChannel == null) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    notificationChannel = new NotificationChannel(channelID, "Event Milestone", importance);
                    notificationChannel.setLightColor(R.color.blue);
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            notificationManager.notify(0, builder.build());
        });
    }
}
