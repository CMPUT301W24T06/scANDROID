package com.example.scandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class FCMNotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCMNotificationService";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle incoming FCM messages here
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Message Received: " + remoteMessage.getData());
            // Handle data messages
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Received: " + remoteMessage.getNotification().getBody());
            // Handle notification messages
        }
    }

    public void sendNotification(ArrayList<String> fcmTokenList, String title, String message) {
        for (String token : fcmTokenList) {
            try {
                // Construct the message payload as a map
                Map<String, String> data = new HashMap<>();
                data.put("title", title);
                data.put("message", message);

                // Send the message
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(token)
                        .setData(data)
                        .build());

                Log.d(TAG, "Message sent successfully to: " + token);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message to: " + token, e);
            }
        }
    }
}
