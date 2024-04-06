package com.example.scandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * EventMilestonesArrayAdapter is a class responsible
 * for EventMilestones in the EventMilestonesActivity.
 */
public class EventMilestonesArrayAdapter extends ArrayAdapter<Long> {
    public EventMilestonesArrayAdapter(Context context, ArrayList<Long> eventMilestones ) { super(context, 0, eventMilestones);}
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.milestone_content, parent, false);
        } else {
            view = convertView;
        }
        Long milestone = super.getItem(position);
        TextView milestoneCount = view.findViewById(R.id.milestone_number);
        if (milestone != null) {
            milestoneCount.setText(String.valueOf(milestone));
            if (milestone.intValue() == 1) {
                TextView milestoneText = view.findViewById(R.id.milestone_text);
                milestoneText.setText(R.string.user_has_checked_into_your_event);
            }
        }

        return view;
    }}
