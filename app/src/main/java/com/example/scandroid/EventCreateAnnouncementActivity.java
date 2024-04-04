package com.example.scandroid;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
        Event event = (Event)getIntent().getSerializableExtra("event");

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

            // Assuming accessUser method takes a UserCallback as the second argument
            dbAccessor.accessUser(userID, user -> {
                if (user != null) {
                    String fcmToken = user.getFCMToken();
                    if (fcmToken != null && !fcmToken.isEmpty()) {
                        fcmTokenList.add(fcmToken);
                    }
                }
                // adding my token to test notifications
                fcmTokenList.add("dPjr4-CbQaWMmXc-wppTsN:APA91bHTMdX04rlBvrltUCDSzkACgXNS-zyqTMMlyATv8LKXLBuPg-ekPE4oX0yO-Tquf2QuWELZwUIn9cSzBBlYWe0eERV1qyvAoe3n8zG_OZrX1Cbrzpy2QNyQh3gT5M6FQnktMBg6");
            });

            sendNotificationButton.setOnClickListener(v -> {
                // Get notification title and message from EditText fields
                String title = editNotificationTitle.getText().toString();
                String message = editNotificationInfo.getText().toString();

                // Check if the title and message are not empty
                if (!title.isEmpty() && !message.isEmpty()) {
                    // Create an instance of FCMNotificationService
                    FCMNotificationService fcmService = new FCMNotificationService();

                    // Call sendNotification() method with the FCM token list, title, and message
                    fcmService.sendNotification(fcmTokenList, title, message);

                    // Show a toast indicating that the notification was sent
                    Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a toast indicating that title or message is empty
                    Toast.makeText(this, "Please enter both title and message.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // title should be the title of the event name by default (as per UI)
        // get sender's name and attach (as per UI)
        // timestamp will be time now - time sent
        // get all those attendees fcm tokens
        // send the notification
        backButton.setOnClickListener(v -> finish());
    }
}
