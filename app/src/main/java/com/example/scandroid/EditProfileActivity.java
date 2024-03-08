package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements AllowAccessCameraRollFragment.OnImageChangedListener{
    ActivityResultLauncher<Intent> launcher;
    private EditText nameEditText, emailEditText, phoneEditText, aboutMeEditText;
    private CheckBox pushNotificationCheckBox;
    private ImageView profileImageView;
    private String userID;
    private User currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;

    private String profilePictureURL;
    private Uri selectedImageUri;

    // ActivityResultLauncher for image selection
    // Replace the previous ActivityResultLauncher with GetContent
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    handleImageSelection(result);
                }
            });

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
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String aboutMe = aboutMeEditText.getText().toString();
            boolean receiveNotifications = pushNotificationCheckBox.isChecked();
            Bitmap profilePicBitmap = new BitmapConfigurator().drawableToBitmap(profileImageView.getDrawable());
            DBAccessor database1 = new DBAccessor();
            currentUser.setProfilePictureUrl(""); // Clear the existing URL if any
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
                AllowAccessCameraRollFragment chooseImageFragment = AllowAccessCameraRollFragment.newInstance(userID, profileImageView.getId());
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
    private void handleImageSelection(Uri imageUri) {
        // Clear the existing URL from the currentUser object
        currentUser.setProfilePictureUrl("");

        // Update the selectedImageUri
        selectedImageUri = imageUri;

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (profileImageView != null) {
                // Update the ImageView directly
                profileImageView.setImageBitmap(bitmap);
                Log.d("EditProfileActivity", "ImageView updated successfully");

                if (bitmap != null) {
                    // Update the backend with the new image
                    DBAccessor database = new DBAccessor();
                    database.storeUserProfileImage(userID, bitmap);
                } else {
                    Log.e("EditProfileActivity", "Bitmap is null");
                }
            } else {
                Log.e("EditProfileActivity", "ImageView is null");
            }

            // Optionally close the InputStream
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("EditProfileActivity", "Error converting image to bitmap: " + e.getMessage());
            Toast.makeText(this, "Error converting image to bitmap", Toast.LENGTH_SHORT).show();
        }
    }
}



