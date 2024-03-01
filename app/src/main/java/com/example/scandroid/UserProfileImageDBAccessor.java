package com.example.scandroid;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to UserProfileImages.
 * @author Jordan Beaubien
 */
public class UserProfileImageDBAccessor {

    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference UserProfileImageRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of UserProfileImageDBAccessor. <br>
     * Allows access to UserProfileImages stored as Bitmaps in a Firestore Database. <br>
     * Actions permitted: Access(get), Delete, and Store
     */
    public UserProfileImageDBAccessor() {

        // Access EventPoster collection of Firestore
        db = FirebaseFirestore.getInstance();
        UserProfileImageRef = db.collection("UserProfileImages");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Delete a UserProfileImage in Firestore Database
     * @param UserID Unique identifier for UserProfileImage to be deleted
     */
    public void deleteUserProfileImage(String UserID) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
        // Delete an UserProfileImage via UserID
        UserProfileImageRef.document(UserID).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "UserProfileImage successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting UserProfileImage", e));
    }

    /**
     * Store or update UserProfileImage in Firestore Database
     * @param UserID Unique identifier for UserProfileImage to be stored or updated
     * @param UserProfileImage Bitmap of UserProfileImage to be stored
     */
    public void storeUserProfileImage(String UserID, Bitmap UserProfileImage) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store a UserProfileImage with UserID as key
        UserProfileImageRef.document(UserID).set(UserProfileImage)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "UserProfileImage successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing UserProfileImage document", e));
    }

    /**
     * Get UserProfileImage stored in Firestore Database
     * @param UserID Unique identifier for UserProfileImage to be accessed
     */
    public Bitmap accessUserProfileImage(String UserID) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get a UserProfileImage via UserID
        final Bitmap[] retrievedProfileImage = new Bitmap[1];
        UserProfileImageRef.document(UserID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedProfileImage[0] = task.getResult().toObject(Bitmap.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedProfileImage[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedProfileImage[0] = null;
                    }
                });

        return retrievedProfileImage[0];
    }
}
