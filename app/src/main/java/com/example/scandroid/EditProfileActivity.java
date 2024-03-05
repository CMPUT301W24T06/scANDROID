package com.example.scandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, aboutMeEditText;
    private CheckBox pushNotificationCheckBox;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = "uniqueUserID"; // Replace with the actual user ID
    private static final int PICK_IMAGE_REQUEST = 1;

    private String profilePictureURL;
    private Uri selectedImageUri;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity); // Replace with the actual layout file name

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);
        pushNotificationCheckBox = findViewById(R.id.pushNotificationCheckBox);
        profilePictureURL = "https://upload.wikimedia.org/wikipedia/commons/a/ac/Default_pfp.jpg";


        // Retrieve existing details (replace with your data retrieval logic)
        //String existingName = "John Doe"; // Replace with your actual logic
        //String existingEmail = "john.doe@example.com";
        //String existingPhone = "123-456-7890";
        //String existingAboutMe = "I love programming and exploring new technologies. ayo what the heck just checking whay atoaysdyasdyadw";
        //boolean existingReceiveNotifications = true;


        // Retrieve current user data
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User currentUser = documentSnapshot.toObject(User.class);


                        // Now you can use the currentUser object to populate your UI components
                        nameEditText.setText(currentUser.getUserName());
                        phoneEditText.setText(currentUser.getUserPhoneNumber());
                        emailEditText.setText(currentUser.getUserEmail());
                        aboutMeEditText.setText(currentUser.getUserAboutMe());
                        //pushNotificationCheckBox.setChecked(currentUser.getNotifiedBy());
                        // ... other fields
                    } else {
                        // Handle the case where the user document does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure, e.g., show an error message
                });


        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle update button click
                updateProfile();

            }
        });

        Button changePictureButton = findViewById(R.id.changePictureTextView);
        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to open the gallery or camera
                openGallery();
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
        updatedUserData.put("profilePictureUrl", newProfilePictureUrl); // Include the new profile picture URL


        db.collection("Users").document(userId)
                .update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    nameEditText.setText(name);

                    // Handle the success, e.g., show a toast or navigate to another activity
                })
                .addOnFailureListener(e -> {
                    // Handle the failure, e.g., show an error message
                });


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

    private void showAdminKeyFragment() {
        AdminKeyFragment adminKeyFragment = new AdminKeyFragment();
        adminKeyFragment.show(getSupportFragmentManager(), "AdminKeyFragment");
    }
}


