package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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

        database.accessQRMain(event.getEventID(), new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    checkInQRCodeImgView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e) {
                // bitmap load failure
            }
        });

        database.accessQRPromo(event.getEventID(), new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    promoQRCodeImgView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e) {
                // bitmap load failure
            }
        });

        shareCheckInQRButton.setOnClickListener(v -> database.accessQRMain(event.getEventID(), new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    shareQRCode(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e) {
                // bitmap load failure
            }
        }));
        sharePromoQRButton.setOnClickListener(v -> database.accessQRMain(event.getEventID(), new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    shareQRCode(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e) {
                // bitmap load failure
            }
        }));
    }

    /**
     * Method for accessing a QR code from the database
     * and sharing it.
     */
    // Source: https://www.youtube.com/watch?v=_vqWgyuexmY
    private void shareQRCode(Bitmap QRcode) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + ".png");
        Intent shareInt;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            QRcode.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        startActivity(Intent.createChooser(shareInt, "Share QR Code"));
    }
}