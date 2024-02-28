package com.example.scandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to User objects and UserProfileImages
 * @author Jordan Beaubien
 */
public class UserDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference UserRef;

    private static CollectionReference UserProfileImageRef;

    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    public UserDBAccessor() {

        db = FirebaseFirestore.getInstance();

        UserRef = db.collection("Users");
        UserProfileImageRef = db.collection("UserProfileImages");
    }

    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Store or update User in Firestore Database
     * @param user User object to be added or updated.
     */
    public void storeEvent(User user) {
        // Store an Event with EventID as key
        UserRef.document(user.getUserID()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "User successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing User document", e);
                    }
                });
    }
}
