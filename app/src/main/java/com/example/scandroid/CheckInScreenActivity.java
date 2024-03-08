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


/**
 * The {@link AppCompatActivity} representing the Check-In screen activity.
 * This activity allows the user to perform check-in actions.
 */
public class CheckInScreenActivity extends AppCompatActivity {
    User currentUser;
    /**
     * Called when the activity is starting. Calling {@code setContentView(int)} to inflate the activity's UI,
     * using {@code findViewById} to programmatically interact with widgets in the UI,
     * and calling additional methods to configure the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_screen_activity);
        ImageView backgroundImageView = findViewById(R.id.check_in_image);
        backgroundImageView.setBackgroundResource(R.drawable.check_in_image);
        Button checkInButton = findViewById(R.id.check_in_button);

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