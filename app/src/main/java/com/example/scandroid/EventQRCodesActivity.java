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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * EventQRCodesActivity is an activity that displays
 * the Promo and Check-In QR Codes for an event, and allows the event
 * organizer to share the QR Codes to other apps or email.
 */
// source for generating qr code: https://youtu.be/n8HdrLYL9DA?si=u515CzkVDgDhSd3K
//                                https://chat.openai.com/share/396352f1-a9ae-4df1-9cf4-9466658263d3

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
     * @param eventID    The eventID of the event that is being promoted and organized.
     * @param dbAccessor DBAccessor, interface between scANDROID and Firestore database.
     */
    public void generateQRCodes(String eventID, DBAccessor dbAccessor) {

        JSONObject jsonObjectMain = new JSONObject();
        try {
            jsonObjectMain.put("eventID", eventID);
            jsonObjectMain.put("isPromo", false);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObjectPromo = new JSONObject();
        try {
            jsonObjectPromo.put("eventID", eventID);
            jsonObjectPromo.put("isPromo", true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrixMain = writer.encode(jsonObjectMain.toString(), BarcodeFormat.QR_CODE, 650, 650);
            BitMatrix matrixPromo = writer.encode(jsonObjectPromo.toString(), BarcodeFormat.QR_CODE, 650, 650);

            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmapMain = encoder.createBitmap(matrixMain);
            Bitmap bitmapPromo = encoder.createBitmap(matrixPromo);

            database.storeQRPromo(eventID, bitmapPromo);
            database.storeQRMain(eventID, bitmapMain);
        } catch (WriterException ex) {
            throw new RuntimeException(ex);
        }
    }

        /**
         * This function checks if a Check-In QR code
         * already exists for the event in the database,
         * and if not it will generate a new one.
         *
         * @param eventID  The eventID of the event that is being shared.
         * @param isPromo  A boolean for whether the QR code links to a promo or check-in.
         * @param database DBAccessor, interface between scANDROID and Firestore database.
         */
        public void checkForQR (String eventID, boolean isPromo, DBAccessor database){
            if (isPromo) {
                database.accessQRPromo(eventID, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        // If the QR code exists, do nothing
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        // If the QR code does not exist, generate it

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("eventID", eventID);
                            jsonObject.put("isPromo", false);
                        } catch (JSONException d) {
                            throw new RuntimeException(e);
                        }

                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writer.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 650, 650);

                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);

                            database.storeQRPromo(eventID, bitmap);
                        } catch (WriterException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
            } else {
                database.accessQRMain(eventID, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        // If the QR code exists, do nothing
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        // If the QR code does not exist, generate it

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("eventID", eventID);
                            jsonObject.put("isPromo", true);
                        } catch (JSONException d) {
                            throw new RuntimeException(e);
                        }

                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writer.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 650, 650);

                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);

                            database.storeQRPromo(eventID, bitmap);
                        } catch (WriterException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
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
        private void shareQRCode (Context context, Bitmap bitmap){
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
        protected void onCreate (Bundle savedInstanceState){
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

                checkForQR(eventID, false, database);
                checkForQR(eventID, true, database);

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