package com.example.scandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

/**
 * Popup fragment to alert users attempting to check into an event that has already ended
 */
public class EventEndedNoticeFragment extends DialogFragment {

    /**
     * Default constructor for the EventEndedNoticeFragment
     */
    EventEndedNoticeFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notice_event_ended_fragment, container, false);
        TextView removalText = view.findViewById(R.id.ended_notice_text);
        Button confirmButton = view.findViewById(R.id.confirm_ended_notice_button);
        //confirmButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            requireActivity().finish();
            dismiss();
        });
        return view;
    }
}
