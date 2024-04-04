package com.example.scandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays a User's profile when either an organizer selects an attendee from the list of users attending their event
 * or they are selected from the list when browsing all users.
 */
public class ProfileInfoActivity extends AppCompatActivity implements onClickListener{
    String userID;
    boolean isAttendee;
    Bitmap userProfilePicture;

    ImageView profilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info_activity);
        TextView profileNameText = findViewById(R.id.profile_info_name);
        TextView profileEmailText = findViewById(R.id.profile_info_email_text);
        TextView profilePhoneText = findViewById(R.id.profile_info_phone_text);
        TextView profileCountText = findViewById(R.id.profile_info_count_text);
        TextView profileCheckInText = findViewById(R.id.profile_info_checkin_text);
        TextView profileAboutMeText = findViewById(R.id.profile_info_about_me_text);
        Button backButton = findViewById(R.id.profile_info_back_arrow);
        profilePicture = findViewById(R.id.profile_info_image);
        Button removeButton = findViewById(R.id.profile_info_remove_profile_button);

        //Fills in the profile with the User's details
        if (getIntent().getSerializableExtra("attendee") != null){
            isAttendee = true;
            Event.CheckIn attendeeCheckIn = (Event.CheckIn) getIntent().getSerializableExtra("attendee");
            assert attendeeCheckIn != null;
            String checkInTimeText = "Check-in Time: " + attendeeCheckIn.getCheckInTime();
            profileCheckInText.setText(checkInTimeText);
            userID = attendeeCheckIn.getUserID();
        } else {
            profileCountText.setVisibility(View.INVISIBLE);
            profileCheckInText.setVisibility(View.INVISIBLE);
            TextView profileCountTitle = findViewById(R.id.profile_info_count_title);
            profileCountTitle.setVisibility(View.INVISIBLE);
            TextView profileCheckInTitle = findViewById(R.id.profile_info_checkin_title);
            profileCheckInTitle.setVisibility(View.INVISIBLE);
            userID = getIntent().getStringExtra("userID");
        }
        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
            profileNameText.setText(user.getUserName());
            profileEmailText.setText(user.getUserEmail());
            profilePhoneText.setText(user.getUserPhoneNumber());
            profileAboutMeText.setText(user.getUserAboutMe());
            if (isAttendee){
                String eventID = getIntent().getStringExtra("eventID");
                profileCountText.setText(user.getTimesAttended(eventID));
            }

            database.accessUserProfileImage(userID, new BitmapCallback() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    userProfilePicture = bitmap;
                    profilePicture.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e) {
                    Log.d("BitmapLoad", "Failed to load image bitmap");
                }
            });

            profilePicture.setOnClickListener(v -> {
                DialogFragment imageInspectPrompt = new AdminInspectImageFragment(userProfilePicture, ProfileInfoActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                imageInspectPrompt.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, imageInspectPrompt);
                transaction.commit();
            });

            if (getIntent().getBooleanExtra("ifAdmin", false)){
                removeButton.setVisibility(View.VISIBLE);
                removeButton.setOnClickListener(v -> {
                    database.deleteUser(userID);
                    for (String eventID: user.getEventsSignedUp()){
                        database.accessEvent(eventID, event -> {
                            event.deleteEventSignUp(userID);
                            database.storeEvent(event);
                        });
                    }
                    for (String eventID: user.getEventsAttending()){
                        database.accessEvent(eventID, event -> {
                            event.deleteEventCheckIn(userID);
                            database.storeEvent(event);
                        });
                    }

                    for (String eventID: user.getEventsOrganized()){
                        database.deleteEvent(eventID);
                    }
                    finish();
                });
            }
        });


        backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void onClick() {
        profilePicture.setImageBitmap(null);
    }
}