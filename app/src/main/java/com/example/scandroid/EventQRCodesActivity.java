package com.example.scandroid;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * EventQRCodesActivity is an activity that displays
 * the Promo and Check-In QR Codes for an event, and allows the event
 * organizer to share the QR Codes to other apps or email.
 */
// source for generating qr code: https://youtu.be/n8HdrLYL9DA?si=u515CzkVDgDhSd3K
//                                https://chat.openai.com/share/396352f1-a9ae-4df1-9cf4-9466658263d3
//                                https://chat.openai.com/share/b2327949-844d-4167-8c4c-9240492b5db6

public class EventQRCodesActivity extends AppCompatActivity {
    DBAccessor database;
    Event event;
    ImageView checkInQRCodeImgView;
    ImageView promoQRCodeImgView;
    AppCompatButton backButton;
    AppCompatButton shareCheckInQRButton;
    AppCompatButton sharePromoQRButton;

    /**
     * This method generates QR Codes
     * that link to a Check-In and a Promo page for an event.
     *
     * @param eventID The eventID of the event that is being promoted and organized.
     */
    protected void generateQRCode(String eventID, boolean isPromo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventID", eventID);
            jsonObject.put("isPromo", isPromo);

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix;
            try {
                matrix = writer.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 650, 650);

                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(matrix);

                if (isPromo) {
                    database.storeQRPromo(eventID, bitmap);
                }
                if (!isPromo) {
                    database.storeQRMain(eventID, bitmap);
                }

            } catch (WriterException e) {
                throw new RuntimeException(e);
            }

        } catch (JSONException d) {
            throw new RuntimeException(d);
        }
    }

    /**
     * Method to share a QR code.
     *
     * @param context Context of the activity
     * @param bitmap  Bitmap of the QR code
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

            // CHECK FOR QRMAIN
            database.accessQRMain(eventID, new BitmapCallback() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    // If the QR code exists
                    if (bitmap != null) {
                        checkInQRCodeImgView.setImageBitmap(bitmap);
                        shareCheckInQRButton.setOnClickListener(v -> {
                            shareQRCode(v.getContext(), bitmap);
                        });
                    }
                }

                @Override
                public void onBitmapFailed(Exception e) {
                    GenerateQRCodesTask generateCheckInQRCodeTask = new GenerateQRCodesTask() {
                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);

                            database.storeQRMain(eventID, bitmap);
                            // Update UI with generated Check-In QR code
                            checkInQRCodeImgView.setImageBitmap(bitmap);
                            shareCheckInQRButton.setOnClickListener(v -> {
                                shareQRCode(v.getContext(), bitmap);
                            });
                        }
                    };
                    generateCheckInQRCodeTask.execute(new Pair<>(eventID, false));
                }
            });

            // CHECK FOR QRPROMO
            database.accessQRPromo(eventID, new BitmapCallback() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    // If the QR code exists
                    if (bitmap != null) {
                        promoQRCodeImgView.setImageBitmap(bitmap);
                        sharePromoQRButton.setOnClickListener(v -> {
                            shareQRCode(v.getContext(), bitmap);
                        });
                    }
                }

                @Override
                public void onBitmapFailed(Exception e) {
                    GenerateQRCodesTask generatePromoQRCodeTask = new GenerateQRCodesTask() {
                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);

                            database.storeQRPromo(eventID, bitmap);
                            // Update UI with generated Promo QR code
                            promoQRCodeImgView.setImageBitmap(bitmap);
                            sharePromoQRButton.setOnClickListener(v -> {
                                shareQRCode(v.getContext(), bitmap);
                            });
                        }
                    };
                    generatePromoQRCodeTask.execute(new Pair<>(eventID, true));
                }
            });
        }
    }

    static class GenerateQRCodesTask extends AsyncTask<Pair<String, Boolean>, Void, Bitmap> {

        public GenerateQRCodesTask() {
        }

        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(Pair<String, Boolean>... params) {
            String eventID = params[0].first;
            boolean isPromo = params[0].second;

            return generateQRCodeTask(eventID, isPromo);
        }

        private Bitmap generateQRCodeTask(String eventID, boolean isPromo) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("eventID", eventID);
                jsonObject.put("isPromo", isPromo);

                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix matrix;
                try {
                    matrix = writer.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 650, 650);

                    BarcodeEncoder encoder = new BarcodeEncoder();

                    return encoder.createBitmap(matrix);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

            } catch (JSONException d) {
                throw new RuntimeException(d);
            }
        }
    }
}