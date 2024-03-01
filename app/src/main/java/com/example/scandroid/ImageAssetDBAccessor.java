package com.example.scandroid;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * Allows storage of and access to ImageAsset's.
 * @author Jordan Beaubien
 */
public class ImageAssetDBAccessor {
    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    private static CollectionReference ImageAssetRef;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of ImageAssetDBAccessor. <br>
     * Allows access to ImageAsset's stored as Bitmaps in a Firestore Database. <br>
     * Actions permitted: Access(get) and Store
     */
    public ImageAssetDBAccessor() {

        // Access ImageAssets collection of Firestore
        db = FirebaseFirestore.getInstance();
        ImageAssetRef = db.collection("ImageAssets");
    }


    /* ------- *
     * METHODS *
     * ------- */
    /**
     * Store or update ImageAsset in Firestore Database
     * @param imageAssetName Name of ImageAsset to be added or updated.
     */
    public void storeImageAsset(String imageAssetName, Bitmap bitmap) {
        // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
        // Store an ImageAsset with imageAssetName as key
        ImageAssetRef.document(imageAssetName).set(bitmap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "ImageAsset successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing ImageAsset document", e));
    }

    /**
     * Get ImageAsset stored in Firestore Database
     * @param imageAssetName Unique identifier for ImageAsset to be accessed
     */
    public Bitmap accessImageAsset(String imageAssetName) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an ImageAsset via imageAssetName
        final Bitmap[] retrievedImageAsset = new Bitmap[1];
        ImageAssetRef.document(imageAssetName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                            retrievedImageAsset[0] = task.getResult().toObject(Bitmap.class);
                        } else {
                            Log.d("Firestore", "No such document");
                            retrievedImageAsset[0] = null;
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                        retrievedImageAsset[0] = null;
                    }
                });

        return retrievedImageAsset[0];
    }
}
