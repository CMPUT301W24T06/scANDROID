package com.example.scandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * HomepageActivity is the main page of the app, and deals with displaying and updating the user's profile.
 * It interacts with the database in order to keep all information in the UI and
 * in the database up to date and consistent.
 * This includes a profile image, contact information, and most importantly
 * events, both being organized and being attended by the user.
 * It extends AppCompatActivity in order to be compatible with
 * older versions of Android as well as use modern Android features.
 */

// source for navigation bar logic: https://www.youtube.com/watch?v=lOTIedfP1OA
// gradle dependency for switch case:https://stackoverflow.com/questions/76430646/constant-expression-required-when-trying-to-create-a-switch-case-block
public class HomepageActivity extends AppCompatActivity {
    TabLayout homepageTabs;
    ViewPager2 homepagePager;
    HomepageActivityPageAdapter homepageActivityPageAdapter;
    BottomNavigationView navigationBar;

    AppCompatButton editProfileButton;
    AppCompatButton createEventButton;
    ImageView profilePicture;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    DBAccessor database = new DBAccessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        userID = new DeviceIDRetriever(HomepageActivity.this).getDeviceId();
        //userID = "testID";

        // deals with the bottom bar
        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.home_button);

        // logic for tabs that toggle between events going to and events organized
        homepageTabs = findViewById(R.id.homepage_tabs);
        homepagePager = findViewById(R.id.homepage_pager);
        //homepageActivityPageAdapter = new HomepageActivityPageAdapter(this, userID);
        //homepagePager.setAdapter(homepageActivityPageAdapter);

        // initialize buttons for navigation between activities
        editProfileButton = findViewById(R.id.edit_profile_button);
        createEventButton = findViewById(R.id.create_event_button);

        // handle navigation between tabs
        homepageTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                homepagePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        homepagePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(homepageTabs.getTabAt(position)).select();
            }
        });

        navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_button:
                        return true;
                    case R.id.qr_button:
                        Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        return true;
                    case R.id.browse_button:
                        startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                        return true;
                    case R.id.notification_button:
                        startActivity(new Intent(getApplicationContext(), EventViewAnnouncementsActivity.class));
                        return true;
                }
                return false;
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, EditProfileActivity.class);
            String userID = new DeviceIDRetriever(HomepageActivity.this).getDeviceId();
            intent.putExtra("userID", userID);
            startActivity(intent);
        });

        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

    }

    /**
     * Called when the activity is resumed from a paused state.
     * It updates the user profile information in the database.
     * It accesses the user's information from the database.
     * The corresponding UI elements are also set and updated accordingly.
     */
    @Override
    protected void onResume() {
        super.onResume();
        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
            if (user == null) {
                //Create a new User object
                user = new User();
                user.setUserID(userID);
                displayWelcomeFragment();
                database.storeUser(user);
            }


                homepageActivityPageAdapter = new HomepageActivityPageAdapter(this, userID);
                homepagePager.setAdapter(homepageActivityPageAdapter);

            TextView profileName = findViewById(R.id.homepage_name_text);
            profileName.setText(user.getUserName());
            profilePicture = findViewById(R.id.profile_image);
            database.accessUserProfileImage(userID, new BitmapCallback() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    profilePicture.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e) {
                    Toast.makeText(HomepageActivity.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    /**
     * Updates the user profile by retrieving the updated user name from SharedPreferences
     * and updating the TextView with the updated user name.
     */
    private void updateProfile() {
        // Retrieve the updated user name from SharedPreferences or any other storage mechanism
        SharedPreferences sharedPreferences = getSharedPreferences("namePref", MODE_PRIVATE);
        String updatedUserName = sharedPreferences.getString("updatedUserName", "");

        // Update the TextView with the updated user name
        TextView homepageNameText = findViewById(R.id.homepage_name_text);
        homepageNameText.setText(updatedUserName);
    }


    /**
     * Displays the welcome fragment
     * and gives the user the option to enter their name upon first using the app
     */
    private void displayWelcomeFragment() {
        // Create an instance of the WelcomeFragment
        WelcomeFragment welcomeFragment = new WelcomeFragment();

        // Use a FragmentTransaction to add the fragment to the layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, welcomeFragment);
        transaction.commit();
    }

    /**
     * Deals with if the user chooses not to enter their name when prompted by the fragment
     * by displaying a randomly generated Guest ID and updating the database
     * @param randomName The randomly generated name to be used as the guest ID.
     */
    public void onMaybeLaterClicked(String randomName) {
        // Handle "Maybe Later" button click
        // Close the fragment and set the generated random name as the activity name
        updateActivityName(randomName);
        updateNameInFirebase(randomName);
    }

    /**
     * Deals with if the user chooses to enter their name when prompted by the fragment
     * by saving and displaying the name entered and updating the database
     * @param enteredName the name entered by the user
     */
    public void onEnterClicked(String enteredName) {
        // Handle "Enter" button click
        // Close the fragment and set the entered name as the activity name
        updateActivityName(enteredName);
        updateNameInFirebase(enteredName);
    }

    /**
     * Updates the user's name displayed on the homepage profile
     * @param newName the new name to be displayed
     */
    private void updateActivityName(String newName) {
        TextView homepageNameText = findViewById(R.id.homepage_name_text);
        homepageNameText.setText(newName);
    }

    /**
     * Updates the user's name in the database
     * @param newName the new name to be updated in the database
     */
    private void updateNameInFirebase(String newName) {
        // Update the user's name in Firebase
        db.collection("Users").document(userID)
                .update("userName", newName);
    }

    public void onNameReceived(Bundle data) {
        String userName = data.getString("userName");
        TextView profileName = findViewById(R.id.homepage_name_text);
        profileName.setText(userName);
        Bitmap newProfilePicture = new ProfilePictureGenerator().generatePictureBitmap(userName);
        profilePicture.setImageBitmap(newProfilePicture);
        database.storeUserProfileImage(userID, newProfilePicture);
        db.collection("Users").document(userID)
                .update("userName", userName);
    }
}