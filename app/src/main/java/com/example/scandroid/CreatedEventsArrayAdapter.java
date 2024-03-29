package com.example.scandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.util.ArrayList;

/**
 * CreatedEventsArrayAdapter is an ArrayAdapter implementation
 * used in the app for displaying created events in a list view.
 */
public class CreatedEventsArrayAdapter extends ArrayAdapter<Tuple<Event, Bitmap>> {
    private OnEventPosterClickListener onEventPosterClickListener;

    /**
     * Constructs a new CreatedEventsArrayAdapter
     *
     * @param context context where the adapter is being used
     * @param myEvents list of event IDs to display
     * @param onEventPosterClickListener Listening activity
     */
    public CreatedEventsArrayAdapter(Context context, ArrayList<Tuple<Event, Bitmap>> myEvents, OnEventPosterClickListener onEventPosterClickListener) {
        super(context,0, myEvents);
        this.onEventPosterClickListener = onEventPosterClickListener;
    }

    /**
     * Constructs a new CreatedEventsArrayAdapter without an event poster listener
     *
     * @param context context where the adapter is being used
     * @param myEvents list of event IDs to display
     */
    public CreatedEventsArrayAdapter(Context context, ArrayList<Tuple<Event, Bitmap>> myEvents) {
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
        Tuple<Event, Bitmap> tuple = getItem(position);
        assert tuple != null;
        String eventName = tuple.first.getEventName();
        eventNameText.setText(eventName);
        eventPoster.setImageBitmap(tuple.second);
        if (onEventPosterClickListener!=null) {
            eventPoster.setOnClickListener(v -> {
                onEventPosterClickListener.onEventPosterClicked(tuple.first, tuple.second);
            });
        }
        return view;
    }

    /**
     * Interface to allow for communication with host activity
     * Used for communicating about an ImageView listener of event poster in the array
     */
    public interface OnEventPosterClickListener {
        void onEventPosterClicked(Event event, Bitmap bitmap);
    }
}
