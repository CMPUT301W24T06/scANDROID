package com.example.scandroid;

import android.graphics.Bitmap;
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "ImageAsset successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing ImageAsset document", e);
                    }
                });
    }

    /* ------- *
     * GETTERS *
     * ------- */
    /**
     * Get ImageAsset stored in Firestore Database
     * @param imageAssetName Unique identifier for ImageAsset to be accessed
     */
    public void getImageAsset(String imageAssetName) {
        // Source: https://firebase.google.com/docs/firestore/query-data/get-data
        // Get an ImageAsset via imageAssetName
        ImageAssetRef.document(imageAssetName).get()
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
