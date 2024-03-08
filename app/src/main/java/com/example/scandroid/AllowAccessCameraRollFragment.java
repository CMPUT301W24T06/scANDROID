package com.example.scandroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresExtension;
import androidx.fragment.app.DialogFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A dialog asking users if they wish to remove their profile picture or select an
 * from their gallery as their new profile picture
 */
public class AllowAccessCameraRollFragment extends DialogFragment {
    ActivityResultLauncher<Intent> launcher;
    String userID;
    DBAccessor database = new DBAccessor();
    Bitmap profilePic;
    private OnImageChangedListener imageChangedListener;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    public AllowAccessCameraRollFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the activity implements the OnImageChangedListener interface
        if (context instanceof OnImageChangedListener) {
            imageChangedListener = (OnImageChangedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnImageChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_image_fragment, container, false);
        Button backButton = view.findViewById(R.id.back_arrow);
        Button choosePicture = view.findViewById(R.id.camera_roll_access_button);
        Button removePicture = view.findViewById(R.id.remove_picture_button);
        registerResult();
        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
            removePicture.setOnClickListener(v -> {
                database.accessUser(userID, user -> {
                    String name = user.getUserName();
                    profilePic = new ProfilePictureGenerator().generatePictureBitmap(name);
                    database.storeUserProfileImage(userID, profilePic);
                    imageChangedListener.onImageChanged(profilePic);
                    dismiss();
                });
            });
        }
        choosePicture.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
                pickImage();
            } else {
                pickImageLegacy();
            }
            dismiss();
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
    public static AllowAccessCameraRollFragment newInstance(String userID) {
        AllowAccessCameraRollFragment fragment = new AllowAccessCameraRollFragment();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Interface for communication between AllowAccessCameraRollFragment and any activities that show profile picture
     * Communication allows for updating the profile picture ImageView
     *
     * @author Simon Thang
     */
    //OpenAI, 2024, ChatGPT, Change ImageView in an activity from a fragment called by it
    public interface OnImageChangedListener {
        /**
         * Passes the new bitmap profile picture to activities
         *
         * @param bitmap The new bitmap profile picture
         */
        void onImageChanged(Bitmap bitmap);
    }

    public void setImageChangedListener(OnImageChangedListener listener) {
        this.imageChangedListener = listener;
    }

//    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    // Get the selected image URI
//                    Uri selectedImageUri = result.getData().getData();
//                    //profilePic = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
//                    // imageChangedListener.onImageChanged(profilePic);
//                    //database.storeUserProfileImage(userID, profilePic);
//                    InputStream inputStream;
//                    try {
//                        inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
//                    } catch (FileNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Drawable drawable = Drawable.createFromStream(inputStream, selectedImageUri.toString());
//
//                    // Convert Drawable to Bitmap
//                    profilePic = BitmapFactory.decodeStream(inputStream);
//
//                    // Notify the listener about the image change
//                    imageChangedListener.onImageChanged(profilePic);
//
//                    // Close the InputStream
//                    try {
//                        assert inputStream != null;
//                        inputStream.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Drawable posterDrawable = Drawable.createFromStream(inputStream, selectedImageUri.toString());
//                    profilePic = BitmapFactory.decodeStream(inputStream);
//                    imageChangedListener.onImageChanged(profilePic);
//
//                }
//            });

    /**
     * Creates a Bitmap object from a Drawable object
     *
     * @param drawable Drawable object that is being converted
     * @return Returns a Bitmap object created from parameters of the Drawable object
     */
    //Source: https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Retrieves the image user selected from their gallery
     */
    //Source: https://www.youtube.com/watch?v=nOtlFl1aUCw
    private void registerResult() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        assert result.getData() != null;
                        Uri imageUri = result.getData().getData();
                        assert imageUri != null;
                        ImageView profile = requireView().findViewById(R.id.profile_image);
                        profile.setImageURI(imageUri);

                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    private void pickImageLegacy() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }
}

