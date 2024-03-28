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
public class QRScannerActivity extends AppCompatActivity {
    BottomNavigationView navigationBar;
    Button qr_scanner_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrscanner_activity);

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.qr_button);
        qr_scanner_button = findViewById(R.id.QR_scan_code_button);

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
            String eventID = result.getContents();
            String userID = getIntent().getStringExtra("userID");

            Intent intent = new Intent(QRScannerActivity.this, CheckInOrPromoActivity.class);
            intent.putExtra("eventID", eventID);
            intent.putExtra("userID", userID);
            startActivity(intent);



//
//            AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);
//            builder.setTitle("Promo or Check-In?");
//
//
//            builder.setNeutralButton("Promo", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // If the QR Code is for looking at promo for an event.
//                    Intent intent = new Intent(QRScannerActivity.this, EventInfoActivity.class);
//                    intent.putExtra("eventID", eventID);
//                    startActivity(intent);
//                    // dialog.dismiss();
//                }
//            }).show();
//            builder.setNeutralButton("Check-In", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // If the QR Code is for checking into an event.
//                    // source: https://stackoverflow.com/a/15392591
//                    DialogFragment checkInPrompt = new EventCheckInFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("eventID", eventID);
//                    checkInPrompt.setArguments(bundle);
//
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.add(android.R.id.content, checkInPrompt);
//                    transaction.commit();
//                }
//            });
        }
    });
}
