package com.example.scandroid;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private EditText nameEditText, emailEditText, phoneEditText, aboutMeEditText;
    private CheckBox pushNotificationCheckBox;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private User currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;

    private String profilePictureURL;
    private Uri selectedImageUri;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity); // Replace with the actual layout file name
        // Get the unique device ID
        String deviceId = new DeviceIDRetriever(this).getDeviceId();

        // Now you can use the deviceId as needed in your application
        Toast.makeText(this, "Device ID: " + deviceId, Toast.LENGTH_SHORT).show();
        userID = deviceId;

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);
        pushNotificationCheckBox = findViewById(R.id.pushNotificationCheckBox);
        ImageView profileImageView = findViewById(R.id.image_inside_card);

        DBAccessor database = new DBAccessor();
        database.accessUser(userID, new UserCallback() {
            @Override
            public void onUserRetrieved(User user) {
                nameEditText.setText(user.getUserName());
                phoneEditText.setText(user.getUserPhoneNumber());
                emailEditText.setText(user.getUserEmail());
                aboutMeEditText.setText(user.getUserAboutMe());
                database.accessUserProfileImage(userID, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        profileImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                    }
                });

                currentUser = user;

            }
        });
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setUserID(userID);
        }

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle update button click
                //updateProfile(currentUser);
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String aboutMe = aboutMeEditText.getText().toString();
                boolean receiveNotifications = pushNotificationCheckBox.isChecked();
                ImageView profileImageView = findViewById(R.id.image_inside_card);
                Bitmap profilePicBitmap = new BitmapConfigurator().drawableToBitmap(profileImageView.getDrawable());
                DBAccessor database = new DBAccessor();
                currentUser.setUserName(name);
                currentUser.setUserEmail(email);
                currentUser.setUserPhoneNumber(phone);
                currentUser.setUserAboutMe(aboutMe);
                database.storeUser(currentUser);
                database.storeUserProfileImage(userID, profilePicBitmap);
                finish();
            }
        });

        Button backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> finish());

        Button changePictureButton = findViewById(R.id.changePictureTextView);
        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to open the gallery or camera
                //openGallery();
                requestStoragePermission();
            }
        });

        AppCompatButton adminWrenchButton = findViewById(R.id.admin_wrench_button);
        adminWrenchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show AdminKeyFragment when the admin wrench button is clicked
                showAdminKeyFragment();
            }
        });
    }

    private void updateProfile() {
        // Implement the logic to update the user's profile
        // Retrieve data from EditText fields and perform the necessary actions
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String aboutMe = aboutMeEditText.getText().toString();
        boolean receiveNotifications = pushNotificationCheckBox.isChecked();

        String currentProfilePictureUrl = profilePictureURL; // Replace with the actual way you retrieve the current URL
        String newProfilePictureUrl = currentProfilePictureUrl; // Default to the existing URL



        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("userName", name);
        updatedUserData.put("userPhoneNumber", phone);
        updatedUserData.put("userEmail", email);
        updatedUserData.put("userAboutMe", aboutMe);
        updatedUserData.put("profilePictureUrl", newProfilePictureUrl); // Include the new profile picture URL



    }   //
    public void changeProfilePicture(View view) {
        // Call the method to open the gallery or camera
        openGallery();
    }

    private void openGallery() {
        // Create an Intent to open the device's gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from the gallery intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle the selected image URI, you may want to update the ImageView or store the URI
            Uri selectedImageUri = data.getData();
            // Do something with the selected image URI
            CardView cardView = findViewById(R.id.edit_profile_image);
            ImageView profileImageView = findViewById(R.id.image_inside_card);
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    /**
     * Requests user for permission for app to access their files
     */
    //Source: https://stackoverflow.com/questions/39866869/how-to-ask-permission-to-access-gallery-on-android-m
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied. Cannot pick image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAdminKeyFragment() {
        AdminKeyFragment adminKeyFragment = new AdminKeyFragment();
        adminKeyFragment.show(getSupportFragmentManager(), "AdminKeyFragment");
    }
}


