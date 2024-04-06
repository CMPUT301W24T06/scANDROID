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
 * Popup fragment to alert users of milestones.
 */
public class MilestoneAlertFragment extends DialogFragment {
    TextView milestoneCount;
    TextView milestoneText;
    Button confirmButton;
    DBAccessor database;
    MilestoneAlertFragment(){}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.milestone_fragment, container, false);
        milestoneCount = view.findViewById(R.id.milestone_fragment_count);
        milestoneText = view.findViewById(R.id.milestone_fragment_text);
        confirmButton = view.findViewById(R.id.milestone_ok_button);
        database = new DBAccessor();

        confirmButton.setOnClickListener(v -> dismiss());

        // TODO: find out where to send this fragment from...
        Bundle bundle = this.getArguments(); // put milestone number in bundle
        if (bundle != null) { }

        return view;
    }
}
