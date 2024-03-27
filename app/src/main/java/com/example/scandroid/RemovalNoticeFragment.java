package com.example.scandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

/**
 * Popup fragment to alert users to the removal of an event
 */
public class RemovalNoticeFragment extends DialogFragment {
    RemovalNoticeFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notice_event_removed_fragment, container, false);
        TextView removalText = view.findViewById(R.id.removal_notice_text);
        Button confirmButton = view.findViewById(R.id.confirm_removal_notice_button);
        confirmButton.setOnClickListener(v -> dismiss());
        return view;
    }
}
