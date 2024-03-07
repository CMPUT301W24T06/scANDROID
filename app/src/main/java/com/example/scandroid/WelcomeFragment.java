package com.example.scandroid;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class WelcomeFragment extends DialogFragment {

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mock_homepage_activity, container, false);

        EditText nameEditText = view.findViewById(R.id.name_prompt_edit_text);
        Button maybeLaterButton = view.findViewById(R.id.maybe_later_button);
        Button enterButton = view.findViewById(R.id.enter_button);

        maybeLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a random name (Guest + random number)
                String randomName = "Guest" + new Random().nextInt(10000);
                // Close the fragment and pass the random name to the activity
                ((HomepageActivity) requireActivity()).onMaybeLaterClicked(randomName);
                hideKeyboard();
                dismiss();
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered name
                String enteredName = nameEditText.getText().toString();
                // Close the fragment and pass the entered name to the activity
                ((HomepageActivity) requireActivity()).onEnterClicked(enteredName);
                hideKeyboard();
                dismiss();
            }
        });

        return view;
    }
    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

