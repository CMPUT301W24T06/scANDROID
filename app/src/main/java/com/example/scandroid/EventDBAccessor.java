package com.example.scandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private FirebaseFirestore db;

    private static CollectionReference EventRef;

//    private static CollectionReference EventPosterImageRef;
//
//    private static CollectionReference EventQRCodeRef;

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    public EventDBAccessor() {

        // Access Event collection of Firestore
        db = FirebaseFirestore.getInstance();
        EventRef = db.collection("Events");
//        EventPosterImageRef = db.collection("PosterImages");
//        EventQRCodeRef = db.collection("QRCodeImages");
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Delete an Event in Firestore Database
     * @param eventID Unique identifier for Event to be deleted
     */
    public void deleteEvent(String eventID) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
        // Delete an Event via EventID
        EventRef.document(eventID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Event successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error deleting Event", e);
                    }
                });
    }

    /**
     * Store or update Event in Firestore Database
     * @param event Event object to be added or updated.
     */
    public void storeEvent(Event event) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
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
    /**
     * Get Event stored in Firestore Database
     * @param eventID Unique identifier for Event to be accessed
     */
    public void getEvent(String eventID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an Event via EventID
        EventRef.document(eventID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }


    // TODO - .getEvent(String EventID)
    // TODO - .getEventPoster(String EventID)
    // TODO - .getEventQRCode(String EventID)
    // TODO - .getCollectionRefEvents
    // TODO - .getCollectionRefEventPosterImages
    // TODO - .getCollectionRefEventQRCodes


}