package com.example.scandroid;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage and access of Event objects.
 * @author Jordan Beaubien
 */
public class EventDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference EventRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of EventDBAccessor. <br>
     * Allows access to Event's stored in a Firestore Database. <br>
     * Actions permitted: Access(get), Delete, and Store
     */
    public EventDBAccessor() {

        // Access Event collection of Firestore
        db = FirebaseFirestore.getInstance();
        EventRef = db.collection("Events");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Delete an Event in Firestore Database
     * @param EventID Unique identifier for Event to be deleted
     */
    public void deleteEvent(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
        // Delete an Event via EventID
        EventRef.document(EventID).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting Event", e));
    }

    /**
     * Store or update Event in Firestore Database
     * @param event Event object to be added or updated.
     */
    public void storeEvent(Event event) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store an Event with EventID as key
        EventRef.document(event.getEventID()).set(event)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing Event document", e));
    }

    /**
     * Get Event stored in Firestore Database
     * @param EventID Unique identifier for Event to be accessed
     * @return Event that matches EventID if exists in Firestore Database
     */
    public Event accessEvent(String EventID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an Event via EventID
        final Event[] retrievedEvent = new Event[1];
        EventRef.document(EventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedEvent[0] = task.getResult().toObject(Event.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedEvent[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedEvent[0] = null;
                    }
                });

        return retrievedEvent[0];
    }
}
