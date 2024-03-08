package com.example.scandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.InputStream;

/**
 * A dialog asking users if they wish to remove their profile picture or select an
 * from their gallery as their new profile picture
 */
public class AllowAccessCameraRollFragment extends DialogFragment {
    String userID;
    DBAccessor database = new DBAccessor();
    Bitmap profilePic;
    ImageView profileImageView;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    handleImageSelection(result);
                }
            });

    public AllowAccessCameraRollFragment() {
        // Required Empty Constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.choose_image_fragment, container, false);
        Button backButton = view.findViewById(R.id.back_arrow);
        Button choosePicture = view.findViewById(R.id.camera_roll_access_button);
        Button removePicture = view.findViewById(R.id.remove_picture_button);
        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
            int viewID = getArguments().getInt("viewID");
            profileImageView = findImageView(viewID);

            removePicture.setOnClickListener(v -> {
                database.accessUser(userID, user -> {
                    String name = user.getUserName();
                    profilePic = new ProfilePictureGenerator().generatePictureBitmap(name);
                    database.storeUserProfileImage(userID, profilePic);
                    profileImageView.setImageBitmap(profilePic);
                    dismiss();
                });
            });
        }
        choosePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch("image/*");

        });
        backButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    /**
     * Creates an AllowAccessCameraRollFragment and allows the transfer of user IDs between edit profile page
     *
     * @param userID String of the user's ID
     * @return Returns the AllowAccessCameraRollFragment
     */
    public static AllowAccessCameraRollFragment newInstance(String userID, int viewID) {
        AllowAccessCameraRollFragment fragment = new AllowAccessCameraRollFragment();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        args.putInt("viewID", viewID);
        fragment.setArguments(args);
        return fragment;
    }

    private void handleImageSelection(Uri imageUri) {

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (profileImageView != null) {
                // Update the ImageView directly
                profileImageView.setImageBitmap(bitmap);
                Log.d("EditProfileActivity", "ImageView updated successfully");

                if (bitmap != null) {
                    // Update the backend with the new image
                    DBAccessor database = new DBAccessor();
                    database.storeUserProfileImage(userID, bitmap);
                } else {
                    Log.e("EditProfileActivity", "Bitmap is null");
                }
            } else {
                Log.e("EditProfileActivity", "ImageView is null");
            }

            // Optionally close the InputStream
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("EditProfileActivity", "Error converting image to bitmap: " + e.getMessage());
            Toast.makeText(requireContext(), "Error converting image to bitmap", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }
    public ImageView findImageView(int viewID){
        Activity hostActivity = getActivity();
        if (hostActivity != null) {
            // Access views or perform actions in the hosting Activity
            return hostActivity.findViewById(viewID);
        }
        return null;
    }
}

