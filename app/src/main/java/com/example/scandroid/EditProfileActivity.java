package com.example.scandroid;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements AllowAccessCameraRollFragment.OnImageChangedListener{

//    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    ActivityResultLauncher<Intent> launcher;
    private EditText nameEditText, emailEditText, phoneEditText, aboutMeEditText;
    private CheckBox pushNotificationCheckBox;
    private ImageView profileImageView;
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
        userID = new DeviceIDRetriever(this).getDeviceId();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);
        pushNotificationCheckBox = findViewById(R.id.pushNotificationCheckBox);
        profileImageView = findViewById(R.id.image_inside_card);

        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
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

        });
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setUserID(userID);
        }

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(view -> {
            //updateProfile(currentUser);
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String aboutMe = aboutMeEditText.getText().toString();
            boolean receiveNotifications = pushNotificationCheckBox.isChecked();
            Bitmap profilePicBitmap = new BitmapConfigurator().drawableToBitmap(profileImageView.getDrawable());
            DBAccessor database1 = new DBAccessor();
            currentUser.setUserName(name);
            currentUser.setUserEmail(email);
            currentUser.setUserPhoneNumber(phone);
            currentUser.setUserAboutMe(aboutMe);
            database1.storeUser(currentUser);
            database1.storeUserProfileImage(userID, profilePicBitmap);
            finish();
        });

        Button backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> finish());

        Button changePictureButton = findViewById(R.id.changePictureTextView);
        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowAccessCameraRollFragment chooseImageFragment = AllowAccessCameraRollFragment.newInstance(userID);
                chooseImageFragment.setImageChangedListener(EditProfileActivity.this);
                // Use a FragmentTransaction to add the fragment to the layout
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, chooseImageFragment);
                transaction.commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
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

    private void showAdminKeyFragment() {
        AdminKeyFragment adminKeyFragment = new AdminKeyFragment();
        adminKeyFragment.show(getSupportFragmentManager(), "AdminKeyFragment");
    }

    /**
     * Allows for communication of the change in profile picture between EditProfileActivity and AllowAccessCameraRollFragment
     * @param newBitmap The new bitmap profile picture
     */
    @Override
    public void onImageChanged(Bitmap newBitmap) {
        Log.d("AllowAccessCameraRollFragment", "onImageChanged is called");
        if (newBitmap != null && profileImageView != null) {
            profileImageView.setImageBitmap(newBitmap);
        }
    }

}



