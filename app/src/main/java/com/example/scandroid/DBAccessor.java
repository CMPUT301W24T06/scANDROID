package com.example.scandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Represents interface between scANDROID and Firestore database. <br>
 * @author Jordan Beaubien
 */
public class DBAccessor {

    /* ------------------- *
     * ATTRIBUTES / FIELDS *
     * ------------------- */
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

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
        // Initialize access to Firebase and Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize access to Event storage
        EventRefName = "Events";
        this.EventDB = new EventDBAccessor(db, EventRefName);

        // Initialize access to EventPoster storage
        EventPosterRefName = "EventPoster";
        this.EventPosterDB = new EventPosterDBAccessor(db, EventPosterRefName);

        // Initialize access to ImageAsset storage
        ImageAssetRefName = "ImageAssets";
        this.ImageAssetDB = new ImageAssetDBAccessor(db, ImageAssetRefName);

        // Initialize access to QRCode storage
        QRCodeMainRefName = "QRCodeMain";
        QRCodePromoRefName = "QRCodePromo";
        this.QRCodeDB = new QRCodeDBAccessor(db, QRCodeMainRefName, QRCodePromoRefName);

        // Initialize access to User storage
        UserRefName = "Users";
        this.UserDB = new UserDBAccessor(db, UserRefName);

        // Initialize access to UserProfileImage storage
        UserProfileImageRefName = "UserProfileImages";
        UserProfileImageDB = new UserProfileImageDBAccessor(db, UserProfileImageRefName);
    }


    /* -------------------------- *
     * METHODS : ACCESS[document] *
     * -------------------------- */
    /**
     * {@link EventDBAccessor#accessEvent(String)}
     * @param EventID Unique identifier for Event to be accessed
     * @return Event that matches EventID if exists in Firestore Database
     */
    public Event accessEvent(String EventID) {
        return this.EventDB.accessEvent(EventID);
    }

    /**
     * {@link EventPosterDBAccessor#accessEventPoster(String)}
     * @param EventID Unique identifier for EventPoster to be accessed
     * @return Bitmap of EventPoster
     */
    public Bitmap accessEventPoster(String EventID) {
        return this.EventPosterDB.accessEventPoster(EventID);
    }

    /**
     * {@link ImageAssetDBAccessor#accessImageAsset(String)}
     * @param imageAssetName Unique identifier for ImageAsset to be accessed
     * @return Bitmap of ImageAsset
     */
    public Bitmap accessImageAsset(String imageAssetName) {
        return this.ImageAssetDB.accessImageAsset(imageAssetName);
    }

    /**
     * {@link QRCodeDBAccessor#accessQRMain(String)}
     * @param EventID Unique identifier for QRMain to be accessed
     * @return Bitmap of main QR code for Event
     */
    public Bitmap accessQRMain(String EventID) {
        return this.QRCodeDB.accessQRMain(EventID);
    }

    /**
     * {@link QRCodeDBAccessor#accessQRPromo(String)}
     * @param EventID Unique identifier for QRPromo to be accessed
     * @return Bitmap of promotional QR code for Event
     */
    public Bitmap accessQRPromo(String EventID) {
        return this.QRCodeDB.accessQRPromo(EventID);
    }

    /**
     * {@link UserDBAccessor#accessUser(String)}
     * @param UserID Unique identifier for User to be accessed
     * @return User that matches UserID if exists in Firestore Database
     */
    public User accessUser(String UserID) {
        return this.UserDB.accessUser(UserID);
    }

    /**
     * {@link UserProfileImageDBAccessor#accessUserProfileImage(String)}
     * @param UserID Unique identifier for UserProfileImage to be accessed
     * @return Bitmap of ProfileImage for User
     */
    public Bitmap accessUserProfileImage(String UserID) {
        return this.UserProfileImageDB.accessUserProfileImage(UserID);
    }

    /* -------------------------- *
     * METHODS : DELETE[document] *
     * -------------------------- */
    /**
     * {@link EventDBAccessor#deleteEvent(String)}
     * As well, deletes respective QRMain, QRPromo and EventPosterImage
     * associated with the EventID. <br>
     * @param EventID Unique identifier for Event to be deleted <br>
     */
    public void deleteEvent(String EventID) {
        // Delete items related to this EventID
        this.QRCodeDB.deleteQRMain(EventID);
        this.QRCodeDB.deleteQRPromo(EventID);
        this.EventPosterDB.deleteEventPoster(EventID);

        // Delete Event of EventID
        this.EventDB.deleteEvent(EventID);
    }

    /**
     * {@link EventPosterDBAccessor#deleteEventPoster(String)}
     * @param EventID Unique identifier for EventPoster to be deleted
     */
    public void deleteEventPoster(String EventID) { this.EventPosterDB.deleteEventPoster(EventID); }

    /* ******************************************************************
     * ImageAssets can only be deleted on Firebase with Owner privilege *
     * ******************************************************************/

    /**
     * {@link QRCodeDBAccessor#deleteQRMain(String)}
     * @param EventID Unique identifier for QRMain to be deleted
     */
    public void deleteQRMain(String EventID) {
        this.QRCodeDB.deleteQRMain(EventID);
    }

    /**
     * {@link QRCodeDBAccessor#deleteQRPromo(String)}
     * @param EventID Unique identifier for QRPromo to be deleted
     */
    public void deleteQRPromo(String EventID) {
        this.QRCodeDB.deleteQRPromo(EventID);
    }

    /**
     * {@link UserDBAccessor#deleteUser(String)}
     * As well, deletes respective UserProfileImage associated with the UserID. <br>
     * @param UserID Unique identifier for User
     */
    public void deleteUser(String UserID) {
        // Delete UserProfileImage related to this UserID
        this.UserProfileImageDB.deleteUserProfileImage(UserID);

        // Delete User of UserID
        this.UserDB.deleteUser(UserID);
    }

    /**
     * {@link UserProfileImageDBAccessor#deleteUserProfileImage(String)}
     * @param UserID Unique identifier for UserProfileImage to be deleted
     */
    public void deleteUserProfileImage(String UserID) {
        this.UserProfileImageDB.deleteUserProfileImage(UserID);
    }

    /* ------------------------- *
     * METHODS : STORE[document] *
     * ------------------------- */
    /**
     * {@link EventDBAccessor#storeEvent(Event)}
     * @param event Event object to be added or updated.
     */
    public void storeEvent(Event event) {
        this.EventDB.storeEvent(event);
    }

    /**
     * {@link EventPosterDBAccessor#storeEventPoster(String, Bitmap)}
     * <code> Get the data from an ImageView as bytes <br>
     * imageView.setDrawingCacheEnabled(true); <br>
     * imageView.buildDrawingCache(); <br>
     * Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); <br>
     * </code>
     * @param EventID Unique identifier for EventPoster to be accessed
     * @param EventPoster Bitmap of EventPoster to be stored.
     */
    public void storeEventPoster(String EventID, Bitmap EventPoster) {
        this.EventPosterDB.storeEventPoster(EventID, EventPoster);
    }

    /**
     * {@link ImageAssetDBAccessor#storeImageAsset(String, Bitmap)}
     * @param imageAssetName Name of ImageAsset to be added or updated.
     * @param bitmap Bitmap of ImageAsset to be added or updated.
     */
    public void storeImageAsset(String imageAssetName, Bitmap bitmap) {
        this.ImageAssetDB.storeImageAsset(imageAssetName, bitmap);
    }

    /**
     * {@link QRCodeDBAccessor#storeQRMain(String, Bitmap)}
     * @param EventID Unique identifier for QRCode to be accessed
     * @param QRMain Bitmap of QRMain to be stored or updated
     */
    public void storeQRMain(String EventID, Bitmap QRMain) {
        this.QRCodeDB.storeQRMain(EventID, QRMain);
    }

    /**
     * {@link QRCodeDBAccessor#storeQRPromo(String, Bitmap)}
     * @param EventID Unique identifier for QRCode to be accessed
     * @param QRPromo Bitmap of QRPromo to be stored or update
     */
    public void storeQRPromo(String EventID, Bitmap QRPromo) {
        this.QRCodeDB.storeQRPromo(EventID, QRPromo);
    }

    /**
     * {@link UserDBAccessor#storeUser(User)}
     * @param user User object to be added or updated.
     */
    public void storeUser(User user) {
        this.UserDB.storeUser(user);
    }

    /**
     * {@link UserProfileImageDBAccessor#storeUserProfileImage(String, Bitmap)}
     * @param UserID Unique identifier for UserProfileImage to be stored or updated
     * @param UserProfileImage Bitmap of UserProfileImage to be stored
     */
    public void storeUserProfileImage(String UserID, Bitmap UserProfileImage) {
        this.UserProfileImageDB.storeUserProfileImage(UserID, UserProfileImage);
    }

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
//        private CollectionReference EventPosterRef;
        private StorageReference EventPosterRef;
        private String EventPosterRefName;
        private ByteArrayOutputStream boas;

        // Constructor
        /**
         * Sole constructor of EventPosterDBAccessor. <br>
         * Allows access to EventPoster's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private EventPosterDBAccessor(FirebaseFirestore db, String EventPosterRefName) {

            // Access EventPoster collection of Firestore
//            this.EventPosterRef = db.collection(EventPosterRefName);
//            this.EventPosterRef = storageRef.child(EventPosterRefName);
            this.EventPosterRefName = EventPosterRefName;
            this.boas = new ByteArrayOutputStream();
        }

        // Methods
        /**
         * Get EventPoster stored in Firestore Database
         * @param EventID Unique identifier for EventPoster to be accessed
         * @return Bitmap of EventPoster
         */
        private Bitmap accessEventPoster(String EventID) {
            // Download an EventPoster from Firestore Storage
            // Source: https://firebase.google.com/docs/storage/android/download-files

            // set storage reference to EventPoster collection with EventID as key
            EventPosterRef = storageRef.child(EventPosterRefName + "/" + EventID);

            // setup getByte limit and return value location
            final Bitmap[] retrievedPoster = new Bitmap[1];
            final long ONE_MEGABYTE = 1024 * 1024;

            // get desired EventPoster from Firebase Storage
            EventPosterRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        retrievedPoster[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Log.d("FireStorage", "EventPoster successfully accessed!"); })
                    .addOnFailureListener(e -> {
                        retrievedPoster[0] = null; });
                        Log.d("FireStorage", "EventPoster access failed!");

            // Get an EventPoster via EventID
            // Source: https://firebase.google.com/docs/firestore/query-data/get-data
//            this.EventPosterRef.document(EventID).get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
//                                retrievedPoster[0] = task.getResult().toObject(Bitmap.class);
//                            } else {
//                                Log.d("Firestore", "No such document");
//                                retrievedPoster[0] = null;
//                            }
//                        } else {
//                            Log.d("Firestore", "get failed with ", task.getException());
//                            retrievedPoster[0] = null;
//                        }
//                    });

            return retrievedPoster[0];
        }

        /**
         * Delete an EventPoster in Firestore Database
         * @param EventID Unique identifier for EventPoster to be deleted
         */
        private void deleteEventPoster(String EventID) {
            // Delete an EventPoster via EventID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
//            this.EventPosterRef.document(EventID).delete()
//                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully deleted!"))
//                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting EventPoster", e));

            // Delete an EventPoster with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/delete-files

            // set storage reference to EventPoster collection with EventID as key
            EventPosterRef = storageRef.child(EventPosterRefName + "/" + EventID);

            // Delete the EventPoster
            EventPosterRef.delete()
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting EventPoster", e))
                    .addOnSuccessListener(unused -> Log.d("Firestore", "EventPoster successfully deleted!"));
        }

        /**
         * Store or update EventPoster in Firestore Database
         * @param EventID Unique identifier for EventPoster to be accessed
         * @param EventPoster Bitmap of EventPoster to be stored.
         */
        private void storeEventPoster(String EventID, Bitmap EventPoster) {
            // Store an EventPoster with EventID as key
            // Source: https://firebase.google.com/docs/firestore/manage-data/add-data#update_elements_in_an_array
//            this.EventPosterRef.document(EventID).set(EventPoster)
//                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "EventPoster successfully written!"))
//                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing EventPoster document", e));

            // Store an EventPoster with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to EventPoster collection with EventID as key
            EventPosterRef = storageRef.child(EventPosterRefName + "/" + EventID);

            // write bitmap to output stream
            EventPoster.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] EventPosterData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = EventPosterRef.putBytes(EventPosterData);
            uploadPosterTask
                    .addOnFailureListener(e -> Log.w("FireStorage", "Error writing EventPoster ByteArray"))
                    .addOnSuccessListener(taskSnapshot -> Log.w("FireStorage", "EventPoster successfully written!"));
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
         * @return Bitmap of ImageAsset
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
         * @param bitmap Bitmap of ImageAsset to be added or updated.
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
         * @return Bitmap of main QR code for Event
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
         * @return Bitmap of promotional QR code for Event
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
         * Delete a QRMain in Firestore Database
         * @param EventID Unique identifier for QRMain to be deleted
         */
        private void deleteQRMain(String EventID) {
            // Delete a QRMain via EventID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.QRCodeMainRef.document(EventID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRMain successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting QRMain", e));
        }

        /**
         * Delete a QRPromo in Firestore Database
         * @param EventID Unique identifier for QRPromo to be deleted
         */
        private void deleteQRPromo(String EventID) {
            // Delete a QRPromo via EventID
            // Source: https://firebase.google.com/docs/firestore/manage-data/delete-data
            this.QRCodePromoRef.document(EventID).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "QRPromo successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting QRPromo", e));
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
         * @return Bitmap of ProfileImage for User
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