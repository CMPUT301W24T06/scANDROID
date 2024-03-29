package com.example.scandroid;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * AdminInspectImageFragment is shown when a user clicks on a profile picture or event poster while
 * browsing events and users. Admins are given the option to remove and delete those images.
 */
public class AdminInspectImageFragment extends DialogFragment {
    private final DBAccessor database = new DBAccessor();
    String userID;
    String eventID;
    Bitmap picture;
    onClickListener listener;

    /**
     * Constructor for AdminInspectImageFragment
     * @param picture Event poster or user profile picture that was clicked on
     * @param listener Listener to communicate with activity that called this fragment
     */
    public AdminInspectImageFragment(Bitmap picture, onClickListener listener){
        this.picture = picture;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_inspect_image_fragment, container, false);
        Button cancelButton = view.findViewById(R.id.cancel_image_inspect_button);
        Button removeButton = view.findViewById(R.id.remove_image_button);
        ImageView imageView = view.findViewById(R.id.fetch_image);

        cancelButton.setOnClickListener(v -> dismiss());
        userID = new DeviceIDRetriever(requireContext()).getDeviceId();
        database.accessUser(userID, user -> {
            if (!user.getHasAdminPermissions()){
                removeButton.setVisibility(View.INVISIBLE);
            }
        });
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            imageView.setImageBitmap(picture);
            //If this fragment was created from clicking on a profile picture
            if (bundle.getString("userID") != null){
                userID = bundle.getString("userID");
                removeButton.setOnClickListener(v -> {
                        database.deleteUserProfileImage(userID);
                        listener.onClick(); //Alert calling activity that remove button has been pressed
                        dismiss();
            });
            //If this fragment was created from clicking on an event poster
            } else if (bundle.getString("eventID") != null){
                eventID = bundle.getString("eventID");
                removeButton.setOnClickListener(v -> {
                        database.deleteEventPoster(eventID);
                        listener.onClick(); //Alert calling activity that remove button has been pressed
                        dismiss();
                });
            }

        }

        return view;
    }
}
