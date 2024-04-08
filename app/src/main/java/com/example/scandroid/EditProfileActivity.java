package com.example.scandroid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentTransaction;

import io.github.muddz.styleabletoast.StyleableToast;

/**
 * The EditProfileActivity class allows users to edit their profile information,
 * including name, email, phone number, about me, push notification preferences,
 * and profile picture. It implements the AllowAccessCameraRollFragment.OnImageChangedListener
 * interface to communicate changes in the profile picture between the activity and the fragment.
 *
 */

public class EditProfileActivity extends AppCompatActivity{
    private EditText nameEditText, emailEditText, phoneEditText, aboutMeEditText;
    private CheckBox pushNotificationCheckBox;
    private ImageView profileImageView;
    private String userID;
    private User currentUser;

    /**
     * Initializes the activity and sets up views, listeners, and data retrieval.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
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

            // validate input
            boolean isValidInput = handleUserInput(name, email, phone);

            if (isValidInput) {
                DBAccessor database1 = new DBAccessor();
                currentUser.setProfilePictureUrl(""); // Clear the existing URL if any
                currentUser.setUserName(name);
                currentUser.setUserEmail(email);
                currentUser.setUserPhoneNumber(phone);
                currentUser.setUserAboutMe(aboutMe);
                database1.storeUser(currentUser);
                database1.storeUserProfileImage(userID, profilePicBitmap);
                finish();
            }
        });

        Button backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> finish());

        Button changePictureButton = findViewById(R.id.changePictureTextView);
        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowAccessCameraRollFragment chooseImageFragment = AllowAccessCameraRollFragment.newInstance(userID, profileImageView.getId(), "user", nameEditText.getText().toString());
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

    // sources:
    // https://www.geeksforgeeks.org/implement-email-validator-in-android/
    // https://www.geeksforgeeks.org/implement-phone-number-validator-in-android/
    /**
     * Validates user input for name, email, and phone number.
     *
     * @param name  The name entered by the user.
     * @param email The email entered by the user.
     * @param phone The phone number entered by the user.
     * @return True if all inputs are valid, false otherwise.
     */
    private boolean handleUserInput(String name, String email, String phone){
        boolean isValid = true;
        // check if name is a string
        if (name.isEmpty() || name.length() > 15 || !name.matches("[a-zA-Z]+")) {
            showToast("Please enter a valid name (up to 15 characters)");
            isValid = false;
        }
        // if user chooses to provide an email
        // check if email is valid
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            isValid = false;
        }
        // if user chooses to provide phone number
        // check if phone number is entered in any valid phone format
        if (!phone.isEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            showToast("Please enter a valid phone number");
            isValid = false;
        }
        return isValid;
    }

    /**
     * Displays a custom toast message.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        StyleableToast.makeText(this, message, R.style.customToast).show();
    }

    /**
     * Displays the AdminKeyFragment dialog.
     * This dialog prompts the user to enter an admin key for authentication.
     */
    private void showAdminKeyFragment() {
        AdminKeyFragment adminKeyFragment = new AdminKeyFragment();
        adminKeyFragment.show(getSupportFragmentManager(), "AdminKeyFragment");
    }

    /**
     * Updates the current user's admin key based on received data.
     *
     * @param data The data bundle containing the entered admin key.
     */
    public void onDataReceived(Bundle data) {
        String enteredAdminKey = data.getString("enteredAdminKey");
        currentUser.enterAdminKey(enteredAdminKey);
    }
}



