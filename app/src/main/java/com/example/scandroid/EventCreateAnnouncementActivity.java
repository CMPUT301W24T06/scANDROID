package com.example.scandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.io.IOException;
import java.util.ArrayList;

/**
 * EventCreateAnnouncementActivity allows an event organizer
 * create notifications and schedule for them to be sent
 * to event attendees.
 */
public class EventCreateAnnouncementActivity extends AppCompatActivity {
    AppCompatButton backButton;
    EditText editNotificationTitle;
    EditText editNotificationInfo;
    EditText editNotificationMinutes;
    EditText editNotificationHour;
    EditText editNotificationPeriods;
    AppCompatButton sendNotificationButton;
    DBAccessor dbAccessor = new DBAccessor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = (Event) getIntent().getSerializableExtra("event");

        setContentView(R.layout.event_create_announcement_activity);
        backButton = findViewById(R.id.back_button);
        editNotificationTitle = findViewById(R.id.edit_notification_title);
        editNotificationInfo = findViewById(R.id.edit_notification_info);
        editNotificationMinutes = findViewById(R.id.notification_minutes);
        editNotificationHour = findViewById(R.id.notification_hour);
        editNotificationPeriods = findViewById(R.id.notification_periods);
        sendNotificationButton = findViewById(R.id.send_notification_button);

        // get all the attendees with getEventAttendeeList()
        assert event != null;
        ArrayList<Event.CheckIn> attendeeList = event.getEventAttendeeList();
        // create a list to store FCM tokens
        ArrayList<String> fcmTokenList = new ArrayList<>();

        for (Event.CheckIn attendee : attendeeList) {
            // get all attendees FCMTokens and store
            // Assuming each attendee has a userID
            String userID = attendee.getUserID();

            // get all the ids of the attendees and their fcmtokens
            dbAccessor.accessUser(userID, user -> {
                if (user != null) {
                    String fcmToken = user.getFCMToken();
                    if (fcmToken != null && !fcmToken.isEmpty()) {
                        fcmTokenList.add(fcmToken);
                    }
                }
            });
        }

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // you can replace with your own fcmToken if you wanna test it because the attendee list is glitching rn
//                fcmTokenList.add("dPjr4-CbQaWMmXc-wppTsN:APA91bHTMdX04rlBvrltUCDSzkACgXNS-zyqTMMlyATv8LKXLBuPg-ekPE4oX0yO-Tquf2QuWELZwUIn9cSzBBlYWe0eERV1qyvAoe3n8zG_OZrX1Cbrzpy2QNyQh3gT5M6FQnktMBg6");
                Log.d("Notification", "fcmTokenList size: " + fcmTokenList.size()); // message for testing
                sendNotification(editNotificationTitle.getText().toString(), editNotificationInfo.getText().toString(), fcmTokenList);
            }
        });

        // title should be the title of the event name by default (as per UI)
        // get sender's name and attach (as per UI)
        // timestamp will be time now - time sent
        // get all those attendees fcm tokens
        // send the notification
        backButton.setOnClickListener(v -> finish());
    }

    //Sources
    // OpenAI ChatGPT 2024, How do I send a notification to a list of tokens not just one
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
}
