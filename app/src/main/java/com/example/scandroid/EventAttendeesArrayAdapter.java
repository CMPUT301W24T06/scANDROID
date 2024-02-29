package com.example.scandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventAttendeesArrayAdapter extends ArrayAdapter<Event.CheckIn> {

    public EventAttendeesArrayAdapter(Context context, ArrayList<Event.CheckIn> attendees) {
        super(context,0, attendees);
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

        Event.CheckIn attendee = getItem(position);
        TextView checkInTime = view.findViewById(R.id.event_attendees_list_checkin_time);
        TextView attendCount = view.findViewById(R.id.event_attendees_list_attendance_count);
        TextView attendeeName = view.findViewById(R.id.event_attendees_list_attendee_name);
        ImageView profilePicture = view.findViewById(R.id.event_attendees_list_profile_picture);

        assert attendee != null;
        String checkInTimeText = "Check-in Time: " + attendee.getCheckInTime();
        checkInTime.setText(checkInTimeText);
        //attendeeCount.setText();
        //Need getter for # of times user has checking into an event

        //attendeeName.setText();
        //Some way to get the name from firebase with the ID from attendee.getUserID();
        return view;
    }
}
