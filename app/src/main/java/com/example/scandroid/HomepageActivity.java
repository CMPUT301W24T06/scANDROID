package com.example.scandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomepageActivity extends AppCompatActivity {
    TabLayout homepageTabs;
    ViewPager2 homepagePager;
    HomepageActivityPageAdapter homepageActivityPageAdapter;
    BottomNavigationView navigationBar;

    AppCompatButton editProfileButton;
    AppCompatButton createEventButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        displayWelcomeFragment();

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.home_button);

        homepageTabs = findViewById(R.id.homepage_tabs);
        homepagePager = findViewById(R.id.homepage_pager);
        homepageActivityPageAdapter = new HomepageActivityPageAdapter(this);
        homepagePager.setAdapter(homepageActivityPageAdapter);

        editProfileButton = findViewById(R.id.edit_profile_button);
        createEventButton = findViewById(R.id.create_event_button);

        userId = CheckInScreenActivity.getDeviceId(this);

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
                        startActivity(new Intent(getApplicationContext(), QRScannerActivity.class));
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

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateProfile();
    }

    private void updateProfile() {
        // Retrieve the updated user name from SharedPreferences or any other storage mechanism
        SharedPreferences sharedPreferences = getSharedPreferences("namePref", MODE_PRIVATE);
        String updatedUserName = sharedPreferences.getString("updatedUserName", "");

        // Update the TextView with the updated user name
        TextView homepageNameText = findViewById(R.id.homepage_name_text);
        homepageNameText.setText(updatedUserName);
    }

    private void displayWelcomeFragment() {
        // Create an instance of the WelcomeFragment
        WelcomeFragment welcomeFragment = new WelcomeFragment();

        // Use a FragmentTransaction to add the fragment to the layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, welcomeFragment);
        transaction.commit();
    }
    public void onMaybeLaterClicked(String randomName) {
        // Handle "Maybe Later" button click
        // Close the fragment and set the generated random name as the activity name
        updateActivityName(randomName);
        updateNameInFirebase(randomName);
    }

    public void onEnterClicked(String enteredName) {
        // Handle "Enter" button click
        // Close the fragment and set the entered name as the activity name
        updateActivityName(enteredName);
        updateNameInFirebase(enteredName);

    }
    private void updateActivityName(String newName) {
        TextView homepageNameText = findViewById(R.id.homepage_name_text);
        homepageNameText.setText(newName);
    }
    private void updateNameInFirebase(String newName) {
        // Update the user's name in Firebase
        db.collection("Users").document(userId)
                .update("userName", newName);
    }


}
