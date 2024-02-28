package com.example.scandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to Event objects, EventPosterImages and EventQRCodes.
 * @author Jordan Beaubien
 */
public class EventDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore EventDB;

    private static CollectionReference EventRef;

    private static CollectionReference EventPosterImageRef;

    private static CollectionReference EventQRCodeRef;

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    public EventDBAccessor() {

        EventDB = FirebaseFirestore.getInstance();

        EventRef = EventDB.collection("Events");
        EventPosterImageRef = EventDB.collection("PosterImages");
        EventQRCodeRef = EventDB.collection("QRCodeImages");
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Store or update Event in Firestore Database
     * @param event Event object to be added or updated.
     */
    public void storeEvent(Event event) {
        // Store an Event with EventID as key
        EventRef.document(event.getEventID()).set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Event successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing Event document", e);
                    }
                });
    }

    /* ------- *
     * GETTERS *
     * ------- */
    // TODO - .getEvent(String EventID)
    // TODO - .getEventPoster(String EventID)
    // TODO - .getEventQRCode(String EventID)
    // TODO - .getCollectionRefEvents
    // TODO - .getCollectionRefEventPosterImages
    // TODO - .getCollectionRefEventQRCodes


}
