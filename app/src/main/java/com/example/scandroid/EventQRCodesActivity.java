package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Represents the screen where event organizers can share the Event Check-In and Promo QR Codes.
 * @author Aaliyah Wusu
 */

public class EventQRCodesActivity extends AppCompatActivity {
    DBAccessor database;
    Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_qr_codes_activity);

        ImageView checkInQRCodeImgView = findViewById(R.id.check_in_qr_code_img);
        ImageView promoQRCodeImgView = findViewById(R.id.promo_qr_code_img);
        AppCompatButton shareCheckInQRButton = findViewById(R.id.share_check_in_qr_button);
        AppCompatButton sharePromoQRButton = findViewById(R.id.share_promo_qr_button);

        event = (Event)getIntent().getSerializableExtra("event");
        database = new DBAccessor();
        Bitmap checkInQRCodeImg = database.accessQRMain(event.getEventID());
        Bitmap promoQRCodeImg = database.accessQRPromo(event.getEventID());

        checkInQRCodeImgView.setImageBitmap(checkInQRCodeImg);
        promoQRCodeImgView.setImageBitmap(promoQRCodeImg);

        shareCheckInQRButton.setOnClickListener(v -> shareCheckInQRCode());

        sharePromoQRButton.setOnClickListener(v -> sharePromoQRCode());
    }

    /**
     * Method for accessing the check in QR code from the database
     * and sharing it.
     */
    // Source: https://www.youtube.com/watch?v=_vqWgyuexmY
    private void shareCheckInQRCode() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        event = (Event)getIntent().getSerializableExtra("event");
        database = new DBAccessor();
        Bitmap checkInQRCodeImg = database.accessQRMain(event.getEventID());

        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + ".png");
        Intent shareInt;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            checkInQRCodeImg.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        startActivity(Intent.createChooser(shareInt, "Share Check-In QR Code"));
    }

    /**
     * Method for accessing the promo QR code from the database
     * and sharing it.
     */
    // Source: https://www.youtube.com/watch?v=_vqWgyuexmY
    private void sharePromoQRCode() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        event = (Event)getIntent().getSerializableExtra("event");
        database = new DBAccessor();
        Bitmap promoQRCodeImg = database.accessQRPromo(event.getEventID());

        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + ".png");
        Intent shareInt;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            promoQRCodeImg.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        startActivity(Intent.createChooser(shareInt, "Share Promo QR Code"));
    }
}