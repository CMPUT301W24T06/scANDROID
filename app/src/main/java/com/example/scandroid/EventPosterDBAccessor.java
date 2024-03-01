package com.example.scandroid;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to EventPoster's.
 * @author Jordan Beaubien
 */
public class EventPosterDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference EventPosterRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of EventPosterDBAccessor. <br>
     * Allows access to EventPoster's stored as Bitmaps in a Firestore Database. <br>
     * Actions permitted: Access(get), Delete, and Store
     */
    public EventPosterDBAccessor() {

        // Access EventPoster collection of Firestore
        db = FirebaseFirestore.getInstance();
        EventPosterRef = db.collection("EventPoster");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Delete an EventPoster in Firestore Database
     * @param EventID Unique identifier for EventPoster to be deleted
     */
    public void deleteEventPoster(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
        // Delete an EventPoster via EventID
        EventPosterRef.document(EventID).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting EventPoster", e));
    }

    /**
     * Store or update EventPoster in Firestore Database
     * @param EventID Unique identifier for EventPoster to be accessed
     * @param EventPoster Bitmap of EventPoster to be stored.
     */
    public void storeEventPoster(String EventID, Bitmap EventPoster) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store an EventPoster with EventID as key
        EventPosterRef.document(EventID).set(EventPoster)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing EventPoster document", e));
    }

    /**
     * Get EventPoster stored in Firestore Database
     * @param EventID Unique identifier for EventPoster to be accessed
     */
    public Bitmap accessEventPoster(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an EventPoster via EventID
        final Bitmap[] retrievedPoster = new Bitmap[1];
        EventPosterRef.document(EventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedPoster[0] = task.getResult().toObject(Bitmap.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedPoster[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedPoster[0] = null;
                    }
                });

        return retrievedPoster[0];
    }
}

