package com.example.scandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TimePickerDialog;

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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

/**
 * EventCreateAnnouncementActivity allows an event organizer
 * create notifications and schedule for them to be sent
 * to event attendees.
 */
public class EventCreateAnnouncementActivity extends AppCompatActivity {
    AppCompatButton backButton;
    EditText editNotificationTitle;
    EditText editNotificationInfo;
    TextView editNotificationTime;
    Calendar calendar = Calendar.getInstance();
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
        editNotificationTime = findViewById(R.id.edit_notification_time);
        sendNotificationButton = findViewById(R.id.send_notification_button);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        editNotificationTime.setText(String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute));

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
                String title = editNotificationTitle.getText().toString();
                // add the event name so we know what event the notification is associated with
                String notificationTitle = event.getEventName() + ": " + title;
                String notificationBody = editNotificationInfo.getText().toString();
                String notificationTime = editNotificationTime.getText().toString();
                String[] parts = notificationTime.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, 0);

                Time time = new Time(calendar.getTimeInMillis());

                // Handle user input validation
                if (handleUserInput(title, notificationBody)) {
                    Log.d("Notification", "fcmTokenList size: " + fcmTokenList.size()); // message for testing
                    event.addEventAnnouncement(notificationTitle, notificationBody, time); // add to db
                    sendNotification(notificationTitle, notificationBody, fcmTokenList);
                    dbAccessor.storeEvent(event);
                }
                finish();
            }
        });
        backButton.setOnClickListener(v -> finish());
    }
    /**
     * Validates user input for notification creation.
     *
     * @param notificationTitle Title of the notification.
     * @param notificationBody  Body/content of the notification.
     * @return True if the input is valid, false otherwise.
     */
    private boolean handleUserInput(String notificationTitle, String notificationBody){
        boolean isValid = true;

        // check if an event name is a string and if it is valid
        if (notificationTitle.isEmpty() || notificationTitle.length() > 20) {
            showToast("Please enter a valid notification title (up to 20 characters).");
            isValid = false;
            Log.d("Validation", "Notification title validation failed");
        }
        // check if the user provided a description for their event
        if (notificationBody.isEmpty()) {
            showToast("Please enter content for your notification.");
            isValid = false;
            Log.d("Validation", "Notification body failed");
        }
        return isValid;
    }

    //Sources
    // OpenAI ChatGPT 2024, How do I send a notification to a list of tokens not just one
    // https://www.youtube.com/watch?v=YjNZO90yVsE
    // https://www.youtube.com/watch?v=6_t87WW6_Gc
    /**
     * Sends a notification to the specified FCM tokens.
     *
     * @param title           Title of the notification.
     * @param body            Body/content of the notification.
     * @param receiverTokens  List of FCM tokens to which the notification will be sent.
     */
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
    /**
     * Displays a toast message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        StyleableToast.makeText(this, message, R.style.customToast).show();
    }
}
