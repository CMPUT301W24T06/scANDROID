package com.example.scandroid;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * @author Jordan Beaubien
 */
public class DBAccessor {

    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;

    // DB Collection references
    private String EventRefName;
    private String EventPosterRefName;
    private String ImageAssetRefName;
    private String QRCodeMainRefName;
    private String QRCodePromoRefName;
    private String UserRefName;
    private String UserProfileImageRefName;

    // DB nested class instances
    EventDBAccessor EventDB;
    EventPosterDBAccessor EventPosterDB;
    ImageAssetDBAccessor ImageAssetDB;
    QRCodeDBAccessor QRCodeDB;
    UserDBAccessor UserDB;
    UserProfileImageDBAccessor UserProfileImageDB;


    /* ----------- *
     * CONSTRUCTOR *
     * ----------- */
    /**
     * Sole constructor of DBAccessor. <br>
     * Contains all access to Firestore Database. <br>
     * DB's: Events, EventPosters, ImageAssets, QRCodes, Users, and UserProfileImages <br>
     * Actions permitted: Access(get), Delete, and Store(update)
     */
    public DBAccessor() {
        EventRefName = "Events";
        this.EventDB = new EventDBAccessor(db, EventRefName);

        EventPosterRefName = "EventPoster";
        this.EventPosterDB = new EventPosterDBAccessor(db, EventPosterRefName);

        ImageAssetRefName = "ImageAssets";
        this.ImageAssetDB = new ImageAssetDBAccessor(db, ImageAssetRefName);

        QRCodeMainRefName = "QRCodeMain";
        QRCodePromoRefName = "QRCodePromo";
        this.QRCodeDB = new QRCodeDBAccessor(db, QRCodeMainRefName, QRCodePromoRefName);

        UserRefName = "Users";
        this.UserDB = new UserDBAccessor(db, UserRefName);

        UserProfileImageRefName = "UserProfileImages";
        UserProfileImageDB = new UserProfileImageDBAccessor(db, UserProfileImageRefName);
    }




    /* -------------------------- *
     * METHODS : ACCESS[document] *
     * -------------------------- */

    // EventDBAccessor
    // EventPosterDBAccessor
    // ImageAssetDBAccessor
    // QRCodeDBAccessor x2
    // UserDBAccessor
    // UserProfileImageDBAccessor

    /* -------------------------- *
     * METHODS : DELETE[document] *
     * -------------------------- */

    // EventDBAccessor
    // EventPosterDBAccessor
            // ImageAssets can only be deleted on Firebase with Owner privilege
    // QRCodeDBAccessor x2 (need to implement in nested class)
    // UserDBAccessor
    // UserProfileImageDBAccessor

    /* ------------------------- *
     * METHODS : STORE[document] *
     * ------------------------- */

    // EventDBAccessor
    // EventPosterDBAccessor
    // ImageAssetDBAccessor
    // QRCodeDBAccessor x2
    // UserDBAccessor
    // UserProfileImageDBAccessor

    /* -------------- *
     * NESTED CLASSES *
     * -------------- */
    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of Event objects.
     * @author Jordan Beaubien
     */
    private class EventDBAccessor {

        // Attributes / Fields
        private CollectionReference EventRef;

        // Constructor
        /**
         * Sole constructor of EventDBAccessor. <br>
         * Allows access to Event's stored in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private EventDBAccessor(FirebaseFirestore db, String EventRefName) {

            // Access Event collection of Firestore
            this.EventRef = db.collection(EventRefName);
        }

        // Methods
        /**
         * Get Event stored in Firestore Database
         * @param EventID Unique identifier for Event to be accessed
         * @return Event that matches EventID if exists in Firestore Database
         */
        private Event accessEvent(String EventID) {
            final Event[] retrievedEvent = new Event[1];

            // Get an Event via EventID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.EventRef.document(EventID).get()
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

        /**
         * Delete an Event in Firestore Database
         * @param EventID Unique identifier for Event to be deleted
         */
        private void deleteEvent(String EventID) {
            // Delete an Event via EventID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.EventRef.document(EventID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting Event", e));
        }

        /**
         * Store or update Event in Firestore Database
         * @param event Event object to be added or updated.
         */
        private void storeEvent(Event event) {
            // Store an Event with EventID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.EventRef.document(event.getEventID()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing Event document", e));
        }

    } // end private class EventDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of EventPoster's.
     * @author Jordan Beaubien
     */
    private class EventPosterDBAccessor {

        // Attributes / Fields
        private CollectionReference EventPosterRef;

        // Constructor
        /**
         * Sole constructor of EventPosterDBAccessor. <br>
         * Allows access to EventPoster's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private EventPosterDBAccessor(FirebaseFirestore db, String EventPosterRefName) {

            // Access EventPoster collection of Firestore
            this.EventPosterRef = db.collection(EventPosterRefName);
        }

        // Methods
        /**
         * Get EventPoster stored in Firestore Database
         * @param EventID Unique identifier for EventPoster to be accessed
         */
        private Bitmap accessEventPoster(String EventID) {
            final Bitmap[] retrievedPoster = new Bitmap[1];

            // Get an EventPoster via EventID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.EventPosterRef.document(EventID).get()
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

        /**
         * Delete an EventPoster in Firestore Database
         * @param EventID Unique identifier for EventPoster to be deleted
         */
        private void deleteEventPoster(String EventID) {
            // Delete an EventPoster via EventID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.EventPosterRef.document(EventID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting EventPoster", e));
        }

        /**
         * Store or update EventPoster in Firestore Database
         * @param EventID Unique identifier for EventPoster to be accessed
         * @param EventPoster Bitmap of EventPoster to be stored.
         */
        private void storeEventPoster(String EventID, Bitmap EventPoster) {
            // Store an EventPoster with EventID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.EventPosterRef.document(EventID).set(EventPoster)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing EventPoster document", e));
        }

    } // end private class EventPosterDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of ImageAsset's.
     * @author Jordan Beaubien
     */
    private class ImageAssetDBAccessor {

        // Attributes / Fields
        private CollectionReference ImageAssetRef;

        // Constructor
        /**
         * Sole constructor of ImageAssetDBAccessor. <br>
         * Allows access to ImageAsset's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get) and Store
         */
        private ImageAssetDBAccessor(FirebaseFirestore db, String ImageAssetRefName) {

            // Access ImageAssets collection of Firestore
            this.ImageAssetRef = db.collection(ImageAssetRefName);
        }

        // Methods
        /**
         * Get ImageAsset stored in Firestore Database
         * @param imageAssetName Unique identifier for ImageAsset to be accessed
         */
        private Bitmap accessImageAsset(String imageAssetName) {
            final Bitmap[] retrievedImageAsset = new Bitmap[1];

            // Get an ImageAsset via imageAssetName
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.ImageAssetRef.document(imageAssetName).get()
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

        /**
         * Store or update ImageAsset in Firestore Database
         * @param imageAssetName Name of ImageAsset to be added or updated.
         */
        private void storeImageAsset(String imageAssetName, Bitmap bitmap) {
            // Store an ImageAsset with imageAssetName as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.ImageAssetRef.document(imageAssetName).set(bitmap)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "ImageAsset successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing ImageAsset document", e));
        }

    } // end private class ImageAssetDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of main Event and promotional Event QRCodes.
     * @author Jordan Beaubien
     */
    private class QRCodeDBAccessor {

        // Attributes / Fields
        private CollectionReference QRCodeMainRef;
        private CollectionReference QRCodePromoRef;


        // Constructor
        /**
         * Sole constructor of QRCodeDBAccessor. <br>
         * Allows access to QRCode's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get) and Store
         */
        private QRCodeDBAccessor(FirebaseFirestore db, String QRCodeMainRefName, String QRCodePromoRefName) {

            // Access ImageAssets collection of Firestore
            this.QRCodeMainRef = db.collection(QRCodeMainRefName);
            this.QRCodePromoRef = db.collection(QRCodePromoRefName);
        }

        // Methods
        /**
         * Get main QR code for an Event stored in Firestore Database
         * @param EventID Unique identifier for QRCode to be accessed
         */
        private Bitmap accessQRMain(String EventID) {
            final Bitmap[] retrievedQRMain = new Bitmap[1];

            // Get a QRMain via EventID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.QRCodeMainRef.document(EventID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                                retrievedQRMain[0] = task.getResult().toObject(Bitmap.class);
                            } else {
                                Log.d("Firestore", "No such document");
                                retrievedQRMain[0] = null;
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                            retrievedQRMain[0] = null;
                        }
                    });

            return retrievedQRMain[0];
        }

        /**
         * Get promotional QR code for an Event stored in Firestore Database
         * @param EventID Unique identifier for promo QRCode to be accessed
         */
        private Bitmap accessQRPromo(String EventID) {
            final Bitmap[] retrievedQRPromo = new Bitmap[1];

            // Get a QRPromo via EventID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.QRCodePromoRef.document(EventID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                                retrievedQRPromo[0] = task.getResult().toObject(Bitmap.class);
                            } else {
                                Log.d("Firestore", "No such document");
                                retrievedQRPromo[0] = null;
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                            retrievedQRPromo[0] = null;
                        }
                    });

            return retrievedQRPromo[0];
        }

        /**
         * Store or update an Events main QR code in Firestore Database
         * @param EventID Unique identifier for QRCode to be accessed
         * @param QRMain Bitmap of QRMain to be stored.
         */
        private void storeQRMain(String EventID, Bitmap QRMain) {
            // Store a QRMain with EventID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.QRCodeMainRef.document(EventID).set(QRMain)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRMain successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing QRMain document", e));
        }

        /**
         * Store or update an Events promotional QR code in Firestore Database
         * @param EventID Unique identifier for QRCode to be accessed
         * @param QRPromo Bitmap of QRPromo to be stored.
         */
        private void storeQRPromo(String EventID, Bitmap QRPromo) {
            // Store a QRPromo with EventID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.QRCodePromoRef.document(EventID).set(QRPromo)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRPromo successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing QRPromo document", e));
        }

    } // end private class QRCodeDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of User objects
     * @author Jordan Beaubien
     */
    private class UserDBAccessor {

        // Attributes / Fields
        private CollectionReference UserRef;

        // Constructor
        /**
         * Sole constructor of UserDBAccessor. <br>
         * Allows access to User's stored in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private UserDBAccessor(FirebaseFirestore db, String UserRefName) {

            // Access User collection of Firestore
            this.UserRef = db.collection(UserRefName);
        }

        // Methods
        /**
         * Get User stored in Firestore Database
         * @param UserID Unique identifier for User
         * @return User that matches UserID if exists in Firestore Database
         */
        private User accessUser(String UserID) {
            final User[] retrievedUser = new User[1];

            // Get a User via UserID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.UserRef.document(UserID).get()
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

        /**
         * Delete a User in Firestore Database
         * @param UserID Unique identifier for User
         */
        private void deleteUser(String UserID) {
            // Delete a User via UserID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.UserRef.document(UserID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting User", e));
        }

        /**
         * Store or update a User in Firestore Database
         * @param user User object to be added or updated.
         */
        private void storeUser(User user) {
            // Store a User with UserID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.UserRef.document(user.getUserID()).set(user)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing User document", e));
        }

    } // end private class UserDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of UserProfileImages.
     * @author Jordan Beaubien
     */
    private class UserProfileImageDBAccessor {

        // Attributes / Fields
        private CollectionReference UserProfileImageRef;

        // Constructor
        /**
         * Sole constructor of UserProfileImageDBAccessor. <br>
         * Allows access to UserProfileImages stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private UserProfileImageDBAccessor(FirebaseFirestore db, String UserProfileImageRefName) {

            // Access EventPoster collection of Firestore
            this.UserProfileImageRef = db.collection(UserProfileImageRefName);
        }

        // Methods
        /**
         * Get UserProfileImage stored in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be accessed
         */
        private Bitmap accessUserProfileImage(String UserID) {
            final Bitmap[] retrievedProfileImage = new Bitmap[1];

            // Get a UserProfileImage via UserID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
            this.UserProfileImageRef.document(UserID).get()
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

        /**
         * Delete a UserProfileImage in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be deleted
         */
        private void deleteUserProfileImage(String UserID) {
            // Delete a UserProfileImage via UserID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.UserProfileImageRef.document(UserID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "UserProfileImage successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting UserProfileImage", e));
        }

        /**
         * Store or update UserProfileImage in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be stored or updated
         * @param UserProfileImage Bitmap of UserProfileImage to be stored
         */
        private void storeUserProfileImage(String UserID, Bitmap UserProfileImage) {
            // Store a UserProfileImage with UserID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
            this.UserProfileImageRef.document(UserID).set(UserProfileImage)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "UserProfileImage successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing UserProfileImage document", e));
        }

    } // end private class UserProfileImageDBAccessor

} // end public class DBAccessor