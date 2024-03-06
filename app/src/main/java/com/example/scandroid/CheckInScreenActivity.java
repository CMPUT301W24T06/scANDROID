package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CheckInScreenActivity extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //
    ImageView backgroundImageView; // declare the ImageView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_screen_activity);
        ImageView backgroundImageView = findViewById(R.id.check_in_image);
        backgroundImageView.setBackgroundResource(R.drawable.check_in_image);

        // Obtain the necessary user information
        String userId = getDeviceId(this); // You might want to use a more robust method to generate a user ID
        String userName = ""; // Set the user's name
        String userPhoneNumber = ""; // Set the user's phone number
        String userAboutMe = ""; // Set the user's about me information
        String userEmail = ""; // Set the user's email

        // Create a new User object
        User newUser = new User(userId, userName, userPhoneNumber, userAboutMe, userEmail);

        // Add the user to Firestore
        addUserToFirestore(newUser);
    }

    public static String getDeviceId(Context context) {
        // Use Settings.Secure.ANDROID_ID for a unique device identifier
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void addUserToFirestore(User user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = firestore.collection("Users");

        usersCollection
                .document(user.getUserID()) // Use the user's ID as the document ID
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User added successfully
                        Log.d("FirebaseUtils", "User added to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("FirebaseUtils", "Error adding user to Firestore", e);
                    }
                });

    }
}