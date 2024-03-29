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
 * A dialog asking users if they wish to remove their profile picture/event poster or select an image
 * from their gallery as their new profile picture/event poster
 */
public class AllowAccessCameraRollFragment extends DialogFragment {
    String ID;
    DBAccessor database = new DBAccessor();
    Bitmap pictureBitmap;
    ImageView posterOrProfileImageView;
    String accessType;

    //OpenAI, 2024, ChatGPT, How to access and select images from gallery
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    handleImageSelection(result);
                }
            });

    /**
     * Default constructor for AllowAccessCameraRollFragment
     */
    public AllowAccessCameraRollFragment() {

    }

    /**
     * Called to create the view for this fragment, using the given layout inflater
     * and container. Initializes views and handles UI interactions such as the back and remove
     * buttons.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.choose_image_fragment, container, false);
        Button backButton = view.findViewById(R.id.back_arrow);
        Button choosePicture = view.findViewById(R.id.camera_roll_access_button);
        Button removePicture = view.findViewById(R.id.remove_picture_button);
        assert getArguments() != null;
        ID = getArguments().getString("ID"); //User or event ID
        int viewID = getArguments().getInt("viewID"); //Image view ID from create event or user profile activities
        accessType = getArguments().getString("type"); //String to inform whether event poster or profile picture
        posterOrProfileImageView = findImageView(viewID);

        removePicture.setOnClickListener(v -> {
            if (Objects.equals(accessType, "user")) {
                database.accessUser(ID, user -> {
                    String name = getArguments().getString("username");
                    //Generate new profile picture since user will have no picture after removing their current one
                    pictureBitmap = new ProfilePictureGenerator().generatePictureBitmap(name);
                    posterOrProfileImageView.setImageBitmap(pictureBitmap);
                });
            } else {
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
     * Creates an AllowAccessCameraRollFragment and allows the transfer of user/event IDs between edit profile page and create
     * event page
     * @param ID String of the event or the user ID
     * @param viewID ID of the image view of the event poster or the user profile picture
     * @param type String that informs whether this fragment is for an event poster or a user profile picture
     * @param username String of the user's name. Used for the random profile picture generator
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

    /**
     * Handle the user's selected image from gallery, storing it in the database and setting the
     * corresponding ImageView's bitmap
     * @param imageUri Selected image from gallery
     */
    //OpenAI, 2024, ChatGPT, How to access and select images from gallery
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

    /**
     * Find the corresponding ImageView from the calling activity
     * @param viewID ID of a view in the activity
     * @return The host activity ImageView of user profile picture or event poster
     */
    public ImageView findImageView(int viewID){
        Activity hostActivity = getActivity();
        if (hostActivity != null) {
            // Access view in the hosting Activity
            return hostActivity.findViewById(viewID);
        }
        return null;
    }
}

