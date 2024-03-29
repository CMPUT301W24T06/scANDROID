package com.example.scandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * AdminInspectUserFragment is shown when a user with admin permissions browses and clicks on an user profile
 * Allows the admin user to view the selected user's name, email, phone number, and about me.
 * The option to remove the selected user's profile from the app is present as well.
 */
public class AdminInspectUserFragment extends DialogFragment {
    private final DBAccessor database = new DBAccessor();
    String userID;
    onClickListener listener;
    AdminInspectUserFragment(onClickListener listener){
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_inspect_user_profile_fragment, container, false);
        TextView userNameText = view.findViewById(R.id.fetch_user_name);
        TextView emailText = view.findViewById(R.id.fetch_user_email);
        TextView phoneNumberText = view.findViewById(R.id.fetch_user_phone_number);
        TextView userAboutMeText = view.findViewById(R.id.fetch_user_about_me);
        Button cancelButton = view.findViewById(R.id.cancel_user_inspect_button);
        Button removeButton = view.findViewById(R.id.remove_user_button);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userID = bundle.getString("userID");
            database.accessUser(userID, user -> {
                if (user != null) {
                    //Fill out the selected user's profile details
                    userNameText.setText(user.getUserName());
                    emailText.setText(user.getUserEmail());
                    phoneNumberText.setText(user.getUserPhoneNumber());
                    userAboutMeText.setText(user.getUserAboutMe());
                    cancelButton.setOnClickListener(v -> dismiss());
                    removeButton.setOnClickListener(v -> {
                        database.deleteUser(userID);
                        listener.onClick(); //Alert calling activity about a removal click
                        dismiss();
                    });
                } else {
                    dismiss();
                }
            });
        }
        return view;
    }
}
