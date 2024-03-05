package com.example.scandroid;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AdminKeyFragment extends DialogFragment {

    public AdminKeyFragment() {
    }

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
