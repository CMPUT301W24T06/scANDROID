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
    FragmentManager fragmentManager;
    boolean isAdmin;
    private OnProfileImageClickListener onProfileImageClickListener;
    /**
     * Constructs a new CreatedEventsArrayAdapter
     *
     * @param context context where the adapter is being used
     * @param myEvents list of event IDs to display
     *
     */
    public CreatedEventsArrayAdapter(Context context, ArrayList<Tuple<Event, Bitmap>> myEvents, FragmentManager fragmentManager, OnProfileImageClickListener onProfileImageClickListener) {
        super(context,0, myEvents);
        this.fragmentManager = fragmentManager;
        this.onProfileImageClickListener = onProfileImageClickListener;
    }

    public CreatedEventsArrayAdapter(Context context, ArrayList<Tuple<Event, Bitmap>> myEvents, FragmentManager fragmentManager) {
        super(context,0, myEvents);
        this.fragmentManager = fragmentManager;
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
        if (onProfileImageClickListener!=null) {
            eventPoster.setOnClickListener(v -> {
                //DialogFragment imageInspectPrompt = new AdminInspectImageFragment(bitmap);
                //Bundle bundle = new Bundle();
                //bundle.putString("eventID", eventID);
                //imageInspectPrompt.setArguments(bundle);

                //FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.add(android.R.id.content, imageInspectPrompt);
                //transaction.commit();
                onProfileImageClickListener.onProfileImageClicked(tuple.first, tuple.second);
            });
        }
        return view;
    }

    public interface OnProfileImageClickListener {
        void onProfileImageClicked(Event event, Bitmap bitmap);
    }
}
