package com.example.scandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

/**
 * Popup fragment for the purpose of alerting users
 */
public class NoticeFragment extends DialogFragment {

    String noticeText;
    /**
     * Default constructor for the NoticeFragment
     */
    NoticeFragment(String noticeText){
        this.noticeText = noticeText;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notice_fragment, container, false);
        TextView removalText = view.findViewById(R.id.notice_text);
        Button confirmButton = view.findViewById(R.id.confirm_notice_button);
        removalText.setText(noticeText);
        //confirmButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            requireActivity().finish();
            dismiss();
        });
        return view;
    }
}
