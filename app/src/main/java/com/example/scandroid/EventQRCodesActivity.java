package com.example.scandroid;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * EventQRCodesActivity is an activity that displays
 * the Promo and Check-In QR Codes for an event, and allows the event
 * organizer to share the QR Codes to other apps or email.
 */
// source for generating qr code: https://youtu.be/n8HdrLYL9DA?si=u515CzkVDgDhSd3K

public class EventQRCodesActivity extends AppCompatActivity {
    DBAccessor database;
    Event event;
    ImageView checkInQRCodeImgView;
    ImageView promoQRCodeImgView;
    AppCompatButton backButton;
    AppCompatButton shareCheckInQRButton;
    AppCompatButton sharePromoQRButton;

    /**
     * This method generates a QR Code
     * that links to a Promo page for an event.
     * @param eventID The eventID of the event that is being promoted.
     * @param dbAccessor DBAccessor, interface between scANDROID and Firestore database.
     */
    public void generatePromoQR(String eventID, DBAccessor dbAccessor) {
        // TODO: need to differentiate between promo and check in qr code
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventID, BarcodeFormat.QR_CODE, 650, 650);
            // removed: promoQRCodeImgView.getWidth(), promoQRCodeImgView.getHeight()
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            dbAccessor.storeQRPromo(eventID, bitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method generates a QR Code
     * that links to a check-in fragment for an event.
     * @param eventID The eventID of the event that is being shared.
     * @param dbAccessor DBAccessor, interface between scANDROID and Firestore database.
     */
    public void generateCheckInQR(String eventID, DBAccessor dbAccessor) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventID, BarcodeFormat.QR_CODE, 650, 650);
            // removed: checkInQRCodeImgView.getWidth(), checkInQRCodeImgView.getHeight()

            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            dbAccessor.storeQRMain(eventID, bitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This function checks if a Check-In QR code
     * already exists for the event in the database,
     * and if not it will generate a new one.
     * @param eventID The eventID of the event that is being shared.
     * @param database DBAccessor, interface between scANDROID and Firestore database.
     */
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

    /**
     * This function checks if a Promo QR code
     * already exists for the event in the database,
     * and if not it will generate a new one.
     * @param eventID The eventID of the event that is being promoted.
     * @param database DBAccessor, interface between scANDROID and Firestore database.
     */
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
     * Method to share a QR code.
     * @param context Context of the activity
     * @param bitmap Bitmap of the QR code
     */
    // sources: https://stackoverflow.com/questions/35966487/how-to-share-image-to-social-media-with-bitmap,
    // https://stackoverflow.com/a/35966511
    private void shareQRCode(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bitmap, event.getEventName() + " QR Code", null);

        Uri uri = Uri.parse(path);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, "You're Invited!");
        context.startActivity(Intent.createChooser(share, event.getEventName() + " QR Code"));
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

        event = (Event) getIntent().getSerializableExtra("event");
        database = new DBAccessor();

        if (event != null) {
            String eventID = event.getEventID();

            checkForCheckInQR(eventID, database);
            checkForPromoQR(eventID, database);

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
                        shareQRCode(v.getContext(), bitmap);
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
                        shareQRCode(v.getContext(), bitmap);
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