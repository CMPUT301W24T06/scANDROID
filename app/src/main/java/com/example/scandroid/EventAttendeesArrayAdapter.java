package com.example.scandroid;



import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Array adapter for the list of users that are attending an event
 */

public class EventAttendeesArrayAdapter extends ArrayAdapter<Event.CheckIn> {
    private String currentEventID;

    User attendeeUser;

    /**
     * Constructs a new EventAttendeesArrayAdapter.
     *
     * @param context    The context in which the adapter is being used.
     * @param attendees  The list of attendees to be displayed.
     * @param eventID    The ID of the current event.
     */
    public EventAttendeesArrayAdapter(Context context, ArrayList<Event.CheckIn> attendees, String eventID) {
        super(context,0, attendees);
        currentEventID = eventID;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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
        if (attendeeCheckIn != null) {
            String userID = attendeeCheckIn.getUserID();
            DBAccessor database = new DBAccessor();
            database.accessUser(userID, new UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    TextView checkInTime = view.findViewById(R.id.event_attendees_list_checkin_time);
                    TextView attendCount = view.findViewById(R.id.event_attendees_list_attendance_count);
                    TextView attendeeName = view.findViewById(R.id.my_events_content_name);
                    ImageView profilePicture = view.findViewById(R.id.my_events_content_poster);

                    String checkInTimeText = "Check-in Time: " + attendeeCheckIn.getCheckInTime();
                    checkInTime.setText(checkInTimeText);

                    if (user != null) {
                        String timesAttended = "Attendance Count: " + String.valueOf(user.getTimesAttended(currentEventID));
                        attendCount.setText(timesAttended);
                        attendeeName.setText(user.getUserName());

                        database.accessUserProfileImage(attendeeCheckIn.getUserID(), new BitmapCallback() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap) {
                                profilePicture.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Exception e) {
                                Log.d("BitmapLoad", "Failed to load image bitmap");
                            }
                        });
                    } else {
                        // Handle case when user is null
                        attendCount.setText("N/A");
                        attendeeName.setText("N/A");
                        // Set a default profile picture or handle it as per your requirement
                    }
                }
            });
        }

        return view;
    }
}
