package com.example.scandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info_activity);
        TextView profileNameText = findViewById(R.id.profile_info_name);
        TextView profileEmailText = findViewById(R.id.profile_info_email_text);
        TextView profilePhoneText = findViewById(R.id.profile_info_phone_text);
        TextView profileCountText = findViewById(R.id.profile_info_count_text);
        TextView profileCheckInText = findViewById(R.id.profile_info_checkin_text);
        Button backButton = findViewById(R.id.profile_info_back_arrow);

        Event.CheckIn attendeeCheckIn = (Event.CheckIn) getIntent().getSerializableExtra("attendee");
        String eventID = getIntent().getStringExtra("eventID");
        DBAccessor database = new DBAccessor();
        User user = database.accessUser(attendeeCheckIn.getUserID());
        profileNameText.setText(user.getUserName());
        profileEmailText.setText(user.getUserEmail());
        profilePhoneText.setText(user.getUserPhoneNumber());
        profileCountText.setText(user.getTimesAttended(eventID));
        profileCheckInText.setText((CharSequence) attendeeCheckIn.getCheckInTime());


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}