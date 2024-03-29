package com.example.scandroid;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
/**
 * A {@link DialogFragment} that represents an administrative key input dialog.
 * This fragment allows users to enter an administrative key for authentication
 * and performs actions based on the entered key.
 */
public class AdminKeyFragment extends DialogFragment {
    /**
     * Default constructor for the AdminKeyFragment.
     */
    public AdminKeyFragment() {
    }

    /**
     * Called to create the view for this fragment, using the given layout inflater
     * and container. Initializes views and handles UI interactions such as cancel
     * and confirm buttons.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the
     *                           view itself, but this can be used to generate the LayoutParams
     *                           of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return A View inflated from the fragment_admin_key layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_key, container, false);

        // Initialize views and handle UI interactions here
        Button btnCancel = view.findViewById(R.id.cancel_button_admin);
        btnCancel.setOnClickListener(v -> {
            // Dismiss the fragment when Cancel button is clicked
            dismiss();
        });

        Button btnConfirm = view.findViewById(R.id.confirm_button_admin);
        btnConfirm.setOnClickListener(v -> {
            // Handle confirmation logic here and perform actions accordingly
            EditText enterAdminKeyEdit = view.findViewById(R.id.key_admin_text);
            String enteredAdminKey = enterAdminKeyEdit.getText().toString();
            User user = new User();
            user.enterAdminKey(enteredAdminKey);
            if (user.getHasAdminPermissions()){
                //OpenAI, 2024, ChatGPT, How to pass info from fragment back to activity
                EditProfileActivity activity = (EditProfileActivity) getActivity();
                Bundle adminKeyResult = new Bundle();
                adminKeyResult.putString("enteredAdminKey", enteredAdminKey);
                if (activity != null) {
                    activity.onDataReceived(adminKeyResult);
                }
                dismiss();
            } else {
                enterAdminKeyEdit.setError("Please enter a valid admin key");
            }
        });
        return view;
    }
}
