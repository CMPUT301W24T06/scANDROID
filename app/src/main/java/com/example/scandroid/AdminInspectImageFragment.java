package com.example.scandroid;

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

public class AdminInspectImageFragment extends DialogFragment {
    private final DBAccessor database = new DBAccessor();
    String userID;
    String eventID;
    Bitmap picture;

    public AdminInspectImageFragment(Bitmap picture){
        this.picture = picture;
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
            if (bundle.getString("userID") != null){
                userID = bundle.getString("userID");
                removeButton.setOnClickListener(v -> database.deleteUserProfileImage(userID));

            } else if (bundle.getString("eventID") != null){
                eventID = bundle.getString("eventID");
                removeButton.setOnClickListener(v -> database.deleteEventPoster(eventID));
            }

        }
        return view;
    }
}
