package com.example.scandroid;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EventAttendeesArrayAdapter extends ArrayAdapter<Event.CheckIn> {
    private String currentEventID;
    public EventAttendeesArrayAdapter(Context context, ArrayList<Event.CheckIn> attendees, String eventID) {
        super(context,0, attendees);
        currentEventID = eventID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_attendee_list_content, parent, false);
        }
        else {
            view = convertView;
        }

        Event.CheckIn attendeeCheckIn = getItem(position);
        assert attendeeCheckIn != null;
        String userID = attendeeCheckIn.getUserID();
        DBAccessor database = new DBAccessor();
        User attendeeUser = database.accessUser(userID);

        TextView checkInTime = view.findViewById(R.id.event_attendees_list_checkin_time);
        TextView attendCount = view.findViewById(R.id.event_attendees_list_attendance_count);
        TextView attendeeName = view.findViewById(R.id.event_attendees_list_attendee_name);
        ImageView profilePicture = view.findViewById(R.id.event_attendees_list_profile_picture);


        String checkInTimeText = "Check-in Time: " + attendeeCheckIn.getCheckInTime();
        checkInTime.setText(checkInTimeText);

        attendCount.setText(attendeeUser.getTimesAttended(currentEventID));

        attendeeName.setText(attendeeUser.getUserName());
//        Bitmap profileBitmap = database.accessUserProfileImage(attendeeCheckIn.getUserID());
//        profilePicture.setImageBitmap(profileBitmap);
        return view;
    }
}
