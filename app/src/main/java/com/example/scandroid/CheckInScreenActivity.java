package com.example.scandroid;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.widget.Button;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;



public class CheckInScreenActivity extends AppCompatActivity {
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_screen_activity);
        ImageView backgroundImageView = findViewById(R.id.check_in_image);
        backgroundImageView.setBackgroundResource(R.drawable.check_in_image);
        Button checkInButton = findViewById(R.id.check_in_button);

        // Obtain the necessary user information
        String userId = new DeviceIDRetriever(CheckInScreenActivity.this).getDeviceId();
        String userName = ""; // Set the user's name
        String userPhoneNumber = ""; // Set the user's phone number
        String userAboutMe = ""; // Set the user's about me information
        String userEmail = ""; // Set the user's email

        DBAccessor database = new DBAccessor();
        database.accessUser(userId, user -> {
            currentUser = user;
            if (currentUser == null) {
                //Create a new User object
                User newUser = new User(userId, userName, userPhoneNumber, userAboutMe, userEmail);
                database.storeUser(newUser); // Add the user to Firestore
            }
        });
        // Create a new User object
        User newUser = new User(userId, userName, userPhoneNumber, userAboutMe, userEmail);

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle check-in button click
                Intent intent = new Intent(CheckInScreenActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });
    }

}