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

public class CreatedEventsArrayAdapter extends ArrayAdapter<String> {

    public CreatedEventsArrayAdapter(Context context, ArrayList<String> myEvents, String userID) {
        super(context,0, myEvents);
    }

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
