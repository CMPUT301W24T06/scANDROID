package com.example.scandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * UsersArrayAdapter is an ArrayAdapter implementation used in the app for displaying
 * a list of users
 */
public class UsersArrayAdapter extends ArrayAdapter<String> {

    /**
     * Constructs a new UsersArrayAdapter
     *
     * @param context context where the adapter is being used
     * @param userIDs list of user IDs to display
     */
    public UsersArrayAdapter(Context context, ArrayList<String> userIDs) {
        super(context,0, userIDs);
    }

    /**
     * Gets a View that displays the data of a specific user in the data set
     *
     * @param position position of the item within the adapter's data set
     * @param convertView old view to reuse (if possible)
     * @param parent parent that this view will eventually be attached to
     * @return a View that corresponds to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_content, parent, false);
        }
        else {
            view = convertView;
        }

        TextView userNameText = view.findViewById(R.id.user_list_content_name);
        ImageView userProfilePicture = view.findViewById(R.id.user_content_profile_picture);
        String userID = getItem(position);
        assert userID != null;
        DBAccessor database = new DBAccessor();
        database.accessUser(userID, user -> {
            String userName = user.getUserName();
            userNameText.setText(userName);
        });

        database.accessUserProfileImage(userID, new BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                userProfilePicture.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e) {
                Toast.makeText(view.getContext(), "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
