package com.example.scandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * CheckInOrPromoFragment is a fragment shown
 * when a user scans a QR code, to tell the app whether
 * the information for the event they're scanning for
 * is promo or check-in.
 */
public class CheckInOrPromoActivity extends AppCompatActivity {
    private DBAccessor database;
    private AppCompatButton checkInButton;
    private AppCompatButton promoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_or_promo_activity);
        checkInButton = findViewById(R.id.check_in_frag_button);
        promoButton = findViewById(R.id.promo_frag_button);

        database = new DBAccessor();
        String eventID = (String) getIntent().getSerializableExtra("eventID");
        String userID = getIntent().getStringExtra("userID");

        database.accessEvent(eventID, event -> {
            if (event != null) {
                checkInButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CheckInOrPromoActivity.this, EventCheckInActivity.class);
                    intent.putExtra("eventID", eventID);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    finish();
                });
                promoButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CheckInOrPromoActivity.this, EventInfoActivity.class);
                    intent.putExtra("eventID", eventID);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}