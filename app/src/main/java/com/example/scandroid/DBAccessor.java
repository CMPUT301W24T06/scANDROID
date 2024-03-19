package com.example.scandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import android.os.Handler;

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
        this.EventPosterDB = new EventPosterDBAccessor(storageRef, EventPosterRefName);

        // Initialize access to ImageAsset storage
        ImageAssetRefName = "ImageAssets";
        this.ImageAssetDB = new ImageAssetDBAccessor(storageRef, ImageAssetRefName);

        // Initialize access to QRCode storage
        QRCodeMainRefName = "QRCodeMain";
        QRCodePromoRefName = "QRCodePromo";
        this.QRCodeDB = new QRCodeDBAccessor(storageRef, QRCodeMainRefName, QRCodePromoRefName);

        // Initialize access to User storage
        UserRefName = "Users";
        this.UserDB = new UserDBAccessor(db, UserRefName);

        // Initialize access to UserProfileImage storage
        UserProfileImageRefName = "UserProfileImages";
        UserProfileImageDB = new UserProfileImageDBAccessor(storageRef, UserProfileImageRefName);
    }

    /* ----------------------- *
     * METHODS : ADMIN:Getters *
     * ----------------------- */
    /**
     * {@link EventDBAccessor#getAllEventReferences(ListIDCallback)}
     * @param callback Handle the asynchronous nature of the Firestore get operation.
     */
    public void getAllEventReferences(ListIDCallback callback) {
        this.EventDB.getAllEventReferences(callback);
    }



    /**
     * {@link UserDBAccessor#getAllEventReferences(ListIDCallback)}
     * @param callback Handle the asynchronous nature of the Firestore get operation.
     */
    public void getAllUserReferences(ListIDCallback callback) {
        this.UserDB.getAllUserReferences(callback);
    }


    /* -------------------------- *
     * METHODS : ACCESS[document] *
     * -------------------------- */
    /**
     * {@link EventDBAccessor#accessEvent(String, EventCallback)}
     * @param EventID Unique identifier for Event to be accessed
     * @param callback Handle the asynchronous nature of the Firestore get operation.
     */
    public void accessEvent(String EventID, EventCallback callback) {
        this.EventDB.accessEvent(EventID, callback);
    }

    /**
     * {@link EventPosterDBAccessor#accessEventPoster(String, BitmapCallback)}
     * @param EventID Unique identifier for EventPoster to be accessed
     */
    public void accessEventPoster(String EventID, BitmapCallback callback) {
        this.EventPosterDB.accessEventPoster(EventID, callback);
    }

    /**
     * {@link ImageAssetDBAccessor#accessImageAsset(String, BitmapCallback)}
     * @param imageAssetName Unique identifier for ImageAsset to be accessed
     */
    public void accessImageAsset(String imageAssetName, BitmapCallback callback) {
        this.ImageAssetDB.accessImageAsset(imageAssetName, callback);
    }

    /**
     * {@link QRCodeDBAccessor#accessQRMain(String, BitmapCallback)}
     * @param EventID Unique identifier for QRMain to be accessed
     */
    public void accessQRMain(String EventID, BitmapCallback callback) {
        this.QRCodeDB.accessQRMain(EventID, callback);
    }

    /**
     * {@link QRCodeDBAccessor#accessQRPromo(String, BitmapCallback)}
     * @param EventID Unique identifier for QRPromo to be accessed
     */
    public void accessQRPromo(String EventID, BitmapCallback callback) {
        this.QRCodeDB.accessQRPromo(EventID, callback);
    }

    /**
     * {@link UserDBAccessor#accessUser(String, UserCallback)}
     * @param UserID Unique identifier for User to be accessed
     * @param callback Handle the asynchronous nature of the Firestore get operation.
     */
    public void accessUser(String UserID, UserCallback callback) {
        this.UserDB.accessUser(UserID, callback);
    }

    /**
     * {@link UserProfileImageDBAccessor#accessUserProfileImage(String, BitmapCallback)}
     * @param UserID Unique identifier for UserProfileImage to be accessed
     */
    public void accessUserProfileImage(String UserID, BitmapCallback callback) {
        this.UserProfileImageDB.accessUserProfileImage(UserID, callback);
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
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessEvent(String EventID, EventCallback callback) {
            // Get an Event via EventID
            // Source: https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf
            this.EventRef.document(EventID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                                Event retrievedEvent = Event.fromSnapshot(document);
                                callback.onEventReceived(retrievedEvent);
                            } else {
                                Log.d("Firestore", "No such document");
                                callback.onEventReceived(null);
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                            callback.onEventReceived(null);
                        }
                    });
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
         * Get a list of all eventID's in firestore database
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void getAllEventReferences(ListIDCallback callback) {
            this.EventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> eventIDs = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            eventIDs.add(document.getId());
                        }
                    }
                    Log.d("Event IDs", eventIDs.toString());
                    callback.onListRetrieved(eventIDs);
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                    callback.onListRetrieved(null);
                }
            });
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
        private StorageReference EventPosterRef;
        private StorageReference storageRef;
        private String EventPosterRefName;
        private ByteArrayOutputStream boas;

        // Constructor
        /**
         * Sole constructor of EventPosterDBAccessor. <br>
         * Allows access to EventPoster's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private EventPosterDBAccessor(StorageReference storageRef, String EventPosterRefName) {
            // Access EventPoster storage of Firestore
            this.storageRef = storageRef;
            this.EventPosterRefName = EventPosterRefName;
            this.boas = new ByteArrayOutputStream();
        }

        // Methods
        /**
         * Get EventPoster stored in Firestore Database
         * @param EventID Unique identifier for EventPoster to be accessed
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessEventPoster(String EventID, BitmapCallback callback) {
            // Download an EventPoster from Firestore Storage
            // Source(1): https://firebase.google.com/docs/storage/android/download-files
            // Source(2): https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c (via Simon Thang)

            // set storage reference to EventPoster collection with EventID as key
            this.EventPosterRef = this.storageRef.child(this.EventPosterRefName + "/" + EventID);

            // setup getByte limit and return value location
            final long ONE_MEGABYTE = 1024 * 1024;

            // download bitmap with EventID as key
            this.EventPosterRef.getBytes(ONE_MEGABYTE * 3)
                    .addOnSuccessListener(bytes -> {
                        Log.d("FireStorage", "EventPoster successfully accessed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            callback.onBitmapLoaded(bitmap);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FireStorage", "EventPoster access failed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onBitmapFailed(e);
                        });
                    });
        }


        /**
         * Delete an EventPoster in Firestore Database
         * @param EventID Unique identifier for EventPoster to be deleted
         */
        private void deleteEventPoster(String EventID) {
            // Delete an EventPoster with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/delete-files

            // set storage reference to EventPoster collection with EventID as key
            this.EventPosterRef = this.storageRef.child(this.EventPosterRefName + "/" + EventID);

            // Delete the EventPoster
            this.EventPosterRef.delete()
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
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to EventPoster collection with EventID as key
            this.EventPosterRef = this.storageRef.child(this.EventPosterRefName + "/" + EventID);

            // write bitmap to output stream
            EventPoster.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] EventPosterData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = this.EventPosterRef.putBytes(EventPosterData);
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
        private StorageReference ImageAssetRef;
        private StorageReference storageRef;
        private String ImageAssetRefName;
        private ByteArrayOutputStream boas;

        // Constructor
        /**
         * Sole constructor of ImageAssetDBAccessor. <br>
         * Allows access to ImageAsset's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get) and Store
         */
        private ImageAssetDBAccessor(StorageReference storageRef, String ImageAssetRefName) {
            // Access EventPoster collection of Firestore
            this.storageRef = storageRef;
            this.ImageAssetRefName = ImageAssetRefName;
            this.boas = new ByteArrayOutputStream();
        }

        // Methods
        /**
         * Get ImageAsset stored in Firestore Database
         * @param imageAssetName Unique identifier for ImageAsset to be accessed
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessImageAsset(String imageAssetName, BitmapCallback callback) {
            // Get an ImageAsset via imageAssetName
            // Source(1): https://firebase.google.com/docs/storage/android/download-files
            // Source(2): https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c (via Simon Thang)

            // set storage reference to EventPoster collection with EventID as key
            ImageAssetRef = this.storageRef.child(this.ImageAssetRefName + "/" + imageAssetName);

            // setup getByte limit and return value location
            final long ONE_MEGABYTE = 1024 * 1024;

            // download bitmap with EventID as key
            ImageAssetRef.getBytes(ONE_MEGABYTE * 3)
                    .addOnSuccessListener(bytes -> {
                        Log.d("FireStorage", "ImageAsset successfully accessed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            callback.onBitmapLoaded(bitmap);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FireStorage", "ImageAsset access failed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onBitmapFailed(e);
                        });
                    });
        }

        /**
         * Store or update ImageAsset in Firestore Database
         * @param imageAssetName Name of ImageAsset to be added or updated.
         * @param imageAsset Bitmap of ImageAsset to be added or updated.
         */
        private void storeImageAsset(String imageAssetName, Bitmap imageAsset) {
            // Store an ImageAsset with imageAssetName as key
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to ImageAsset collection with imageAssetName as key
            this.ImageAssetRef = this.storageRef.child(this.ImageAssetRefName + "/" + imageAssetName);

            // write bitmap to output stream
            imageAsset.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] ImageAssetData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = this.ImageAssetRef.putBytes(ImageAssetData);
            uploadPosterTask
                    .addOnFailureListener(e -> Log.w("FireStorage", "Error writing ImageAsset ByteArray"))
                    .addOnSuccessListener(taskSnapshot -> Log.w("FireStorage", "ImageAsset successfully written!"));
        }

    } // end private class ImageAssetDBAccessor

    /**
     * Represents interface between scANDROID and Firestore database. <br>
     * Allows storage and access of main Event and promotional Event QRCodes.
     * @author Jordan Beaubien
     */
    private class QRCodeDBAccessor {

        // Attributes / Fields
        private StorageReference QRCodeMainRef;
        private StorageReference QRCodePromoRef;
        private StorageReference storageRef;
        private String QRCodeMainRefName;
        private String QRCodePromoRefName;
        private ByteArrayOutputStream boas;


        // Constructor
        /**
         * Sole constructor of QRCodeDBAccessor. <br>
         * Allows access to QRCode's stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private QRCodeDBAccessor(StorageReference storageRef, String QRCodeMainRefName, String QRCodePromoRefName) {
            // Access QRCode storage of Firestore
            this.storageRef = storageRef;
            this.QRCodeMainRefName = QRCodeMainRefName;
            this.QRCodePromoRefName = QRCodePromoRefName;
            this.boas = new ByteArrayOutputStream();
        }

        // Methods
        /**
         * Get main QR code for an Event stored in Firestore Database
         * @param EventID Unique identifier for QRCode to be accessed
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessQRMain(String EventID, BitmapCallback callback) {
            // Download a QRCodeMain from Firestore Storage
            // Source(1): https://firebase.google.com/docs/storage/android/download-files
            // Source(2): https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c (via Simon Thang)

            // set storage reference to QRCodeMain collection with EventID as key
            this.QRCodeMainRef = this.storageRef.child(this.QRCodeMainRefName + "/" + EventID);

            // setup getByte limit and return value location
            final long ONE_MEGABYTE = 1024 * 1024;

            // download bitmap with EventID as key
            this.QRCodeMainRef.getBytes(ONE_MEGABYTE * 3)
                    .addOnSuccessListener(bytes -> {
                        Log.d("FireStorage", "QRCodeMain successfully accessed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            callback.onBitmapLoaded(bitmap);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FireStorage", "QRCodeMain access failed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onBitmapFailed(e);
                        });
                    });
        }

        /**
         * Get promotional QR code for an Event stored in Firestore Database
         * @param EventID Unique identifier for promo QRCode to be accessed
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessQRPromo(String EventID, BitmapCallback callback) {
            // Download a QRCodePromo from Firestore Storage
            // Source(1): https://firebase.google.com/docs/storage/android/download-files
            // Source(2): https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c (via Simon Thang)

            // set storage reference to QRCodePromo collection with EventID as key
            this.QRCodePromoRef = this.storageRef.child(this.QRCodePromoRefName + "/" + EventID);

            // setup getByte limit and return value location
            final long ONE_MEGABYTE = 1024 * 1024;

            // download bitmap with EventID as key
            this.QRCodePromoRef.getBytes(ONE_MEGABYTE * 3)
                    .addOnSuccessListener(bytes -> {
                        Log.d("FireStorage", "QRCodePromo successfully accessed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            callback.onBitmapLoaded(bitmap);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FireStorage", "QRCodePromo access failed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onBitmapFailed(e);
                        });
                    });
        }

        /**
         * Delete a QRCodeMain in Firestore Database
         * @param EventID Unique identifier for QRCodeMain to be deleted
         */
        private void deleteQRMain(String EventID) {
            // Delete a QRCodeMain with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/delete-files

            // set storage reference to QRCodeMain collection with EventID as key
            this.QRCodeMainRef = this.storageRef.child(this.QRCodeMainRefName + "/" + EventID);

            // Delete the EventPoster
            this.QRCodeMainRef.delete()
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting QRCodeMain", e))
                    .addOnSuccessListener(unused -> Log.d("Firestore", "QRCodeMain successfully deleted!"));
        }

        /**
         * Delete a QRCodePromo in Firestore Database
         * @param EventID Unique identifier for QRCodePromo to be deleted
         */
        private void deleteQRPromo(String EventID) {
            // Delete a QRCodePromo with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/delete-files

            // set storage reference to QRCodePromo collection with EventID as key
            this.QRCodePromoRef = this.storageRef.child(this.QRCodePromoRefName + "/" + EventID);

            // Delete the EventPoster
            this.QRCodePromoRef.delete()
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting QRCodePromo", e))
                    .addOnSuccessListener(unused -> Log.d("Firestore", "QRCodePromo successfully deleted!"));
        }


        /**
         * Store or update an Events main QR code in Firestore Database
         * @param EventID Unique identifier for QRCodeMain to be accessed
         * @param QRCodeMain Bitmap of QRCodeMain to be stored.
         */
        private void storeQRMain(String EventID, Bitmap QRCodeMain) {
            // Store an QRCodeMain with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to QRCodeMain collection with EventID as key
            this.QRCodeMainRef = this.storageRef.child(this.QRCodeMainRefName + "/" + EventID);

            // write bitmap to output stream
            QRCodeMain.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] EventPosterData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = this.QRCodeMainRef.putBytes(EventPosterData);
            uploadPosterTask
                    .addOnFailureListener(e -> Log.w("FireStorage", "Error writing QRCodeMain ByteArray"))
                    .addOnSuccessListener(taskSnapshot -> Log.w("FireStorage", "QRCodeMain successfully written!"));
        }


        /**
         * Store or update an Events promotional QR code in Firestore Database
         * @param EventID Unique identifier for QRCodePromo to be accessed
         * @param QRCodePromo Bitmap of QRCodePromo to be stored.
         */
        private void storeQRPromo(String EventID, Bitmap QRCodePromo) {
            // Store an QRCodePromo with EventID as key
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to QRCodePromo collection with EventID as key
            this.QRCodePromoRef = this.storageRef.child(this.QRCodePromoRefName + "/" + EventID);

            // write bitmap to output stream
            QRCodePromo.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] EventPosterData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = this.QRCodePromoRef.putBytes(EventPosterData);
            uploadPosterTask
                    .addOnFailureListener(e -> Log.w("FireStorage", "Error writing QRCodePromo ByteArray"))
                    .addOnSuccessListener(taskSnapshot -> Log.w("FireStorage", "QRCodePromo successfully written!"));
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
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void accessUser(String UserID, UserCallback callback) {
//            final User[] retrievedUser = new User[1];

            // Get a User via UserID
            // Source: https://chat.openai.com/share/e135f2dc-cd2f-47ca-b48e-55115d41e6bf
            this.UserRef.document(UserID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                                User retrievedUser = task.getResult().toObject(User.class);
                                callback.onUserRetrieved(retrievedUser);
                            } else {
                                Log.d("Firestore", "No such document");
                                callback.onUserRetrieved(null);
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                            callback.onUserRetrieved(null);
                        }
                    });

//            return retrievedUser[0];
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
         * Get a list of all userID's in firestore database
         * @param callback To handle asynchronous operations for accessing firebase
         */
        private void getAllUserReferences(ListIDCallback callback) {
            this.UserRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> userIDs = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            userIDs.add(document.getId());
                        }
                    }
                    Log.d("User IDs", userIDs.toString());
                    callback.onListRetrieved(userIDs);
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                    callback.onListRetrieved(null);
                }
            });
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
        private StorageReference UserProfileImageRef;
        private StorageReference storageRef;
        private String UserProfileImageRefName;
        private ByteArrayOutputStream boas;

        // Constructor
        /**
         * Sole constructor of UserProfileImageDBAccessor. <br>
         * Allows access to UserProfileImages stored as Bitmaps in a Firestore Database. <br>
         * Actions permitted: Access(get), Delete, and Store
         */
        private UserProfileImageDBAccessor(StorageReference storageRef, String UserProfileImageRefName) {

            // Access EventPoster collection of Firestore
            this.storageRef = storageRef;
            this.UserProfileImageRefName = UserProfileImageRefName;
            this.boas = new ByteArrayOutputStream();
        }

        // Methods
        /**
         * Get UserProfileImage stored in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be accessed
         */
        private void accessUserProfileImage(String UserID, BitmapCallback callback) {
            // Download a UserProfileImage from Firestore Storage
            // Source(1): https://firebase.google.com/docs/storage/android/download-files
            // Source(2): https://chat.openai.com/share/5353b1da-4b10-4b2c-a928-be60d7cbb08c (via Simon Thang)

            // set storage reference to UserProfileImage collection with UserID as key
            this.UserProfileImageRef = this.storageRef.child(this.UserProfileImageRefName + "/" + UserID);

            // setup getByte limit and return value location
            final long ONE_MEGABYTE = 1024 * 1024;

            // download bitmap with EventID as key
            this.UserProfileImageRef.getBytes(ONE_MEGABYTE * 3)
                    .addOnSuccessListener(bytes -> {
                        Log.d("FireStorage", "UserProfileImage successfully accessed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            callback.onBitmapLoaded(bitmap);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FireStorage", "UserProfileImage access failed!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onBitmapFailed(e);
                        });
                    });
        }

        /**
         * Delete a UserProfileImage in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be deleted
         */
        private void deleteUserProfileImage(String UserID) {
            // Delete an UserProfileImage with UserID as key
            // Source: https://firebase.google.com/docs/storage/android/delete-files

            // set storage reference to UserProfileImage collection with UserID as key
            this.UserProfileImageRef = this.storageRef.child(this.UserProfileImageRefName + "/" + UserID);

            // Delete the EventPoster
            this.UserProfileImageRef.delete()
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting UserProfileImage", e))
                    .addOnSuccessListener(unused -> Log.d("Firestore", "UserProfileImage successfully deleted!"));
        }

        /**
         * Store or update UserProfileImage in Firestore Database
         * @param UserID Unique identifier for UserProfileImage to be stored or updated
         * @param UserProfileImage Bitmap of UserProfileImage to be stored
         */
        private void storeUserProfileImage(String UserID, Bitmap UserProfileImage) {
            // Store an UserProfileImage with UserID as key
            // Source: https://firebase.google.com/docs/storage/android/upload-files#java_1

            // set storage reference to UserProfileImage collection with UserID as key
            this.UserProfileImageRef = this.storageRef.child(this.UserProfileImageRefName + "/" + UserID);

            // write bitmap to output stream
            UserProfileImage.compress(Bitmap.CompressFormat.PNG, 100, boas);
            byte[] EventPosterData = boas.toByteArray();

            // upload bitmap to Firebase Storage
            UploadTask uploadPosterTask = this.UserProfileImageRef.putBytes(EventPosterData);
            uploadPosterTask
                    .addOnFailureListener(e -> Log.w("FireStorage", "Error writing UserProfileImage ByteArray"))
                    .addOnSuccessListener(taskSnapshot -> Log.w("FireStorage", "UserProfileImage successfully written!"));
        }

    } // end private class UserProfileImageDBAccessor

} // end public class DBAccessor