package com.example.scandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * AdminInspectEventFragment is shown when a user with admin permissions browses and clicks on an event
 * Allows the admin user to view event name, date, location, and gives the option to remove the event from the system
 */
public class AdminInspectEventFragment extends DialogFragment {
    private final DBAccessor database = new DBAccessor();
    String eventID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_inspect_event_fragment, container, false);
        TextView eventTitle = view.findViewById(R.id.fetch_inspect_event_title);
        TextView eventLocation = view.findViewById(R.id.fetch_inspect_event_location);
        TextView eventDate = view.findViewById(R.id.fetch_inspect_event_date);
        Button cancelButton = view.findViewById(R.id.cancel_inspect_event_button);
        Button removeButton = view.findViewById(R.id.remove_event_button);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventID = bundle.getString("eventID");
            database.accessEvent(eventID, event -> {
                if (event != null) {
                    eventTitle.setText(event.getEventName());
                    eventLocation.setText(new LocationGeocoder(getActivity()).coordinatesToAddress(event.getEventLocation()));
                    Calendar calendar = event.getEventDate();
                    String eventDateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                    eventDate.setText(eventDateText);
                    cancelButton.setOnClickListener(v -> dismiss());
                    removeButton.setOnClickListener(v -> {
                        database.deleteEvent(eventID);
                        database.deleteEventPoster(eventID);
                        database.deleteQRMain(eventID);
                        database.deleteQRPromo(eventID);
                    });
                } else {
                    dismiss();
                }
            });
        }
        return view;
    }
}
