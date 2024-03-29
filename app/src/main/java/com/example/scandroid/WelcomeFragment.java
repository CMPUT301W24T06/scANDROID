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

/**
 * A {@link DialogFragment} that represents a welcome screen prompting the user
 * to enter their name or proceed as a guest. The fragment provides options to
 * either enter a name or continue as a guest with a randomly generated name.
 */
public class WelcomeFragment extends DialogFragment {
    /**
     * Default constructor for the WelcomeFragment.
     * Required empty public constructor.
     */
    public WelcomeFragment() {
    }

    /**
     * Called to create the view for this fragment, using the given layout inflater
     * and container. Initializes views and handles UI interactions such as maybe
     * later and enter buttons.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. Can be used to generate the LayoutParams
     *                           of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return A View inflated from the mock_homepage_activity layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mock_homepage_activity, container, false);

        EditText nameEditText = view.findViewById(R.id.name_prompt_edit_text);
        Button maybeLaterButton = view.findViewById(R.id.maybe_later_button);
        Button enterButton = view.findViewById(R.id.enter_button);

        maybeLaterButton.setOnClickListener(v -> {
            // Generate a random name (Guest + random number)
            String randomName = "Guest" + new Random().nextInt(10000);
            // Close the fragment and pass the random name to the activity
            //((HomepageActivity) requireActivity()).onMaybeLaterClicked(randomName);
            HomepageActivity activity = (HomepageActivity) getActivity();
            Bundle userNameGeneration = new Bundle();
            userNameGeneration.putString("userName", randomName);
            if (activity != null) {
                activity.onNameReceived(userNameGeneration);
            }
            hideKeyboard();
            dismiss();
        });

        enterButton.setOnClickListener(v -> {
            // Get the entered name
            String enteredName = nameEditText.getText().toString();
            // Close the fragment and pass the entered name to the activity
            //((HomepageActivity) requireActivity()).onEnterClicked(enteredName);
            if (enteredName.isEmpty()) {
                enteredName = "Guest" + new Random().nextInt(10000);
            }
            HomepageActivity activity = (HomepageActivity) getActivity();
            Bundle userNameGeneration = new Bundle();
            userNameGeneration.putString("userName", enteredName);
            if (activity != null) {
                activity.onNameReceived(userNameGeneration);

            }
            hideKeyboard();
            dismiss();
        });

        return view;
    }
    /**
     * Helper method to hide the keyboard.
     * This method is used to hide the soft keyboard when necessary.
     */
    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

