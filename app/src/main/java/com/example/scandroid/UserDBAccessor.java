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

    /**
     * Get User stored in Firestore Database
     * @param userID Unique identifier for Event
     */
    public void getUser(String userID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        UserRef.document(userID).get()
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
}
