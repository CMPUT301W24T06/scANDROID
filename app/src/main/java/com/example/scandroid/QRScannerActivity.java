package com.example.scandroid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * QRScannerActivity activity provides functionality for scanning QR Codes using the device's camera.
 * It initializes the camera, handles scanning event details and check-in information,
 * and subsequent user actions for attending or RSVPing to the event shown.
 * It extends AppCompatActivity in order to be compatible with
 * older versions of Android as well as use modern Android features.
 */
// credit for QR Scanner
// code sourced from https://www.youtube.com/watch?v=jtT60yFPelI
// source for navigation bar logic: https://www.youtube.com/watch?v=lOTIedfP1OA
// gradle dependency for switch case:https://stackoverflow.com/questions/76430646/constant-expression-required-when-trying-to-create-a-switch-case-block
// https://chat.openai.com/share/396352f1-a9ae-4df1-9cf4-9466658263d3
public class QRScannerActivity extends AppCompatActivity {
    BottomNavigationView navigationBar;
    Button qr_scanner_button;
    DBAccessor database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrscanner_activity);

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.qr_button);
        qr_scanner_button = findViewById(R.id.QR_scan_code_button);
        database = new DBAccessor();


        navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_button:
                        startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                        return true;
                    case R.id.qr_button:
                        return true;
                    case R.id.browse_button:
                        startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                        return true;
                    case R.id.notification_button:
                        startActivity(new Intent(getApplicationContext(), EventViewAnnouncementsActivity.class));
                        return true;

                }
                return false;
            }
        });

        qr_scanner_button.setOnClickListener(v -> {
            scanCode();
        });
    }

    /**
     * Opens the camera and scans QR codes
     * Before opening the QR Scanner, orientation, auditory confirmation, capture activity
     * and prompt are configured.
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Please center barcode in box to scan. Volume up for flash!");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            try {
                String userID = getIntent().getStringExtra("userID");

                // Parse the JSON string into a JSONObject
                JSONObject jsonObject = new JSONObject(result.getContents());

                // Extract eventID and isPromo from the JSONObject
                String eventID = jsonObject.getString("eventID");
                boolean isPromo = jsonObject.getBoolean("isPromo");

                database.accessEvent(eventID, event -> {
                    // Now you have both the event ID and the type of QR code
                    // You can perform the appropriate action based on the QR code type
                    Intent intent;
                    if (isPromo) {
                        // Handle promo QR code
                        System.out.println("This is a promo QR code for event ID: " + eventID);
                        intent = new Intent(QRScannerActivity.this, EventInfoActivity.class);
                    } else {
                        // Handle check-in QR code
                        System.out.println("This is a check-in QR code for event ID: " + eventID);
                        intent = new Intent(QRScannerActivity.this, EventCheckInActivity.class);
                    }

                    intent.putExtra("event", event);
                    intent.putExtra("eventID", eventID);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                });

            } catch (JSONException e) {
                // Handle JSON parsing error
                throw new RuntimeException("Error parsing QR code content", e);
            }
        }
    });
}
