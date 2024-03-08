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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the fragment when Cancel button is clicked
                dismiss();
            }
        });

        Button btnConfirm = view.findViewById(R.id.confirm_button_admin);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle confirmation logic here
                // and perform actions accordingly
                //String enteredAdminKey = ((EditText) view.findViewById(R.id.editTextAdminKey)).getText().toString();
                //if (isValidAdminKey(enteredAdminKey)) {
                    // Admin key is valid, perform actions
                    // For now, just dismiss the fragment
                    dismiss();
                //} else {
                    // Display an error message or take appropriate action
                    // This is just an example, adjust it based on your needs
                    // For now, just dismiss the fragment
                    //dismiss();
                //}
            }
        });
        return view;
    }
}
