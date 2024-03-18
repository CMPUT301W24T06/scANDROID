package com.example.scandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A dialog asking users if they wish to remove their profile picture or select an
 * from their gallery as their new profile picture
 */
public class AllowAccessCameraRollFragment extends DialogFragment {
    String ID;
    DBAccessor database = new DBAccessor();
    Bitmap pictureBitmap;
    ImageView posterOrProfileImageView;
    String accessType;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    handleImageSelection(result);
                }
            });

    public AllowAccessCameraRollFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.choose_image_fragment, container, false);
        Button backButton = view.findViewById(R.id.back_arrow);
        Button choosePicture = view.findViewById(R.id.camera_roll_access_button);
        Button removePicture = view.findViewById(R.id.remove_picture_button);
        assert getArguments() != null;
        ID = getArguments().getString("ID");
        int viewID = getArguments().getInt("viewID");
        accessType = getArguments().getString("type");
        posterOrProfileImageView = findImageView(viewID);

        removePicture.setOnClickListener(v -> {
            if (Objects.equals(accessType, "user")) {
                database.accessUser(ID, user -> {
                    String name = getArguments().getString("username");
                    pictureBitmap = new ProfilePictureGenerator().generatePictureBitmap(name);
                    database.storeUserProfileImage(ID, pictureBitmap);
                    posterOrProfileImageView.setImageBitmap(pictureBitmap);
                });
            }
            else {
                database.deleteEventPoster(ID);
                Drawable defaultPoster = ResourcesCompat.getDrawable(getResources(), R.drawable.add_poster_icon, requireContext().getTheme());
                posterOrProfileImageView.setImageDrawable(defaultPoster);
            }
            dismiss();
        });

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
     * @param ID String of the user's ID
     * @return Returns the AllowAccessCameraRollFragment
     */
    public static AllowAccessCameraRollFragment newInstance(String ID, int viewID, String type, String username) {
        AllowAccessCameraRollFragment fragment = new AllowAccessCameraRollFragment();
        Bundle args = new Bundle();
        args.putString("ID", ID);
        args.putInt("viewID", viewID);
        args.putString("type", type);
        if (Objects.equals(type, "user")){
            args.putString("username", username);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private void handleImageSelection(Uri imageUri) {

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (posterOrProfileImageView != null) {
                // Update the ImageView directly
                posterOrProfileImageView.setImageBitmap(bitmap);
                Log.d("AllowAccessCameraFragment", "ImageView updated successfully");

                if (bitmap != null) {
                    // Update the backend with the new image
                    DBAccessor database = new DBAccessor();
                    if (Objects.equals(accessType, "user")){
                        database.storeUserProfileImage(ID, bitmap);
                    } else {
                        database.storeEventPoster(ID, bitmap);
                    }

                } else {
                    Log.e("AllowAccessCameraFragment", "Bitmap is null");
                }
            } else {
                Log.e("AllowAccessCameraFragment", "ImageView is null");
            }

            // Optionally close the InputStream
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AllowAccessCameraFragment", "Error converting image to bitmap: " + e.getMessage());
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

