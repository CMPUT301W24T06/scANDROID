package com.example.scandroid;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage and access of User objects
 * @author Jordan Beaubien
 */
public class UserDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference UserRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of UserDBAccessor. <br>
     * Allows access to User's stored in a Firestore Database. <br>
     * Actions permitted: Access(get), Delete, and Store
     */
    public UserDBAccessor() {

        // Access User collection of Firestore
        db = FirebaseFirestore.getInstance();
        UserRef = db.collection("Users");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Delete a User in Firestore Database
     * @param UserID Unique identifier for User
     */
    public void deleteUser(String UserID) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
        // Delete a User via UserID
        UserRef.document(UserID).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting User", e));
    }

    /**
     * Store or update a User in Firestore Database
     * @param user User object to be added or updated.
     */
    public void storeUser(User user) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store a User with UserID as key
        UserRef.document(user.getUserID()).set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing User document", e));
    }

    /**
     * Get User stored in Firestore Database
     * @param UserID Unique identifier for User
     * @return User that matches UserID if exists in Firestore Database
     */
    public User accessUser(String UserID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get a User via UserID
        final User[] retrievedUser = new User[1];
        UserRef.document(UserID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedUser[0] = task.getResult().toObject(User.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedUser[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedUser[0] = null;
                    }
                });

        return retrievedUser[0];
    }
}
