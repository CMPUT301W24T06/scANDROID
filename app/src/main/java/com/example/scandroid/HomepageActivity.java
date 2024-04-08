package com.example.scandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        //userID = "e9256b128bd8fb6a";
        //userID = "dac1f387416d7ffb";

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
                switch (item.getItemId()) {
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

        getFCMToken();
    }

    // Credit: https://www.youtube.com/watch?v=NF0RzhXDRKw
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                db.collection("Users").document(userID)
                        .update("fcmToken", token);
            }
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
            } else {
                updateSharedPreferences();
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

    public void onNameReceived(Bundle data) {
        String userName = data.getString("name");
        TextView profileName = findViewById(R.id.homepage_name_text);
        profileName.setText(userName);
        Bitmap newProfilePicture = new ProfilePictureGenerator().generatePictureBitmap(userName);
        profilePicture.setImageBitmap(newProfilePicture);
        database.storeUserProfileImage(userID, newProfilePicture);
        db.collection("Users").document(userID)
                .update("name", userName);
    }

    private void updateSharedPreferences() {
        // OpenAI ChatGPT 2024, how to implement shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MilestonesNotify", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // TODO: notify unnotified events, update notified events to next milestone, delete events that are not in organized
        // TODO: when about to notify, do a check to make sure number of attendees matches milestoneSeries[0] value
        // TODO: notify using a little toast, don't need an entire fragment for that

        // KEY: eventID, VALUES: Set<String> [currentMilestone, reachedBool]
        database.accessUser(userID, user -> {
            ArrayList<String> eventsOrganized = user.getEventsOrganized();
            if (!eventsOrganized.isEmpty()) {
                // the user is organizing some events
                for (String eventID : eventsOrganized) {
                    // going event by event
                    if (!sharedPreferences.contains(eventID)) {
                        // adding a new event and notifying if possible
                        database.accessEvent(eventID, event -> {
                            Set<String> eventDetails = new HashSet<>();
                            Integer currentMilestone = event.getEventMilestoneSeries().get(0).intValue();
                            String reachedBool;
                            Integer attendeesTotal = event.getEventAttendeesTotal();
                            if (attendeesTotal >= currentMilestone) {
                                reachedBool = "true";
                                // TODO: send a toast!
                                Log.d("Milestones", event.getEventName() + " has reached a new milestone: " + currentMilestone.toString());
                            } else {
                                reachedBool = "false";
                            }
                            eventDetails.add(currentMilestone.toString());
                            eventDetails.add(reachedBool);
                            editor.putStringSet(eventID, eventDetails);
                            editor.apply();
                        });
                    } else {
                        // if event already exists, notifying and updating if possible
                        Set<String> retrievedSet = new HashSet<>();
                        Set<String> eventDetails = sharedPreferences.getStringSet(eventID, retrievedSet);
                        List<String> eventDetailsList = new ArrayList<>(eventDetails);
                        database.accessEvent(eventID, event -> {
                            Long currentMilestone = Long.parseLong(eventDetailsList.get(0));
                            String reachedBool = eventDetailsList.get(1);
                            if (Objects.equals(reachedBool, "true")) {
                                // if reached true, check if theres a new milestone to update it
                                if (currentMilestone.intValue() < event.getEventMilestoneSeries().get(0).intValue()) {
                                    currentMilestone = event.getEventMilestoneSeries().get(0);
                                    if (event.getEventAttendeesTotal() >= currentMilestone.intValue()) {
                                        reachedBool = "true";
                                        // TODO: send a toast!
                                        Log.d("Milestones", event.getEventName() + " has reached a new milestone: " + currentMilestone.toString());
                                    } else {
                                        reachedBool = "false";
                                    }
                                }

                            } else {
                                // if reached false, check if it has reached the current milestone
                                if (event.getEventAttendeesTotal() >= currentMilestone.intValue()) {
                                    reachedBool = "true";
                                    // TODO: send a toast!
                                    Log.d("Milestones", event.getEventName() + " has reached a new milestone: " + currentMilestone.toString());
                                }
                            }
                            Set<String> updatedEventDetails = new HashSet<>();
                            updatedEventDetails.add(currentMilestone.toString());
                            updatedEventDetails.add(reachedBool);
                            editor.putStringSet(eventID, updatedEventDetails);
                            editor.apply();
                        });
                    }
                }
            } else {
                // delete all stored events bc there's nothing in organized
                Map<String, ?> allEvents = sharedPreferences.getAll();
                for (Map.Entry<String, ?> anEvent : allEvents.entrySet()) {
                    editor.remove(anEvent.getKey());
                    editor.apply();
                }

            }
        });
    }
}