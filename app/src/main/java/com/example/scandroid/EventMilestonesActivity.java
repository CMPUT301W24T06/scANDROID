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

    // https://www.youtube.com/watch?v=YjNZO90yVsE
    // https://www.youtube.com/watch?v=6_t87WW6_Gc
    private void sendNotification(String title, String body, ArrayList<String> receiverTokens) {
        Log.d("Notification", "Sending notification..."); // testing message

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");


        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("title", title);
            jsonNotification.put("body", body);
        } catch (JSONException e) {
            Log.e("JSON", "Error creating JSON notification object", e);
            return; // return early if JSON creation fails
        }

        for (String receiverToken : receiverTokens) {
            JSONObject wholeObject = new JSONObject();
            try {
                wholeObject.put("to", receiverToken);
                wholeObject.put("notification", jsonNotification);
            } catch (JSONException e) {
                Log.e("JSON", "Error creating JSON object", e);
                continue; // continue to the next token if JSON creation fails
            }

            RequestBody requestBody = RequestBody.create(wholeObject.toString(), mediaType);
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer AAAAeRCNhko:APA91bHlOvBO9UpjipxlHHvdwz4srFwfM-NyliEmScAzRwSY-0b4kkX5OOR15egcJOWkFZXTk4cn3-0lF0OfP_RDRM_z9oGjdh5h_bDfI-fUgRw6LqCHoIR02Q53sRFQ8Nwnh-P6k4X2")
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("Network", "Error sending notification", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("Network", "Unsuccessful response: " + response);
                        // Handle unsuccessful response, e.g., show a toast to the user
                    } else {
                        Log.d("Notification", "Sent successfully!");
                    }
                }
            });
        }
    }

    // https://www.youtube.com/watch?v=vyt20Gg2Ckg
    public void milestoneNotify(Long milestone, String eventID) {

        database.accessEvent(eventID, event -> {
            if (event != null) {
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
            }
        });
    }
}
