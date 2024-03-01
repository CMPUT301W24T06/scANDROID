package com.example.scandroid;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to Event main and Event promotional QRCodes.
 * @author Jordan Beaubien
 */
public class QRCodeDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference QRCodeMainRef;
    private static CollectionReference QRCodePromoRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of QRCodeDBAccessor. <br>
     * Allows access to QRCode's stored as Bitmaps in a Firestore Database. <br>
     * Actions permitted: Access(get) and Store
     */
    public QRCodeDBAccessor() {

        // Access ImageAssets collection of Firestore
        db = FirebaseFirestore.getInstance();
        QRCodeMainRef = db.collection("QRCodeMain");
        QRCodePromoRef = db.collection("QRCodePromo");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Store or update an Events main QR code in Firestore Database
     * @param EventID Unique identifier for QRCode to be accessed
     * @param QRMain Bitmap of QRMain to be stored.
     */
    public void storeQRMain(String EventID, Bitmap QRMain) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store a QRMain with EventID as key
        QRCodeMainRef.document(EventID).set(QRMain)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRMain successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing QRMain document", e));
    }

    /**
     * Store or update an Events promotional QR code in Firestore Database
     * @param EventID Unique identifier for QRCode to be accessed
     * @param QRPromo Bitmap of QRPromo to be stored.
     */
    public void storeQRPromo(String EventID, Bitmap QRPromo) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store a QRPromo with EventID as key
        QRCodePromoRef.document(EventID).set(QRPromo)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRPromo successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing QRPromo document", e));
    }

    /**
     * Get main QR code for an Event stored in Firestore Database
     * @param EventID Unique identifier for QRCode to be accessed
     */
    public Bitmap accessQRMain(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an QRMain via EventID
        final Bitmap[] retrievedQRMain = new Bitmap[1];
        QRCodeMainRef.document(EventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedQRMain[0] = task.getResult().toObject(Bitmap.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedQRMain[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedQRMain[0] = null;
                    }
                });

        return retrievedQRMain[0];
    }

    /**
     * Get promotional QR code for an Event stored in Firestore Database
     * @param EventID Unique identifier for promo QRCode to be accessed
     */
    public Bitmap accessQRPromo(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an QRPromo via EventID
        final Bitmap[] retrievedQRPromo = new Bitmap[1];
        QRCodePromoRef.document(EventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedQRPromo[0] = task.getResult().toObject(Bitmap.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedQRPromo[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedQRPromo[0] = null;
                    }
                });

        return retrievedQRPromo[0];
    }
}
