package com.example.scandroid;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * EventMilestonesArrayAdapter is a class responsible
 * for EventMilestones in the EventMilestonesActivity.
 */
public class EventMilestonesArrayAdapter extends ArrayAdapter<Integer> {
    public EventMilestonesArrayAdapter(Context context, ArrayList<Integer> eventMilestones ) {
        super(context, 0, eventMilestones);
    }
}
