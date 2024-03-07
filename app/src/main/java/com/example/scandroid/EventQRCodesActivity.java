package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Represents the screen where event organizers can share the Event Check-In and Promo QR Codes.
 * @author Aaliyah Wusu and Moyo Dawodu
 */

public class EventQRCodesActivity extends AppCompatActivity {
    DBAccessor database;
    Event event;
    ImageView checkInQRCodeImgView;
    ImageView promoQRCodeImgView;
    AppCompatButton backButton;
    AppCompatButton shareCheckInQRButton;
    AppCompatButton sharePromoQRButton;

    public void generatePromoQR(String eventID, DBAccessor dbAccessor){
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventID, BarcodeFormat.QR_CODE,promoQRCodeImgView.getHeight(),promoQRCodeImgView.getWidth());
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            dbAccessor.storeQRPromo(eventID,bitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO - what do we want encoded in the QR code for check ins?
    // IMPORTANT - most of the work for the QR codes is actually done after you click the button, 'scan'
    public void generateCheckInQR(String eventID, DBAccessor dbAccessor){
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventID, BarcodeFormat.QR_CODE,checkInQRCodeImgView.getHeight(),checkInQRCodeImgView.getWidth());
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            dbAccessor.storeQRMain(eventID,bitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkForCheckInQR(String eventID, DBAccessor database) {
        database.accessQRMain(eventID, new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                // do not generate a QR code
            }
            @Override
            public void onBitmapFailed(Exception e) {
                generateCheckInQR(eventID, database);
                checkForCheckInQR(eventID, database);

            }
        });
    }

    public void checkForPromoQR(String eventID, DBAccessor database) {
        database.accessQRPromo(eventID, new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                // do not generate a QR code
            }
            @Override
            public void onBitmapFailed(Exception e) {
                generatePromoQR(eventID, database);
                checkForPromoQR(eventID, database);
            }
        });
    }

    /**
     * Method for sharing a QR Code.
     * @param qrCodeToShare Bitmap of the QR code to be shared
     */
    // Source: https://www.youtube.com/watch?v=_vqWgyuexmY
    private void shareQRCode(Bitmap qrCodeToShare) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        event = (Event) getIntent().getSerializableExtra("event");
        database = new DBAccessor();

        File f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name)+ "QRcode" + ".png");
        Intent shareInt;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            qrCodeToShare.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_qr_codes_activity);

        backButton = findViewById(R.id.back_button);
        checkInQRCodeImgView = findViewById(R.id.check_in_qr_code_img);
        promoQRCodeImgView = findViewById(R.id.promo_qr_code_img);
        shareCheckInQRButton = findViewById(R.id.share_check_in_qr_button);
        sharePromoQRButton = findViewById(R.id.share_promo_qr_button);

        backButton.setOnClickListener(v -> finish());

        event = (Event)getIntent().getSerializableExtra("event");
        database = new DBAccessor();

        if (event != null){
            String eventID = event.getEventID();

            checkForCheckInQR(eventID,database);
            checkForPromoQR(eventID,database);

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
            sharePromoQRButton.setOnClickListener(v -> database.accessQRPromo(event.getEventID(), new BitmapCallback() {
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
    }
}