package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays a User's profile when an organizer selects an attendee from the list of users attending their event
 */
public class ProfileInfoActivity extends AppCompatActivity {
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info_activity);
        TextView profileNameText = findViewById(R.id.profile_info_name);
        TextView profileEmailText = findViewById(R.id.profile_info_email_text);
        TextView profilePhoneText = findViewById(R.id.profile_info_phone_text);
        TextView profileCountText = findViewById(R.id.profile_info_count_text);
        TextView profileCheckInText = findViewById(R.id.profile_info_checkin_text);
        ImageView profilePicture = findViewById(R.id.profile_info_image);
        Button backButton = findViewById(R.id.profile_info_back_arrow);

        //Fills in the profile with the User's details
        Event.CheckIn attendeeCheckIn = (Event.CheckIn) getIntent().getSerializableExtra("attendee");
        String eventID = getIntent().getStringExtra("eventID");
        DBAccessor database = new DBAccessor();
        database.accessUser(attendeeCheckIn.getUserID(), new UserCallback() {
            @Override
            public void onUserRetrieved(User user) {
                currentUser = user;
            }
        });
        profileNameText.setText(currentUser.getUserName());
        profileEmailText.setText(currentUser.getUserEmail());
        profilePhoneText.setText(currentUser.getUserPhoneNumber());
        profileCountText.setText(currentUser.getTimesAttended(eventID));
        profileCheckInText.setText((CharSequence) attendeeCheckIn.getCheckInTime());
        database.accessUserProfileImage(currentUser.getUserID(), new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                profilePicture.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e) {
                Log.d("BitmapLoad", "Failed to load image bitmap");
            }
        });

        backButton.setOnClickListener(v -> finish());
    }
}