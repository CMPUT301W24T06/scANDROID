package com.example.scandroid;

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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;

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
                switch (item.getItemId()){
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

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Temporary Message");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    // replace this with our actual content class
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->{
        if(result.getContents() != null){
            String eventID = result.getContents();
            //TODO : IF QR IS PROMO
//          Intent intent = new Intent(QRScannerActivity.this, EventInfoActivity.class);
//          intent.putExtra("eventID", eventID);
//          startActivity(intent);

            // IF QR IS CHECK IN:
            // source: https://stackoverflow.com/a/15392591
            DialogFragment checkInPrompt = new EventCheckInFragment();
            Bundle bundle = new Bundle();
            bundle.putString("eventID", eventID);
            checkInPrompt.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, checkInPrompt);
            transaction.commit();

//            AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);
//            builder.setTitle("Content");
//            builder.setMessage(result.getContents());
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();
        }
    });
}
