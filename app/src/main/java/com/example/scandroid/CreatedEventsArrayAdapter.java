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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

/**
 * CreatedEventsArrayAdapter is an ArrayAdapter implementation
 * used in the app for displaying created events in a list view.
 */
public class CreatedEventsArrayAdapter extends ArrayAdapter<String> {

    /**
     * Constructs a new CreatedEventsArrayAdapter
     *
     * @param context context where the adapter is being used
     * @param myEvents list of event IDs to display
     * @param userID ID of the organizer (user who created the events)
     */
    public CreatedEventsArrayAdapter(Context context, ArrayList<String> myEvents, String userID) {
        super(context,0, myEvents);
    }

    /**
     * Gets a View that displays the data of a specific event in the data set
     *
     * @param position position of the item within the adapter's data set
     * @param convertView old view to reuse (if possible)
     * @param parent parent that this view will eventually be attached to
     * @return a View that corresponds to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.my_events_list_content, parent, false);
        }
        else {
            view = convertView;
        }

        TextView eventNameText = view.findViewById(R.id.my_events_content_name);
        ImageView eventPoster = view.findViewById(R.id.my_events_content_poster);
        String eventID = getItem(position);
        assert eventID != null;
        DBAccessor database = new DBAccessor();
        database.accessEvent(eventID, event -> {
            String eventName = event.getEventName();
            eventNameText.setText(eventName);
        });

        database.accessEventPoster(eventID, new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                eventPoster.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e) {
                Toast.makeText(view.getContext(), "Failed to retrieve event poster", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
